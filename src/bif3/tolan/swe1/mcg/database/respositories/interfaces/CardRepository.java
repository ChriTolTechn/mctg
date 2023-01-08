package bif3.tolan.swe1.mcg.database.respositories.interfaces;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.Card;

import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public interface CardRepository {
    Card getCardById(String cardId) throws SQLException, InvalidCardParameterException;

    Vector<Card> getCardsByUserId(int userId) throws SQLException, InvalidCardParameterException;

    Card getCardByTradeOfferId(String tradeId) throws SQLException, InvalidCardParameterException;

    Vector<Card> getCardsByDeckId(int deckId) throws SQLException, InvalidCardParameterException;

    Vector<Card> getCardPackageByPackageId(int packageId) throws SQLException, InvalidCardParameterException;

    void addCard(Card card) throws SQLException, InvalidCardParameterException, IdExistsException, InvalidInputException;

    void assignCardToUserStack(String cardId, int userId) throws SQLException, InvalidCardParameterException, InvalidInputException;

    boolean doesCardBelongToUser(String cardId, int userId) throws SQLException;

    void assignCardToUserDeck(String cardId, int deckId) throws InvalidInputException, SQLException, InvalidCardParameterException;

    void assignCardToPackage(String cardId, int packageId) throws InvalidInputException, SQLException, InvalidCardParameterException;

    void assignCardToTradeOffer(String cardId, String tradeOfferId) throws InvalidInputException, SQLException, InvalidCardParameterException;

    ConcurrentHashMap<String, Card> getCardsByDeckIdAsMap(int deckId) throws SQLException, InvalidCardParameterException;
}
