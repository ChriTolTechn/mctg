package bif3.tolan.swe1.mctg;

import bif3.tolan.swe1.mctg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.model.enums.CardType;
import bif3.tolan.swe1.mctg.model.enums.ElementType;
import bif3.tolan.swe1.mctg.utils.CardUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CardUtilsTest {
    @Test
    public void testValidElement() throws UnsupportedElementTypeException {
        String element = "FIRE";

        ElementType extractedElement = CardUtils.extractElementType(element);

        Assertions.assertEquals(ElementType.FIRE, extractedElement);
    }

    @Test
    public void testRegularElement() throws UnsupportedElementTypeException {
        String element = "Regular";

        ElementType extractedElement = CardUtils.extractElementType(element);

        Assertions.assertEquals(ElementType.NORMAL, extractedElement);
    }

    @Test
    public void testInvalidElement() {
        String element = "invalid";

        Assertions.assertThrows(UnsupportedElementTypeException.class, () -> CardUtils.extractElementType(element));
    }

    @Test
    public void testValidCardType() throws UnsupportedCardTypeException {
        String type = "DRAGON";

        CardType extractedType = CardUtils.extractCardType(type);

        Assertions.assertEquals(CardType.DRAGON, extractedType);
    }

    @Test
    public void testInvalidCardType() {
        String type = "invalid";

        Assertions.assertThrows(UnsupportedCardTypeException.class, () -> CardUtils.extractCardType(type));
    }

    @Test
    public void testValidCard() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card card = new Card("1", "WaterDragon", 100f);

        boolean isValid = CardUtils.isValidNewCard(card);

        Assertions.assertTrue(isValid);
    }

    @Test
    public void testCardIdNull() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card card = new Card(null, "Dragon", 100f);

        boolean isValid = CardUtils.isValidNewCard(card);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testCardIdTooLong() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        StringBuilder longId = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longId.append("a");
        }
        Card card = new Card(longId.toString(), "Dragon", 100f);

        boolean isValid = CardUtils.isValidNewCard(card);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testCardDamageNegative() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card card = new Card("123", "Dragon", -100f);

        boolean isValid = CardUtils.isValidNewCard(card);

        Assertions.assertFalse(isValid);
    }

}
