package bif3.tolan.swe1.mctg.httpserver.enums;

/**
 * Content type enums used in HTTP responses
 *
 * @author Christopher Tolan
 */
public enum HttpContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    public final String type;

    HttpContentType(String type) {
        this.type = type;
    }

}
