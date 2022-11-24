package bif3.tolan.swe1.mcg.httpserver;

public class HttpResponse {
    private int status;
    private String statusMessage;
    private String contentType;
    private String content;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String content) {
        this.status = httpStatus.code;
        this.statusMessage = httpStatus.message;
        this.contentType = contentType.type;
        this.content = content;
    }

    public String get() {
        return "HTTP/1.1 " + this.status + " " + this.statusMessage + "\r\n" +
                "Content-Type: " + this.contentType + "; charset=utf-8\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "Connection: Closed\r\n\r\n" + this.content;
    }

}
