package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.constants.ServerConstants;
import bif3.tolan.swe1.mcg.database.DbConnector;
import bif3.tolan.swe1.mcg.httpserver.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DbConnector dbConnection = new DbConnector();
        HttpServer server = new HttpServer(ServerConstants.DEFAULT_SERVER_PORT);

        server.initializeWorkers(dbConnection);
        server.start();
    }
}
