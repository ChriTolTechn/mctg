package bif3.tolan.swe1.mcg.httpserver.enums;

public enum HttpContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    public final String type;

    HttpContentType(String type) {
        this.type = type;
    }

}
