package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.model.TradeOffer;

import java.sql.SQLException;

public interface TradeOfferRepository {
    public void addTradeOffer(int userId, TradeOffer tradeOffer) throws SQLException, InvalidInputException, HasActiveTradeException;

    public TradeOffer getTradeOfferByUserId(int userId) throws SQLException;

    public void removeTradeOfferFromUser(int userId) throws TradeOfferNotFoundException, SQLException;
}
