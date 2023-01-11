package bif3.tolan.swe1.mctg.utils;

import bif3.tolan.swe1.mctg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.model.enums.CardType;
import bif3.tolan.swe1.mctg.model.enums.ElementType;

import java.util.List;

/**
 * Util card used for cards
 *
 * @author Christopher Tolan
 */
public class CardUtils {
    /**
     * Extracts an ElementType enum out of a string
     *
     * @param element the element to be extracted
     * @return element as enum
     * @throws UnsupportedElementTypeException if there is no enum that represents the string
     */
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

    /**
     * Extracts a CardType enum out of a string
     *
     * @param type the type to be extracted
     * @return type as enum
     * @throws UnsupportedElementTypeException if there is no enum that represents the string
     */
    public static CardType extractCardType(String type) throws UnsupportedCardTypeException {
        try {
            return CardType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedCardTypeException();
        }
    }

    /**
     * Method that prints a string of all cards provided to be displayed pretty in the console
     *
     * @param username Name of the user
     * @param cards    Cards to be displayed
     * @return string with all the data
     */
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

    /**
     * Checks if a card is valid and can be saved to the database
     *
     * @param card Card to be checked
     * @return True if it is valid
     */
    public static boolean isValidNewCard(Card card) {
        if (card.getCardId() == null || card.getName() == null) return false;
        if (card.getName().length() > 50) return false;
        if (card.getCardId().length() > 50) return false;
        if (card.getDamage() < 0f) return false;

        return true;
    }
}
