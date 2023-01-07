package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.model.Card;

import java.util.ArrayList;
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


    public static Card buildCard(String cardId, String name, float damage) throws InvalidCardParameterException {
        List<String> nameSplit = new ArrayList<>(List.of(name.split(CommonRegex.SPLIT_STRING_BY_UPPERCASE_LETTERS)));

        ElementType element = ElementType.NORMAL;
        CardType type = null;

        //TODO move logic to constructor of card
        try {
            if (nameSplit.size() > 0) {
                if (nameSplit.size() == 2) {
                    element = extractElementType(nameSplit.get(0));
                    nameSplit.remove(0);
                }

                type = extractCardType(nameSplit.get(0));
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidCardParameterException();
        }

        return new Card(cardId, name, element, damage, type);
    }

    public static String getCardsAsStringForDisplayPlain(String username, List<Card> cards) {
        StringBuilder cardsAsString = new StringBuilder("Cards in deck of user" + username + ":");
        if (cards.size() == 0) {
            cardsAsString.append("\nNo cards available");
        }
        for (Card c : cards) {
            cardsAsString.append("\n");
            cardsAsString.append(c.toString());
        }
        return cardsAsString.toString();
    }

    public static String getCardsAsStringForDisplay(String username, List<Card> cards) {
        StringBuilder cardsAsString = new StringBuilder("Cards in deck of user" + username + ":");
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
