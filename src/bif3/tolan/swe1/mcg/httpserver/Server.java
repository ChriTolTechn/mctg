package bif3.tolan.swe1.mcg.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(10001);
        server.start();
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
                executorService.execute(new RequestHandler(clientConnection));
            }
        }
    }
}
