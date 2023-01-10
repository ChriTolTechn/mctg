package bif3.tolan.swe1.mcg.persistence;

import bif3.tolan.swe1.mcg.persistence.respositories.implementations.*;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import static bif3.tolan.swe1.mcg.constants.DatabaseConstants.*;

public class PersistenceManager {
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private DeckRepository deckRepository;
    private PackageRepository packageRepository;
    private TradeOfferRepository tradeOfferRepository;

    public PersistenceManager() {
        if (TRY_RESET_DB_ON_STARTUP) {
            try {
                resetDatabase();
            } catch (SQLException e) {
                System.err.println("----------------------------------------------");
                e.printStackTrace();
                System.err.println("----------------------------------------------");
            }
        }
        initializeRepositories();
    }

    private void initializeRepositories() {
        DatabaseConnector dbConnector = new DatabaseConnectorImplementation();

        userRepository = new UserRepositoryImplementation(dbConnector);
        cardRepository = new CardRepositoryImplementation(dbConnector);
        deckRepository = new DeckRepositoryImplementation(dbConnector);
        packageRepository = new PackageRepositoryImplementation(dbConnector);
        tradeOfferRepository = new TradeOfferRepositoryImplementation(dbConnector);
    }

    private void resetDatabase() throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Currently the database is set to reset on every startup. Are you sure that you want to delete all data in it? (y/n)");
        String input = scanner.nextLine();
        if (input.toLowerCase().equals("y")) {
            try (
                    Connection connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USERNAME, DB_PASSWORD);
                    PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE TABLE mctg_card, mctg_trade_offer, mctg_deck, mctg_package, mctg_user")
            ) {
                preparedStatement.execute();
            }

            System.out.println("----------------------------------------------");
            System.out.println("The database has been successfully reset!");
            System.out.println("----------------------------------------------");
        } else {
            System.out.println("----------------------------------------------");
            System.out.println("The database did not reset!");
            System.out.println("----------------------------------------------");
        }
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
