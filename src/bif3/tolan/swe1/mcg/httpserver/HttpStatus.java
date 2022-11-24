package bif3.tolan.swe1.mcg.httpserver;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),

    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    NOT_ACCEPTABLE(406, "Not acceptable"),
    BAD_REQUEST(400, "Bad Request");

    public final int code;
    public final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
