package eu.cessda.cvs.utils;

import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionUtils {
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
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
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

    /**
     * Spit language version e.g. de-2.0.1 into several part
     * @param languageVersion
     * @return array with 4 properties e.g. from example above "2.0", "2.0.2", "de", "TL"
     */
    public static String[] splitLanguageVersion( String languageVersion ) {
        final int lastIndexOfSpace = languageVersion.lastIndexOf("-");
        final String language = languageVersion.substring(0, lastIndexOfSpace);
        final String versionNumber = languageVersion.substring( lastIndexOfSpace + 1 );
        String itemType = "SL";
        String slNumber = versionNumber;
        if( StringUtils.countMatches( versionNumber, ".") == 2){
            itemType = "TL";
            slNumber = versionNumber.substring( 0, versionNumber.lastIndexOf("."));
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

        currentVersionCvSb.append( "Cv Name: " + versionDTO.getTitle() + "\n");
        currentVersionCvSb.append( "Cv Def: " + versionDTO.getDefinition() + "\n");
        currentVersionCvSb.append( "Cv Notes: " + versionDTO.getNotes() + "\n\n\n");

        prevVersionCvSb.append( "Cv Name: " + prevVersionDTO.getTitle() + "\n");
        prevVersionCvSb.append( "Cv Def: " + prevVersionDTO.getDefinition() + "\n");
        prevVersionCvSb.append( "Cv Notes: " + prevVersionDTO.getNotes() + "\n\n\n");

        // get concepts and sorted by position
        List<ConceptDTO> currentConcepts = versionDTO.getConcepts().stream()
            .sorted(Comparator.comparing(ConceptDTO::getPosition)).collect(Collectors.toList());
        Set<ConceptDTO> existingConceptsInPrevAndCurrent = new HashSet<>();
        currentConcepts.forEach(currentConcept -> {
            currentVersionCvSb.append( "Code: " + currentConcept.getNotation()+ "\n");
            currentVersionCvSb.append( "Code Term: " + currentConcept.getTitle() + "\n");
            currentVersionCvSb.append( "Code Def: " + currentConcept.getDefinition() + "\n\n");

            ConceptDTO prevConceptDTO = null;

            if( currentConcept.getPreviousConcept() != null )
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getPreviousConcept().equals( prevConcept.getId())).findFirst()
                    .orElse(null);

            if ( prevConceptDTO == null ) {
                prevVersionCvSb.append( "Code: \n");
                prevVersionCvSb.append( "Code Term: \n");
                prevVersionCvSb.append( "Code Def: \n\n");
            } else {
                existingConceptsInPrevAndCurrent.add( prevConceptDTO );
                prevVersionCvSb.append( "Code: " + prevConceptDTO.getNotation()+ "\n");
                prevVersionCvSb.append( "Code Term: " + prevConceptDTO.getTitle() + "\n");
                prevVersionCvSb.append( "Code Def: " + prevConceptDTO.getDefinition() + "\n\n");
            }
        });

        // put deleted concept at the end
        prevVersionDTO.getConcepts().removeAll(existingConceptsInPrevAndCurrent);

        for (ConceptDTO prevConcept : prevVersionDTO.getConcepts()) {
            currentVersionCvSb.append( "Code: \n");
            currentVersionCvSb.append( "Code Term: \n");
            currentVersionCvSb.append( "Code Def: \n\n");
            prevVersionCvSb.append( "Code: " + prevConcept.getNotation()+ "\n");
            prevVersionCvSb.append( "Code Term: " + prevConcept.getTitle() + "\n");
            prevVersionCvSb.append( "Code Def: " + prevConcept.getDefinition() + "\n\n");
        }

        List<String> compareCurrentPrev = new ArrayList<>();
        compareCurrentPrev.add( prevVersionCvSb.toString() );
        compareCurrentPrev.add( currentVersionCvSb.toString() );
        return compareCurrentPrev;
    }
}
