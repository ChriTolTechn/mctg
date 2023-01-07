package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.database.DbConnection;
import bif3.tolan.swe1.mcg.workers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public void initializeWorkers(DbConnection dbConnection) {
        workers = new ConcurrentHashMap<>();

        workers.put(Paths.USER_WORKER_MAIN_PATH, new UserWorker(dbConnection.getUserRepository(), dbConnection.getDeckRepository()));
        workers.put(Paths.LOGIN_WORKER_MAIN_PATH, new LoginWorker(dbConnection.getUserRepository()));
        workers.put(Paths.PACKAGES_WORKER_MAIN_PATH, new PackageWorker(dbConnection.getUserRepository(), dbConnection.getCardRepository(), dbConnection.getPackageRepository()));
        workers.put(Paths.SHOP_WORKER_MAIN_PATH, new StoreWorker(dbConnection.getUserRepository(), dbConnection.getCardRepository(), dbConnection.getPackageRepository()));
        workers.put(Paths.CARD_WORKER_MAIN_PATH, new CardWorker(dbConnection.getUserRepository(), dbConnection.getCardRepository()));
        workers.put(Paths.DECK_WORKER_MAIN_PATH, new DeckWorker(dbConnection.getUserRepository(), dbConnection.getDeckRepository(), dbConnection.getCardRepository()));
        workers.put(Paths.STATISTICS_WORKER_MAIN_PATH, new StatisticsWorker(dbConnection.getUserRepository()));
        workers.put(Paths.SCOREBOARD_WORKER_MAIN_PATH, new ScoreboardWorker(dbConnection.getUserRepository()));
    }
}
