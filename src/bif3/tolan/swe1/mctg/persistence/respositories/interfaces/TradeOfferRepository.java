package bif3.tolan.swe1.mctg.persistence.respositories.interfaces;

import bif3.tolan.swe1.mctg.exceptions.HasActiveTradeException;
import bif3.tolan.swe1.mctg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mctg.exceptions.NoActiveTradeOffersException;
import bif3.tolan.swe1.mctg.exceptions.TradeOfferNotFoundException;
import bif3.tolan.swe1.mctg.model.TradeOffer;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Repository responsible for accessing trade offer data
 *
 * @author Christopher Tolan
 */
public interface TradeOfferRepository {
    public void createTradeOffer(TradeOffer tradeOffer) throws SQLException, InvalidInputException, HasActiveTradeException;

    public TradeOffer getTradeOfferById(String tradeId) throws SQLException, TradeOfferNotFoundException;

    TradeOffer getTradeOfferByUserId(int userId) throws SQLException, TradeOfferNotFoundException;

    Vector<TradeOffer> getAllTradeOffersAsList() throws SQLException, NoActiveTradeOffersException;

    public void deleteTrade(String tradeId) throws TradeOfferNotFoundException, SQLException;
}
