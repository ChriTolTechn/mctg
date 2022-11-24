package bif3.tolan.swe1.mcg.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("----------------------------------------------");
            System.out.println("Server started...");
            System.out.println("----------------------------------------------");
            while (true) {
                Socket clientConnection = serverSocket.accept();
                executorService.execute(new HttpRequestHandler(clientConnection));
            }
        }
    }
}
