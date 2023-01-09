package bif3.tolan.swe1.mcg.httpserver;

import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.persistence.PersistenceManager;
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

    public void initializeWorkers(PersistenceManager persistenceManager) {
        workers = new ConcurrentHashMap<>();

        workers.put(RequestPaths.USER_WORKER_MAIN_PATH, new UserWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getDeckRepository()));
        workers.put(RequestPaths.LOGIN_WORKER_MAIN_PATH, new LoginWorker(
                persistenceManager.getUserRepository()));
        workers.put(RequestPaths.PACKAGES_WORKER_MAIN_PATH, new PackageWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getCardRepository(),
                persistenceManager.getPackageRepository()));
        workers.put(RequestPaths.SHOP_WORKER_MAIN_PATH, new StoreWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getCardRepository(),
                persistenceManager.getPackageRepository()));
        workers.put(RequestPaths.CARD_WORKER_MAIN_PATH, new CardWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getCardRepository()));
        workers.put(RequestPaths.DECK_WORKER_MAIN_PATH, new DeckWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getDeckRepository(),
                persistenceManager.getCardRepository()));
        workers.put(RequestPaths.STATISTICS_WORKER_MAIN_PATH, new StatisticsWorker(
                persistenceManager.getUserRepository()));
        workers.put(RequestPaths.SCOREBOARD_WORKER_MAIN_PATH, new ScoreboardWorker(
                persistenceManager.getUserRepository()));
        workers.put(RequestPaths.BATTLE_WORKER_MAIN_PATH, new BattleWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getCardRepository(),
                persistenceManager.getDeckRepository()));
        workers.put(RequestPaths.TRADE_WORKER_MAIN_PATH, new TradeWorker(
                persistenceManager.getUserRepository(),
                persistenceManager.getTradeOfferRepository(),
                persistenceManager.getCardRepository()));
    }
}
