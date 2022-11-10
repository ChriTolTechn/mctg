package bif3.tolan.swe1.mcg.httpserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private Method method;
    private String pathname;
    private Map<String, String> params = new HashMap<>();
    private Map<String, String> headerMap = new HashMap<>();
    private String body;

    private int contentLength = 0;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public String addHeader(String key, String value) {
        if (key != null && key.equals("Content-Length")) {
            this.contentLength = Integer.parseInt(value);
        }
        return headerMap.put(key, value);
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

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(String params) {
        if (params == null) {
            this.params = new HashMap<String, String>();
            return;
        }

        var splitParams = Arrays.stream(params.split("&")).toList();
        var parameterMap = new HashMap<String, String>();

        for (String parameter : splitParams) {
            var keyValue = parameter.split("=");
            if (keyValue.length == 2) {
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        }

        this.params = parameterMap;
    }
}

