package bif3.tolan.swe1.mcg.httpserver;

public enum ContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    public final String type;

    ContentType(String type) {
        this.type = type;
    }

}
