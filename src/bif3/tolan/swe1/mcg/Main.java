package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.httpserver.HttpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer(DefaultValues.DEFAULT_SERVER_PORT);
        server.start();
    }
}
