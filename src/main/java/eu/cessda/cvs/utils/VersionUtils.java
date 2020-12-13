package eu.cessda.cvs.utils;

import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionUtils {
    public static final String CODE_DEF = "Code Def:";
    public static final String CODE_TERM = "Code Term:";
    public static final String CODE = "Code:";
    public static final String CV_NAME = "Cv Name:";
    public static final String CV_DEF = "Cv Def:";
    public static final String CV_NOTES = "Cv Notes:";

    private VersionUtils() {}

	public static int compareVersion(String v1, String v2) {
        String s1 = normalisedVersion(v1);
        String s2 = normalisedVersion(v2);
        return s1.compareTo(s2);
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        final String format = "%" + maxWidth + "s";
        for (String s : split) {
            sb.append(String.format(format, s));
        }
        return sb.toString();
    }

    public static String increaseSlVersionByOne( String prevVersionNumber ) {
        int indexAfterLastDot = prevVersionNumber.lastIndexOf('.') + 1;
        return prevVersionNumber.substring(0, indexAfterLastDot) +
            ( Integer.parseInt( prevVersionNumber.substring(indexAfterLastDot) ) + 1 );
    }

    public static String increaseTlVersionByOne( String prevVersionNumber, String currentSlNumber ) {
        int indexAfterLastDot = prevVersionNumber.lastIndexOf('.') + 1;
        if ( VersionUtils.compareVersion( prevVersionNumber, currentSlNumber ) == -1)
            return currentSlNumber + ".1";
        else
            return prevVersionNumber.substring(0, indexAfterLastDot) +
                ( Integer.parseInt( prevVersionNumber.substring(indexAfterLastDot) ) + 1 );
    }

    public static String getSlNumberFromTl( String tlNumber ) {
        if( StringUtils.countMatches( tlNumber, ".") == 2){
            tlNumber = tlNumber.substring( 0, tlNumber.lastIndexOf('.'));
        }
        return tlNumber;
    }

    /**
     * Spit language version e.g. de-2.0.1 into several part
     * @param languageVersion
     * @return array with 4 properties e.g. from example above "2.0", "2.0.2", "de", "TL"
     */
    public static String[] splitLanguageVersion( String languageVersion ) {
        final int lastIndexOfSpace = languageVersion.lastIndexOf('-');
        final String language = languageVersion.substring(0, lastIndexOfSpace);
        final String versionNumber = languageVersion.substring( lastIndexOfSpace + 1 );
        String itemType = "SL";
        String slNumber = versionNumber;
        if( StringUtils.countMatches( versionNumber, ".") == 2){
            itemType = "TL";
            slNumber = versionNumber.substring( 0, versionNumber.lastIndexOf('.'));
        }
	    return new String[]{slNumber, versionNumber, language, itemType};
    }

    public static String getBaseVersionUri( String availableVersionUri, String notation ) {
        return availableVersionUri.substring(0, availableVersionUri.indexOf( "/" + notation ));
    }

    public static List<String> buildComparisonCurrentAndPreviousCV(VersionDTO versionDTO, VersionDTO prevVersionDTO) {
        // create comparison based on current version
        StringBuilder currentVersionCvSb = new StringBuilder();
        StringBuilder prevVersionCvSb = new StringBuilder();

        currentVersionCvSb.append( CV_NAME + " " + versionDTO.getTitle() + "\n");
        currentVersionCvSb.append( CV_DEF + " " + versionDTO.getDefinition() + "\n");
        currentVersionCvSb.append( CV_NOTES + " " + (versionDTO.getNotes() == null ? "": versionDTO.getNotes()) + "\n\n\n");

        prevVersionCvSb.append( CV_NAME + " " + prevVersionDTO.getTitle() + "\n");
        prevVersionCvSb.append( CV_DEF + " " + prevVersionDTO.getDefinition() + "\n");
        prevVersionCvSb.append( CV_NOTES + " " + (prevVersionDTO.getNotes() == null ? "": prevVersionDTO.getNotes()) + "\n\n\n");

        // get concepts and sorted by position
        List<ConceptDTO> currentConcepts = versionDTO.getConcepts().stream()
            .sorted(Comparator.comparing(ConceptDTO::getPosition)).collect(Collectors.toList());
        Set<ConceptDTO> existingConceptsInPrevAndCurrent = new HashSet<>();
        currentConcepts.forEach(currentConcept -> {
            currentVersionCvSb.append( CODE + " " + currentConcept.getNotation()+ "\n");
            currentVersionCvSb.append( CODE_TERM + " " + currentConcept.getTitle() + "\n");
            currentVersionCvSb.append( CODE_DEF + " " + currentConcept.getDefinition() + "\n\n");

            ConceptDTO prevConceptDTO = null;

            if( currentConcept.getPreviousConcept() != null )
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getPreviousConcept().equals( prevConcept.getId())).findFirst()
                    .orElse(null);

            if( prevConceptDTO == null ) {
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getNotation().equals( prevConcept.getNotation())).findFirst()
                    .orElse(null);
            }

            if ( prevConceptDTO == null ) {
                prevVersionCvSb.append(CODE + " \n");
                prevVersionCvSb.append(CODE_TERM + " \n");
                prevVersionCvSb.append(CODE_DEF + " \n\n");
            } else {
                existingConceptsInPrevAndCurrent.add( prevConceptDTO );
                prevVersionCvSb.append( CODE + " " + prevConceptDTO.getNotation()+ "\n");
                prevVersionCvSb.append( CODE_TERM + " " + prevConceptDTO.getTitle() + "\n");
                prevVersionCvSb.append( CODE_DEF + " " + prevConceptDTO.getDefinition() + "\n\n");
            }
        });

        // put deleted concept at the end
        prevVersionDTO.getConcepts().removeAll(existingConceptsInPrevAndCurrent);

        for (ConceptDTO prevConcept : prevVersionDTO.getConcepts()) {
            currentVersionCvSb.append(CODE + " \n");
            currentVersionCvSb.append(CODE_TERM + " \n");
            currentVersionCvSb.append(CODE_DEF + " \n\n");
            prevVersionCvSb.append( CODE + " " + prevConcept.getNotation()+ "\n");
            prevVersionCvSb.append( CODE_TERM + " " + prevConcept.getTitle() + "\n");
            prevVersionCvSb.append( CODE_DEF + " " + prevConcept.getDefinition() + "\n\n");
        }

        List<String> compareCurrentPrev = new ArrayList<>();
        compareCurrentPrev.add( prevVersionCvSb.toString() );
        compareCurrentPrev.add( currentVersionCvSb.toString() );
        return compareCurrentPrev;
    }
}
