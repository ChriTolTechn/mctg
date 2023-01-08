package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.model.enums.CardType;
import bif3.tolan.swe1.mcg.model.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mcg.model.Card;

import java.util.List;

public class CardUtils {

    public static ElementType extractElementType(String element) throws UnsupportedElementTypeException {
        if (element.equalsIgnoreCase("regular")) {
            return ElementType.NORMAL;
        } else {
            try {
                return ElementType.valueOf(element.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new UnsupportedElementTypeException();
            }
        }
    }

    public static CardType extractCardType(String type) throws UnsupportedCardTypeException {
        try {
            return CardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCardTypeException();
        }
    }

    public static String getMultipleCardDisplayForUser(String username, List<Card> cards) {
        StringBuilder cardsAsString = new StringBuilder("Cards of user" + username + ":");
        if (cards.size() == 0) {
            cardsAsString.append("\nNo cards available");
        }
        for (Card c : cards) {
            cardsAsString.append("\n");
            cardsAsString.append(c.toString());
        }
        return cardsAsString.toString();
    }

    public static String getCardDetails(String username, List<Card> cards) {
        StringBuilder cardsAsString = new StringBuilder("Cards of user" + username + ":");
        if (cards.size() == 0) {
            cardsAsString.append("\nNo cards available");
        }
        for (Card c : cards) {
            cardsAsString.append("\n-------- Card " + (cards.indexOf(c) + 1) + " --------");
            cardsAsString.append("\n Id:      " + c.getCardId());
            cardsAsString.append("\n Name:    " + c.getName());
            cardsAsString.append("\n Element: " + c.getElement());
            cardsAsString.append("\n Type:    " + c.getCardType());
            cardsAsString.append("\n Damage:  " + c.getDamage());
        }
        return cardsAsString.toString();
    }

    public static boolean isValidNewCard(Card card) {
        if (card.getCardId() == null || card.getName() == null) return false;
        if (card.getName().length() > 50) return false;
        if (card.getCardId().length() > 50) return false;
        if (card.getDamage() < 0f) return false;

        return true;
    }
}
