package util;

public final class StringUtils {
    public static String joinStringPieces(String... pieces) {
        return String.join("\r\n", pieces);
    }

    public static String addStringBeforeAndAfter(String before, String original, String after){
        return String.format("%s%s%s", before, original, after);
    }

    public static String getLanguageForLocale(String language) {
        String[] parts = language.split("-");
        return parts[0];
    }

}
