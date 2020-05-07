package eu.cessda.cvs.utils;

import java.util.regex.Pattern;

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

    public static String getBaseVersionUri( String availableVersionUri, String notation ) {
        return availableVersionUri.substring(0, availableVersionUri.indexOf( "/" + notation ));
    }
}
