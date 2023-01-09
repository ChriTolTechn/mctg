package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.database.DbConnector;
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

    public void initializeWorkers(DbConnector dbConnector) {
        workers = new ConcurrentHashMap<>();

        workers.put(RequestPaths.USER_WORKER_MAIN_PATH, new UserWorker(
                dbConnector.getUserRepository(),
                dbConnector.getDeckRepository()));
        workers.put(RequestPaths.LOGIN_WORKER_MAIN_PATH, new LoginWorker(
                dbConnector.getUserRepository()));
        workers.put(RequestPaths.PACKAGES_WORKER_MAIN_PATH, new PackageWorker(
                dbConnector.getUserRepository(),
                dbConnector.getCardRepository(),
                dbConnector.getPackageRepository()));
        workers.put(RequestPaths.SHOP_WORKER_MAIN_PATH, new StoreWorker(
                dbConnector.getUserRepository(),
                dbConnector.getCardRepository(),
                dbConnector.getPackageRepository()));
        workers.put(RequestPaths.CARD_WORKER_MAIN_PATH, new CardWorker(
                dbConnector.getUserRepository(),
                dbConnector.getCardRepository()));
        workers.put(RequestPaths.DECK_WORKER_MAIN_PATH, new DeckWorker(
                dbConnector.getUserRepository(),
                dbConnector.getDeckRepository(),
                dbConnector.getCardRepository()));
        workers.put(RequestPaths.STATISTICS_WORKER_MAIN_PATH, new StatisticsWorker(
                dbConnector.getUserRepository()));
        workers.put(RequestPaths.SCOREBOARD_WORKER_MAIN_PATH, new ScoreboardWorker(
                dbConnector.getUserRepository()));
        workers.put(RequestPaths.BATTLE_WORKER_MAIN_PATH, new BattleWorker(
                dbConnector.getUserRepository(),
                dbConnector.getCardRepository(),
                dbConnector.getDeckRepository()));
        workers.put(RequestPaths.TRADE_WORKER_MAIN_PATH, new TradeWorker(
                dbConnector.getUserRepository(),
                dbConnector.getTradeOfferRepository(),
                dbConnector.getCardRepository()));
    }
}
