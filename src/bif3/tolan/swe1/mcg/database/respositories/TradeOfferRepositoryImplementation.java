package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.model.TradeOffer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TradeOfferRepositoryImplementation extends BaseRepository implements TradeOfferRepository {
    public TradeOfferRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public void addTradeOffer(int userId, TradeOffer tradeOffer) throws SQLException, InvalidInputException, HasActiveTradeException {
        if (isValidTrade(tradeOffer)) {
            if (getTradeOfferByUserId(userId) != null) {
                throw new HasActiveTradeException();
            }

            String sql = "INSERT INTO mctg_trade_offer (id, min_damage, user_id, card_type, card_group) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, tradeOffer.getTradeId());
            preparedStatement.setInt(2, tradeOffer.getMinDamage());
            preparedStatement.setInt(3, tradeOffer.getUserId());
            preparedStatement.setString(4, tradeOffer.getCardType().name());
            preparedStatement.setString(5, tradeOffer.getCardGroup().name());

            preparedStatement.executeUpdate();
        } else {
            throw new InvalidInputException();
        }
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
    public void removeTradeOfferFromUser(int userId) throws TradeOfferNotFoundException, SQLException {
        TradeOffer tradeOffer = getTradeOfferByUserId(userId);
        if (tradeOffer == null) {
            throw new TradeOfferNotFoundException();
        } else {
            String sql = "DELETE FROM mctg_trade_offer WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, userId);

            preparedStatement.executeUpdate();
        }
    }

    private TradeOffer extractTrade(ResultSet res) throws SQLException {
        if (res.next()) {
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
        } else {
            return null;
        }
    }

    private boolean isValidTrade(TradeOffer tradeOffer) {
        if (tradeOffer == null) return false;
        if (tradeOffer.getTradeCardId() == null) return false;
        if (tradeOffer.getTradeId() == null) return false;
        if (tradeOffer.getUserId() < 0) return false;
        if (tradeOffer.getTradeId().length() > 50) return false;

        return true;
    }
}
