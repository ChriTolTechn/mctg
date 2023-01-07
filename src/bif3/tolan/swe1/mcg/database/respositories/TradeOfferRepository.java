package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mcg.model.TradeOffer;

import java.sql.SQLException;
import java.util.Vector;

public interface TradeOfferRepository {
    public void createTradeOffer(TradeOffer tradeOffer) throws SQLException, InvalidInputException, HasActiveTradeException;

    public TradeOffer getTradeOfferById(String tradeId) throws SQLException;

    TradeOffer getTradeOfferByUserId(int userId) throws SQLException;

    Vector<TradeOffer> getAllTradeOffers() throws SQLException;

    public void deleteTrade(String tradeId) throws TradeOfferNotFoundException, SQLException;
}
