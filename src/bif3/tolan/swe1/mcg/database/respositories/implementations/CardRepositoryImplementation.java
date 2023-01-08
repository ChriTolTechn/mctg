package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.utils.CardUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class CardRepositoryImplementation extends BaseRepository implements CardRepository {
    public CardRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public Card getCardById(String cardId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, cardId);

        ResultSet res = preparedStatement.executeQuery();

        return extractSingleCard(res);
    }

    @Override
    public Vector<Card> getAllCardsByUserIdAsList(int userId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, userId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCards(res);
    }

    @Override
    public Card getCardByTradeOfferId(String tradeId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_trade_offer_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, tradeId);

        ResultSet res = preparedStatement.executeQuery();

        return extractSingleCard(res);
    }

    @Override
    public Vector<Card> getAllCardsByDeckIdAsList(int deckId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_deck_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, deckId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCards(res);
    }

    @Override
    public ConcurrentHashMap<String, Card> getAllCardsByDeckIdAsMap(int deckId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_deck_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, deckId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCardsAsMap(res);
    }

    @Override
    public Vector<Card> getAllCardsByPackageIdAsList(int packageId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_package_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, packageId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCards(res);
    }

    @Override
    public void addNewCard(Card card) throws SQLException, InvalidCardParameterException, IdExistsException, InvalidInputException {
        if (CardUtils.isValidNewCard(card)) {
            if (getCardById(card.getCardId()) != null) {
                throw new IdExistsException();
            }

            String sql = "INSERT INTO mctg_card (id, name, damage) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, card.getCardId());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setFloat(3, card.getDamage());

            preparedStatement.executeUpdate();
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public void assignCardToUserStack(String cardId, int userId) throws SQLException, InvalidCardParameterException, InvalidInputException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_user_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, cardId);

        preparedStatement.executeUpdate();
    }

    @Override
    public boolean doesCardBelongToUser(String cardId, int userId) throws SQLException {
        String sql = "SELECT * FROM mctg_card WHERE id = ? AND mctg_user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, cardId);
        preparedStatement.setInt(2, userId);

        ResultSet resultSet = preparedStatement.executeQuery();

        return resultSet.next();
    }

    @Override
    public void assignCardToUserDeck(String cardId, int deckId) throws InvalidInputException, SQLException, InvalidCardParameterException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_deck_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, deckId);
        preparedStatement.setString(2, cardId);

        preparedStatement.executeUpdate();
    }

    @Override
    public void assignCardToPackage(String cardId, int packageId) throws InvalidInputException, SQLException, InvalidCardParameterException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_package_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, packageId);
        preparedStatement.setString(2, cardId);

        preparedStatement.executeUpdate();
    }

    @Override
    public void assignCardToTradeOffer(String cardId, String tradeOfferId) throws InvalidInputException, SQLException, InvalidCardParameterException {
        resetCardRelations(cardId);

        String sql = "UPDATE mctg_card SET mctg_trade_offer_id = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, tradeOfferId);
        preparedStatement.setString(2, cardId);

        preparedStatement.executeUpdate();
    }

    private void resetCardRelations(String cardId) throws SQLException, InvalidCardParameterException, InvalidInputException {
        if (getCardById(cardId) != null) {
            String sql = "UPDATE mctg_card " +
                    "SET mctg_user_id = NULL, " +
                    "mctg_trade_offer_id = NULL, " +
                    "mctg_package_id = NULL, " +
                    "mctg_deck_id = NULL WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, cardId);

            preparedStatement.executeUpdate();
        } else {
            throw new InvalidInputException();
        }
    }

    private Card extractSingleCard(ResultSet res) throws SQLException, InvalidCardParameterException {
        if (res.next()) {
            return convertResultSetToCardModel(res);
        } else {
            return null;
        }
    }

    private Vector<Card> extractManyCards(ResultSet res) throws SQLException, InvalidCardParameterException {
        Vector<Card> cards = new Vector<>();
        while (res.next()) {
            cards.add(convertResultSetToCardModel(res));
        }
        return cards;
    }

    private ConcurrentHashMap<String, Card> extractManyCardsAsMap(ResultSet res) throws SQLException, InvalidCardParameterException {
        ConcurrentHashMap<String, Card> cards = new ConcurrentHashMap();
        while (res.next()) {
            Card card = convertResultSetToCardModel(res);
            cards.put(card.getCardId(), card);
        }
        return cards;
    }

    private Card convertResultSetToCardModel(ResultSet res) throws SQLException, InvalidCardParameterException {
        String cardId = res.getString("id");
        String cardName = res.getString("name");
        float cardDamage = res.getFloat("damage");

        return new Card(cardId, cardName, cardDamage);
    }
}
