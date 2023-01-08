package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.model.Card;

import java.util.List;

public class CardUtils {

    public static ElementType extractElementType(String element) {
        if (element.equalsIgnoreCase("regular")) {
            return ElementType.NORMAL;
        } else {
            return ElementType.valueOf(element.toUpperCase());
        }
    }

    public static CardType extractCardType(String type) {
        return CardType.valueOf(type.toUpperCase());
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
}
