/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.cessda.cvs.utils;

import eu.cessda.cvs.service.dto.ConceptDTO;
import eu.cessda.cvs.service.dto.VersionDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class VersionUtils {
    public static final String CODE_DEF = "Code Def:";
    public static final String CODE_TERM = "Code Term:";
    public static final String CODE = "Code:";
    public static final String CV_NAME = "Cv Name:";
    public static final String CV_DEF = "Cv Def:";
    public static final String CV_NOTES = "Cv Notes:";
    public static final String DEPRECATED_SUFFIX = " (DEPRECATED)";

    private VersionUtils() {}

	public static int compareVersion(String v1, String v2) {
        return VersionNumber.fromString(v1).compareTo(VersionNumber.fromString(v2));
    }

    public static String increaseSlVersionNumber(String versionNumber) {
        return VersionNumber.fromString(versionNumber).increaseMinorNumber().toString();
    }

    public static String increaseTlVersionByOne(String prevVersionNumber, String currentSlVersionNumber) {
        VersionNumber prev = VersionNumber.fromString(prevVersionNumber);
        VersionNumber curr = VersionNumber.fromString(currentSlVersionNumber);
        if (prev.compareTo(curr) < 0)
            return new VersionNumber(curr.getBasePatchVersion(), 1).toString();
        else
            return prev.increasePatchNumber().toString();
    }

    public static VersionNumber getSlVersionNumber( String tlNumber ) {
        return VersionNumber.fromString(tlNumber).getBasePatchVersion();
    }

    public static boolean equalSlVersionNumber(String versionNumber1, String versionNumber2) {
        return VersionNumber.fromString(versionNumber1).equalMinorVersionNumber(VersionNumber.fromString(versionNumber2));
    }

    /**
     * Split language version e.g. de-2.0.1 into several part
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

    public static void appendVersionToSb(StringBuilder sb, VersionDTO versionDTO) {
        sb.append(CV_NAME + " ").append(versionDTO.getTitle()).append("\n");
        sb.append(CV_DEF + " ").append(versionDTO.getDefinition()).append("\n");
        sb.append(CV_NOTES + " ").append(versionDTO.getNotes() == null ? "" : versionDTO.getNotes()).append("\n\n\n");
    }

    public static void appendConceptToSb(StringBuilder sb, ConceptDTO conceptDTO) {
        if (conceptDTO == null) {
            sb.append(CODE + " \n");
            sb.append(CODE_TERM + " \n");
            sb.append(CODE_DEF + " \n\n");
        } else {
            sb.append(CODE + " ").append(conceptDTO.getNotation()).append(conceptDTO.getDeprecated() ? DEPRECATED_SUFFIX : "").append("\n");
            sb.append(CODE_TERM + " ").append(conceptDTO.getTitle()).append(conceptDTO.getDeprecated() ? DEPRECATED_SUFFIX : "").append("\n");
            sb.append(CODE_DEF + " ").append(conceptDTO.getDefinition()).append(conceptDTO.getDeprecated() ? DEPRECATED_SUFFIX : "").append("\n\n");
        }
    }

    public static List<String> compareCurPrevCV(VersionDTO versionDTO, VersionDTO prevVersionDTO) {
        // create comparison based on current version
        StringBuilder currentVersionCvSb = new StringBuilder();
        StringBuilder prevVersionCvSb = new StringBuilder();

        appendVersionToSb(currentVersionCvSb, versionDTO);
        appendVersionToSb(prevVersionCvSb, prevVersionDTO);

        // get concepts and sorted by position
        List<ConceptDTO> currentConcepts = versionDTO.getConcepts().stream()
            .sorted(Comparator.comparing(ConceptDTO::getPosition)).collect(Collectors.toList());
        Set<ConceptDTO> existingConceptsInPrevAndCurrent = new HashSet<>();
        currentConcepts.forEach(currentConcept -> {
            appendConceptToSb(currentVersionCvSb, currentConcept);
            ConceptDTO prevConceptDTO = null;
            if( currentConcept.getPreviousConcept() != null ) {
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getPreviousConcept().equals( prevConcept.getId())).findFirst()
                    .orElse(null);
            }
            if( prevConceptDTO == null ) {
                prevConceptDTO = prevVersionDTO.getConcepts().stream()
                    .filter(prevConcept -> currentConcept.getNotation().equals( prevConcept.getNotation())).findFirst()
                    .orElse(null);
            } else {
                existingConceptsInPrevAndCurrent.add( prevConceptDTO );
            }
            appendConceptToSb(prevVersionCvSb, prevConceptDTO);
        });

        // put deleted concept at the end
        prevVersionDTO.getConcepts().removeAll(existingConceptsInPrevAndCurrent);

        for (ConceptDTO prevConcept : prevVersionDTO.getConcepts()) {
            appendConceptToSb(currentVersionCvSb, null);
            appendConceptToSb(prevVersionCvSb, prevConcept);
        }

        List<String> compareCurrentPrev = new ArrayList<>();
        compareCurrentPrev.add( prevVersionCvSb.toString() );
        compareCurrentPrev.add( currentVersionCvSb.toString() );
        return compareCurrentPrev;
    }
}
