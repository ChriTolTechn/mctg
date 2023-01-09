package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;

public class HttpResponse {
    private int status;
    private String statusMessage;
    private String contentType;
    private String content;

    public HttpResponse(HttpStatus httpStatus, HttpContentType httpContentType, String content) {
        this.status = httpStatus.code;
        this.statusMessage = httpStatus.message;
        this.contentType = httpContentType.type;
        this.content = content;
    }

    public String get() {
        return "HTTP/1.1 " + this.status + " " + this.statusMessage + "\r\n" +
                "Content-Type: " + this.contentType + "; charset=utf-8\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "Connection: Closed\r\n\r\n" + this.content;
    }

}
