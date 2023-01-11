package bif3.tolan.swe1.mctg.persistence.respositories.implementations;

import bif3.tolan.swe1.mctg.exceptions.*;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.persistence.DatabaseConnector;
import bif3.tolan.swe1.mctg.persistence.respositories.BaseRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mctg.utils.CardUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation Repository responsible for accessing card data
 *
 * @author Christopher Tolan
 */
public class CardRepositoryImplementation extends BaseRepository implements CardRepository {
    public CardRepositoryImplementation(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public Card getCardById(String cardId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, cardId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractSingleCard(resultSet);
            }
        }
    }

    @Override
    public Vector<Card> getAllCardsByUserIdAsList(int userId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_user_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractManyCards(resultSet);
            }
        }
    }

    @Override
    public Card getCardByTradeOfferId(String tradeId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_trade_offer_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, tradeId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractSingleCard(resultSet);
            }
        }
    }

    @Override
    public Vector<Card> getAllCardsByDeckIdAsList(int deckId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_deck_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, deckId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractManyCards(resultSet);
            }
        }
    }

    @Override
    public ConcurrentHashMap<String, Card> getAllCardsByDeckIdAsMap(int deckId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_deck_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, deckId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractManyCardsAsMap(resultSet);
            }
        }
    }

    @Override
    public Vector<Card> getAllCardsByPackageIdAsList(int packageId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_package_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, packageId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return extractManyCards(resultSet);
            }
        }
    }

    @Override
    public synchronized void addNewCard(Card card) throws SQLException, IdExistsException, InvalidInputException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        if (CardUtils.isValidNewCard(card)) {
            if (getCardById(card.getCardId()) != null) {
                throw new IdExistsException();
            }

            String sql = "INSERT INTO mctg_card (id, name, damage) VALUES (?, ?, ?);";

            try (
                    Connection connection = connector.getDatabaseConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setString(1, card.getCardId());
                preparedStatement.setString(2, card.getName());
                preparedStatement.setFloat(3, card.getDamage());

                preparedStatement.executeUpdate();
            }
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public synchronized void assignCardToUserStack(String cardId, int userId) throws SQLException, InvalidInputException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_user_id = ? WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, cardId);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void checkCardBelongsToUser(String cardId, int userId) throws SQLException, ItemDoesNotBelongToUserException {
        boolean doesCardBelongToUser = false;
        String sql = "SELECT * FROM mctg_card WHERE id = ? AND mctg_user_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, cardId);
            preparedStatement.setInt(2, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                doesCardBelongToUser = resultSet.next();
            }
        }
        if (doesCardBelongToUser == false)
            throw new ItemDoesNotBelongToUserException();
    }

    @Override
    public synchronized void assignCardToUserDeck(String cardId, int deckId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_deck_id = ? WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, deckId);
            preparedStatement.setString(2, cardId);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public synchronized void assignCardToPackage(String cardId, int packageId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_package_id = ? WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, packageId);
            preparedStatement.setString(2, cardId);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public synchronized void assignCardToTradeOffer(String cardId, String tradeOfferId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_trade_offer_id = ? WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, tradeOfferId);
            preparedStatement.setString(2, cardId);

            preparedStatement.executeUpdate();
        }
    }

    private synchronized void resetCardRelations(String cardId) throws SQLException, InvalidInputException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        if (getCardById(cardId) != null) {
            String sql = "UPDATE mctg_card " +
                    "SET mctg_user_id = NULL, " +
                    "mctg_trade_offer_id = NULL, " +
                    "mctg_package_id = NULL, " +
                    "mctg_deck_id = NULL WHERE id = ?";

            try (
                    Connection connection = connector.getDatabaseConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setString(1, cardId);

                preparedStatement.executeUpdate();
            }
        } else {
            throw new InvalidInputException();
        }
    }

    private Card extractSingleCard(ResultSet resultSet) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        if (resultSet.next()) {
            return convertResultSetToCardModel(resultSet);
        } else {
            return null;
        }
    }

    private Vector<Card> extractManyCards(ResultSet resultSet) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        Vector<Card> cards = new Vector<>();
        while (resultSet.next()) {
            cards.add(convertResultSetToCardModel(resultSet));
        }
        return cards;
    }

    private ConcurrentHashMap<String, Card> extractManyCardsAsMap(ResultSet resultSet) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        ConcurrentHashMap<String, Card> cards = new ConcurrentHashMap();
        while (resultSet.next()) {
            Card card = convertResultSetToCardModel(resultSet);
            cards.put(card.getCardId(), card);
        }
        return cards;
    }

    private Card convertResultSetToCardModel(ResultSet resultSet) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        String cardId = resultSet.getString("id");
        String cardName = resultSet.getString("name");
        float cardDamage = resultSet.getFloat("damage");

        return new Card(cardId, cardName, cardDamage);
    }
}
