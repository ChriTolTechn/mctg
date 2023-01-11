package bif3.tolan.swe1.mctg.httpserver;

import bif3.tolan.swe1.mctg.constants.RequestPaths;
import bif3.tolan.swe1.mctg.constants.ServerConstants;
import bif3.tolan.swe1.mctg.persistence.PersistenceManager;
import bif3.tolan.swe1.mctg.workers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP Server implementation
 *
 * @author Christopher Tolan
 */
public class HttpServer implements Runnable {
    private int port;
    private Map<String, Workable> workers;

    public HttpServer(int port) {
        this.port = port;
    }

    /**
     * Initializes all workers handling all requests
     *
     * @param persistenceManager injects required repositories into the workers
     */
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

    /**
     * Starts the server as Runnable
     */
    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(ServerConstants.DEFAULT_THREAD_COUNT);
        Socket clientConnection = null;

        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("----------------------------------------------");
            System.out.println("Server started...");
            System.out.println("----------------------------------------------");

            while (!Thread.currentThread().isInterrupted()) {
                clientConnection = serverSocket.accept();
                executorService.execute(new HttpRequestHandler(clientConnection, workers));
            }

            clientConnection.close();
        } catch (IOException e) {
            try {
                clientConnection.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }
}
