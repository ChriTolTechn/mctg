package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.httpserver.HttpServer;
import bif3.tolan.swe1.mcg.persistence.PersistenceManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        PersistenceManager dbConnection = new PersistenceManager();
        HttpServer server = new HttpServer(ServerConstants.DEFAULT_SERVER_PORT);

        server.initializeWorkers(dbConnection);
        server.start();
    }
}
