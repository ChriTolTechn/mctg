package bif3.tolan.swe1.mcg.httpserver;

public enum HttpStatus {
    OK(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized");

    public final int code;
    public final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
