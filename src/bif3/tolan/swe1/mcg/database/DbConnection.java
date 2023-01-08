package bif3.tolan.swe1.mcg.database;

import bif3.tolan.swe1.mcg.database.respositories.implementations.*;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import static bif3.tolan.swe1.mcg.constants.DatabaseConstants.*;

public class DbConnection {
    private Connection connection;

    private UserRepository userRepository;
    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private PackageRepository packageRepository;
    private TradeOfferRepository tradeOfferRepository;

    public DbConnection() {
        establishConnection();
        initializeRepositories();
    }

    private void initializeRepositories() {
        userRepository = new UserRepositoryImplementation(connection);
        cardRepository = new CardRepositoryImplementation(connection);
        deckRepository = new DeckRepositoryImplementation(connection);
        packageRepository = new PackageRepositoryImplementation(connection);
        tradeOfferRepository = new TradeOfferRepositoryImplementation(connection);
        //TODO add other repositories
    }

    private void establishConnection() {
        System.out.println("----------------------------------------------");
        System.out.println("Trying to connect to database...");
        System.out.println("----------------------------------------------");
        try {
            connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USERNAME, DB_PASSWORD);

            System.out.println("----------------------------------------------");
            System.out.println("Connected to database!");
            System.out.println("----------------------------------------------");

            if (TRY_RESET_DB_ON_STARTUP)
                resetDatabase();
        } catch (SQLException e) {
            System.err.println("----------------------------------------------");
            e.printStackTrace();
            System.err.println("----------------------------------------------");
        }
    }

    private void resetDatabase() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Currently the database is set to reset on every startup. Are you sure that you want to delete all data in it? (y/n)");
        String input = scanner.nextLine();
        if (input.toLowerCase().equals("y")) {
            PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE mctg_card, mctg_trade_offer, mctg_deck, mctg_package, mctg_user");
            statement.execute();
            statement.close();
            System.out.println("----------------------------------------------");
            System.out.println("The database has been successfully reset!");
            System.out.println("----------------------------------------------");
        } else {
            System.out.println("----------------------------------------------");
            System.out.println("The database did not reset!");
            System.out.println("----------------------------------------------");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public CardRepository getCardRepository() {
        return cardRepository;
    }

    public DeckRepository getDeckRepository() {
        return deckRepository;
    }

    public PackageRepository getPackageRepository() {
        return packageRepository;
    }

    public TradeOfferRepository getTradeOfferRepository() {
        return tradeOfferRepository;
    }
}
