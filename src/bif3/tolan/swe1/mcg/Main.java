package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.httpserver.HttpServer;
import bif3.tolan.swe1.mcg.persistence.PersistenceManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        PersistenceManager dbConnection = new PersistenceManager();

        HttpServer server = new HttpServer(ServerConstants.DEFAULT_SERVER_PORT);
        server.initializeWorkers(dbConnection);

        Thread httpServerThread = new Thread(server);
        httpServerThread.start();

        Scanner scanner = new Scanner(System.in);
        boolean stop = false;

        while (!stop) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("stop")) {
                    stop = true;
                }
            }
        }
    }
}
