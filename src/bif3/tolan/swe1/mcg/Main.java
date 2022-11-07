package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.persistence.PostgreSQLJDBC;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        PostgreSQLJDBC test = new PostgreSQLJDBC();
        test.openDatabase();
    }
}
