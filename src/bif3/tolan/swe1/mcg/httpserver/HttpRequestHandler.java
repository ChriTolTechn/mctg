package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.workers.Workable;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class HttpRequestHandler implements Runnable {
    private Socket clientConnection;

    private Map<String, Workable> workers;

    public HttpRequestHandler(Socket socket, Map<String, Workable> workers) {
        this.clientConnection = socket;
        this.workers = workers;
    }

    /**
     * Main method that will create the request, execute it and return a response
     */
    @Override
    public void run() {

        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try {
            // Setup bufferedReader
            InputStream inputStream = clientConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            // Build request
            HttpRequest request = new HttpRequestBuilder().buildRequest(bufferedReader);

            // Execute request and save response
            String response = executeRequest(request).get();

            // Setup printWriter
            OutputStream outputStream = clientConnection.getOutputStream();
            printWriter = new PrintWriter(outputStream);

            // Write response
            printWriter.write(response);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    clientConnection.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Parses the request to a corresponding worker to execute the request
     *
     * @param request The request that needs to be executed
     * @return A response that describes the outcome of the execution
     */
    private HttpResponse executeRequest(HttpRequest request) {
        Workable correspondingWorker = workers.get(request.getPathArray()[0]);

        if (correspondingWorker != null) {
            return correspondingWorker.executeRequest(request);
        } else {
            return new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.PLAIN_TEXT, "Invalid path");
        }
    }
}
