package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.TradeOfferRepository;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.NoActiveTradeOffersException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.model.TradeOffer;
import bif3.tolan.swe1.mcg.model.enums.CardType;
import bif3.tolan.swe1.mcg.utils.TradeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class TradeOfferRepositoryImplementation extends BaseRepository implements TradeOfferRepository {
    public TradeOfferRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public TradeOffer getTradeOfferByUserId(int userId) throws SQLException {
        String sql = "SELECT id, min_damage, card_type, card_group, user_id FROM mctg_trade_offer WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, userId);

        ResultSet resultSet = preparedStatement.executeQuery();

        TradeOffer tradeOffer = extractTrade(resultSet);

        resultSet.close();
        preparedStatement.close();

        return tradeOffer;
    }

    @Override
    public Vector<TradeOffer> getAllTradeOffersAsList() throws SQLException, NoActiveTradeOffersException {
        String sql = "SELECT id, min_damage, card_type, card_group, user_id FROM mctg_trade_offer";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        Vector<TradeOffer> tradeOffers = extractManyTrades(resultSet);

        resultSet.close();
        preparedStatement.close();

        if (tradeOffers.isEmpty())
            throw new NoActiveTradeOffersException();
        return tradeOffers;
    }

    @Override
    public synchronized void createTradeOffer(TradeOffer tradeOffer) throws SQLException, InvalidInputException {
        if (TradeUtils.isValidTrade(tradeOffer)) {
            String sql = "INSERT INTO mctg_trade_offer (id, min_damage, user_id, card_type, card_group) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, tradeOffer.getTradeId());
            preparedStatement.setInt(2, tradeOffer.getMinDamage());
            preparedStatement.setInt(3, tradeOffer.getUserId());
            preparedStatement.setString(4, tradeOffer.getCardType() != null ? tradeOffer.getCardType().name() : null);
            preparedStatement.setString(5, tradeOffer.getCardGroup() != null ? tradeOffer.getCardGroup().name() : null);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public TradeOffer getTradeOfferById(String tradeId) throws SQLException, TradeOfferNotFoundException {
        String sql = "SELECT id, min_damage, card_type, card_group, user_id FROM mctg_trade_offer WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, tradeId);

        ResultSet resultSet = preparedStatement.executeQuery();

        TradeOffer tradeOffer = extractTrade(resultSet);

        resultSet.close();
        preparedStatement.close();

        if (tradeOffer == null)
            throw new TradeOfferNotFoundException();

        return tradeOffer;
    }

    @Override
    public synchronized void deleteTrade(String tradeId) throws SQLException {
        String sql = "DELETE FROM mctg_trade_offer WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, tradeId);

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }

    private TradeOffer convertResultSetToTradeOfferModel(ResultSet resultSet) throws SQLException {
        String tradeId = resultSet.getString("id");
        int minDamage = resultSet.getInt("min_damage");
        int userId = resultSet.getInt("user_id");
        String cardType = resultSet.getString("card_type");
        String cardGroup = resultSet.getString("card_group");

        if (cardType == null || cardType.isEmpty()) {
            return new TradeOffer(tradeId, userId, minDamage, CardType.CardGroup.valueOf(cardGroup));
        } else {
            return new TradeOffer(tradeId, userId, minDamage, CardType.valueOf(cardType));
        }
    }

    private TradeOffer extractTrade(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return convertResultSetToTradeOfferModel(resultSet);
        } else {
            return null;
        }
    }

    private Vector<TradeOffer> extractManyTrades(ResultSet resultSet) throws SQLException {
        Vector<TradeOffer> offers = new Vector<>();
        while (resultSet.next()) {
            offers.add(convertResultSetToTradeOfferModel(resultSet));
        }
        return offers;
    }
}
