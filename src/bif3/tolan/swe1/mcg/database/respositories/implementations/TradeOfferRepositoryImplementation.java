package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.TradeOfferRepository;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
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

        ResultSet res = preparedStatement.executeQuery();

        return extractTrade(res);
    }

    @Override
    public Vector<TradeOffer> getAllTradeOffersAsList() throws SQLException {
        String sql = "SELECT id, min_damage, card_type, card_group, user_id FROM mctg_trade_offer";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet res = preparedStatement.executeQuery();

        return extractManyTrades(res);
    }

    @Override
    public void createTradeOffer(TradeOffer tradeOffer) throws SQLException, InvalidInputException {
        if (TradeUtils.isValidTrade(tradeOffer)) {
            String sql = "INSERT INTO mctg_trade_offer (id, min_damage, user_id, card_type, card_group) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, tradeOffer.getTradeId());
            preparedStatement.setInt(2, tradeOffer.getMinDamage());
            preparedStatement.setInt(3, tradeOffer.getUserId());
            preparedStatement.setString(4, tradeOffer.getCardType() != null ? tradeOffer.getCardType().name() : null);
            preparedStatement.setString(5, tradeOffer.getCardGroup() != null ? tradeOffer.getCardGroup().name() : null);

            preparedStatement.executeUpdate();
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public TradeOffer getTradeOfferById(String tradeId) throws SQLException {
        String sql = "SELECT id, min_damage, card_type, card_group, user_id FROM mctg_trade_offer WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, tradeId);

        ResultSet res = preparedStatement.executeQuery();

        return extractTrade(res);
    }

    @Override
    public void deleteTrade(String tradeId) throws SQLException {
        String sql = "DELETE FROM mctg_trade_offer WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, tradeId);
        preparedStatement.executeUpdate();
    }

    private TradeOffer convertResultSetToTradeOfferModel(ResultSet res) throws SQLException {
        String tradeId = res.getString("id");
        int minDamage = res.getInt("min_damage");
        int userId = res.getInt("user_id");
        String cardType = res.getString("card_type");
        String cardGroup = res.getString("card_group");

        if (cardType == null || cardType.isEmpty()) {
            return new TradeOffer(tradeId, userId, minDamage, CardType.CardGroup.valueOf(cardGroup));
        } else {
            return new TradeOffer(tradeId, userId, minDamage, CardType.valueOf(cardType));
        }
    }

    private TradeOffer extractTrade(ResultSet res) throws SQLException {
        if (res.next()) {
            return convertResultSetToTradeOfferModel(res);
        } else {
            return null;
        }
    }

    private Vector<TradeOffer> extractManyTrades(ResultSet res) throws SQLException {
        Vector<TradeOffer> offers = new Vector<>();
        while (res.next()) {
            offers.add(convertResultSetToTradeOfferModel(res));
        }
        return offers;
    }
}
