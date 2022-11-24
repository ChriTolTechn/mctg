package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.workers.UserWorker;
import bif3.tolan.swe1.mcg.workers.Workable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private int port;

    private Map<String, Workable> workers;

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(ServerConstants.DEFAULT_THREAD_COUNT);

        initializeWorkers();

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("----------------------------------------------");
            System.out.println("Server started...");
            System.out.println("----------------------------------------------");
            while (true) {
                Socket clientConnection = serverSocket.accept();
                executorService.execute(new HttpRequestHandler(clientConnection, workers));
            }
        }
    }

    private void initializeWorkers() {
        workers = new HashMap<>();

        workers.put(Paths.USER_WORKER_MAIN_PATH, new UserWorker());
    }
}
