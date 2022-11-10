package bif3.tolan.swe1.mcg.httpserver;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket clientConnection;

    public RequestHandler(Socket socket) {
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

            Request request = new RequestBuilder().buildRequest(bufferedReader);

            String response = "";

            ////////DEMO
            if (request.getMethod() == Method.GET && request.getParams().containsKey("token") && request.getParams().get("token").equals("test")) {
                response = new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Hi").get();
            } else {
                response = new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "UNAUTHORIZED").get();
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
