package bif3.tolan.swe1.mcg.httpserver;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private Method method;
    private String[] pathArray;
    private Map<String, String> parameterMap = new HashMap<>();
    private Map<String, String> headerMap = new HashMap<>();
    private String body;

    private int contentLength = 0;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String[] getPathArray() {
        return pathArray;
    }

    public void setPathArray(String[] pathArray) {
        this.pathArray = pathArray;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap = parameterMap;
    }
}