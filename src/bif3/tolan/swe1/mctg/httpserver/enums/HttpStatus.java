package bif3.tolan.swe1.mctg.httpserver.enums;

/**
 * HTTP Status codes for responses
 *
 * @author Christopher Tolan
 */
public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    NOT_ACCEPTABLE(406, "Not acceptable"),
    BAD_REQUEST(400, "Bad Request"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    GATEWAY_TIMEOUT(504, "Gateway timeout");

    public final int code;
    public final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
