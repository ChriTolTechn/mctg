package bif3.tolan.swe1.mctg.constants;

/**
 * Regex patterns used throughout the API
 *
 * @author Christopher Tolan
 */
public final class CommonRegex {
    public static final String SPLIT_STRING_BY_UPPERCASE_LETTERS = "(?=[A-Z])";
    public static final String USERNAME_FROM_TOKEN = "^Basic (.*)-mtcgToken$";
    public final static String PARAMETER_SPLITTER = "&";
    public final static String PARAMETER_KEY_VALUE_SPLITTER = "=";
    public final static String HEADER_KEY_VALUE_SPLITTER = ":";
    public final static String PATH_METHOD_SPLITTER = " ";
    public final static String PATH_REQUEST_SPLITTER = "\\?";
    public final static String PARAMETER_IDENTIFIER = "?";
    public final static String PATH_SPLITTER = "/";
}
