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
        boolean exitApplication = false;

        while (!exitApplication) {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("stop")) {
                    httpServerThread.interrupt();
                    System.out.println("----------------------------------------------");
                    System.out.println("Server stopped");
                    System.out.println("----------------------------------------------");
                } else if (input.equalsIgnoreCase("exit")) {
                    if (httpServerThread.isAlive()) {
                        System.out.println("----------------------------------------------");
                        System.out.println("Http server thread still running. Please clost it with 'exit' first!");
                        System.out.println("----------------------------------------------");
                    } else {
                        exitApplication = true;
                    }
                }
            }
        }

        System.exit(0);
    }
}
