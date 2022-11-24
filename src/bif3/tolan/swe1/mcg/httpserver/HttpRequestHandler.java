package bif3.tolan.swe1.mcg.httpserver;

import java.io.*;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {
    private Socket clientConnection;

    public HttpRequestHandler(Socket socket) {
        this.clientConnection = socket;
    }

    @Override
    public void run() {

        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        try {
            InputStream inputStream = clientConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            OutputStream outputStream = clientConnection.getOutputStream();
            printWriter = new PrintWriter(outputStream);

            HttpRequest request = new HttpRequestBuilder().buildRequest(bufferedReader);

            String response = "";

            ////////DEMO
            if (request.getMethod() == Method.GET
                    && request.getParams().containsKey("token")
                    && request.getParams().get("token").equals("test")) {
                response = new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Hi").get();
            } else {
                response = new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "UNAUTHORIZED").get();
            }
            ////////

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
}
