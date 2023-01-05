package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.model.Card;

import java.sql.SQLException;
import java.util.List;

public interface CardRepository {
    List<Card> getCardById(String cardId) throws SQLException;

    List<Card> getCardStackOfUserByUserId(String userId);

    Card getCardByTradeOfferId(String tradeId);

    List<Card> getCardDeckOfUserByUserId(String userId);

    List<Card> getCardPackageByPackageId(String packageId);

    void addCard(Card card);
}
