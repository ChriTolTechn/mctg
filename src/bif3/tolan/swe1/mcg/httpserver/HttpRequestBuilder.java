package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.constants.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestBuilder {

    /**
     * Builds a HTTP request
     *
     * @param bufferedReader BufferedReader containing the whole httpRequest
     * @return Build request
     * @throws IOException
     */
    public HttpRequest buildRequest(BufferedReader bufferedReader) throws IOException {
        HttpRequest request = new HttpRequest();
        String firstLine = bufferedReader.readLine();

        if (firstLine != null) {
            String[] splittedFirstLine = firstLine.split(CommonRegex.PATH_METHOD_SPLITTER);
            String rawMethodString = splittedFirstLine[0];
            String rawPathName = splittedFirstLine[1];

            request.setMethod(extractMethod(rawMethodString));

            boolean hasParams = rawPathName.contains(CommonRegex.PARAMETER_IDENTIFIER);
            if (hasParams) {
                String[] splittedPathString = rawPathName.split(CommonRegex.PATH_REQUEST_SPLITTER);
                request.setPathArray(extractPath(splittedPathString[0]));
                request.setParameterMap(extractParams(splittedPathString[1]));
            } else {
                request.setPathArray(extractPath(rawPathName));
                request.setParameterMap(new HashMap<>());
            }

            request.setHeaderMap(extractHeaders(bufferedReader));
            request.setContentLength(extractContentLength(request.getHeaderMap()));
        }

        if (request.getContentLength() > 0) {
            request.setBody(extractBody(request.getContentLength(), bufferedReader));
        }

        return request;
    }

    /**
     * Extracts all headers provided by the buffered reader
     *
     * @param bufferedReader bufferedReader that has all the header information
     * @return Map of headers
     * @throws IOException
     */
    private Map<String, String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        HashMap<String, String> headers = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            String[] headerRow = line.split(CommonRegex.HEADER_KEY_VALUE_SPLITTER, 2);
            headers.put(headerRow[0], headerRow[1].trim());
            line = bufferedReader.readLine();
        }

        return headers;
    }

    /**
     * Extracts the content-length from a header map
     *
     * @param headers header map which contains the content-length as a string
     * @return content-length as integer value
     */
    private int extractContentLength(Map<String, String> headers) {
        if (headers.containsKey(ServerConstants.KEY_CONTENT_LENGTH)) {
            return Integer.parseInt(headers.get(ServerConstants.KEY_CONTENT_LENGTH));
        }
        return 0;
    }

    /**
     * Reads the body from a bufferedReader for the provided contentLength
     *
     * @param contentLength  length of the body
     * @param bufferedReader bufferedReader that has only the body left to read from it
     * @return The body of the request as string
     * @throws IOException
     */
    private String extractBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] charBuffer = new char[contentLength];
        bufferedReader.read(charBuffer, 0, contentLength);

        return String.valueOf(charBuffer);
    }

    /**
     * Converts a method string into an enum
     *
     * @param rawMethodString method as string
     * @return Method as enum
     */
    private Method extractMethod(String rawMethodString) {
        return Method.valueOf(rawMethodString.toUpperCase());
    }

    /**
     * Extracts parameter from a raw parameter string and puts them into a map
     *
     * @param rawParameterString string of raw parameters
     * @return Map of parameters
     */
    private Map<String, String> extractParams(String rawParameterString) {

        List<String> splitParams = Arrays.stream(rawParameterString.split(CommonRegex.PARAMETER_SPLITTER)).toList();
        HashMap<String, String> parameterMap = new HashMap<>();

        for (String parameter : splitParams) {
            String[] keyValue = parameter.split(CommonRegex.PARAMETER_KEY_VALUE_SPLITTER);
            if (keyValue.length == 2) {
                parameterMap.put(keyValue[0], keyValue[1]);
            }
        }

        return parameterMap;
    }

    /**
     * Extracts parameters as list
     *
     * @param path all parameters as string
     * @return List of parameters
     */
    private String[] extractPath(String path) {
        String[] splittedPath = path
                .replaceFirst(CommonRegex.PATH_SPLITTER, "")
                .split(CommonRegex.PATH_SPLITTER);
        return splittedPath;
    }
}
