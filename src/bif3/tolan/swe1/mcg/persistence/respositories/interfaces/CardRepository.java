package bif3.tolan.swe1.mcg.persistence.respositories.interfaces;

import bif3.tolan.swe1.mcg.exceptions.*;
import bif3.tolan.swe1.mcg.model.Card;

import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public interface CardRepository {
    Card getCardById(String cardId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    Vector<Card> getAllCardsByUserIdAsList(int userId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    Card getCardByTradeOfferId(String tradeId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    Vector<Card> getAllCardsByDeckIdAsList(int deckId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    Vector<Card> getAllCardsByPackageIdAsList(int packageId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    void addNewCard(Card card) throws SQLException, IdExistsException, InvalidInputException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    void assignCardToUserStack(String cardId, int userId) throws SQLException, InvalidInputException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    void checkCardBelongsToUser(String cardId, int userId) throws SQLException, ItemDoesNotBelongToUserException;

    void assignCardToUserDeck(String cardId, int deckId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    void assignCardToPackage(String cardId, int packageId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    void assignCardToTradeOffer(String cardId, String tradeOfferId) throws InvalidInputException, SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;

    ConcurrentHashMap<String, Card> getAllCardsByDeckIdAsMap(int deckId) throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException;
}
