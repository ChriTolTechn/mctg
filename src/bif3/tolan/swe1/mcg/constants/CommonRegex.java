package bif3.tolan.swe1.mcg.constants;

public final class CommonRegex {

    public static final String SPLIT_STRING_BY_UPPERCASE_LETTERS = "(?=[A-Z])";
    public static final String USERNAME_FROM_TOKEN = "^Basic (.*)-mtcgToken$";
    public static final String WIN_PERCENTAGE_DISPLAY_FORMAT = "%.1f%%";
    public final static String PARAMETER_SPLITTER = "&";
    public final static String PARAMETER_KEY_VALUE_SPLITTER = "=";
    public final static String HEADER_KEY_VALUE_SPLITTER = ":";
    public final static String PATH_METHOD_SPLITTER = " ";
    public final static String PATH_REQUEST_SPLITTER = "\\?";
    public final static String PARAMETER_IDENTIFIER = "?";
    public final static String PATH_SPLITTER = "/";
}
