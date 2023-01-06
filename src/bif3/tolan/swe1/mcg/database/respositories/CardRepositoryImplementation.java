package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.utils.CardUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public List<Card> getCardsByUserId(int userId) throws SQLException, InvalidCardParameterException {
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
    public List<Card> getCardsByDeckId(int deckId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_deck_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, deckId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCards(res);
    }

    @Override
    public List<Card> getCardPackageByPackageId(String packageId) throws SQLException, InvalidCardParameterException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE mctg_package_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, packageId);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyCards(res);
    }

    @Override
    public void addCard(Card card) throws SQLException, InvalidCardParameterException, IdExistsException, InvalidInputException {
        if (isValidNewCard(card)) {
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

        String sql = "UPDATE mctg_card SET mctg_package = ? WHERE id = ?";
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
                    "mctg_package = NULL, " +
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
            return convertCardToModel(res);
        } else {
            return null;
        }
    }

    private List<Card> extractManyCards(ResultSet res) throws SQLException, InvalidCardParameterException {
        List<Card> cards = new ArrayList<>();
        while (res.next()) {
            cards.add(convertCardToModel(res));
        }
        return cards;
    }

    private Card convertCardToModel(ResultSet res) throws SQLException, InvalidCardParameterException {
        String cardId = res.getString("id");
        String cardName = res.getString("name");
        float cardDamage = res.getFloat("damage");

        return CardUtils.buildCard(cardId, cardName, cardDamage);
    }

    private boolean isValidNewCard(Card card) {
        if (card.getCardId() == null || card.getName() == null) return false;
        if (card.getName().length() > 50) return false;
        if (card.getCardId().length() > 50) return false;
        if (card.getDamage() < 0f) return false;

        return true;
    }
}
