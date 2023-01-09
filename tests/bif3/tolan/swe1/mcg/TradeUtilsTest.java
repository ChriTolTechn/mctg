package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.model.TradeOffer;
import bif3.tolan.swe1.mcg.model.enums.CardType;
import bif3.tolan.swe1.mcg.utils.TradeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TradeUtilsTest {
    @Test
    public void testValidTrade() {
        TradeOffer tradeOffer = new TradeOffer("123", 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("1");

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertTrue(isValid);
    }

    @Test
    public void testNullTrade() {
        TradeOffer tradeOffer = null;

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidTradeCardId() {
        TradeOffer tradeOffer = new TradeOffer("123", 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId(null);

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidTradeId() {
        TradeOffer tradeOffer = new TradeOffer(null, 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("a");

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidUserId() {
        TradeOffer tradeOffer = new TradeOffer("a", -1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("a");

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidTradeIdLength() {
        // Arrange
        StringBuilder longTradeId = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            longTradeId.append("a");
        }
        TradeOffer tradeOffer = new TradeOffer(longTradeId.toString(), 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("a");

        boolean isValid = TradeUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }
}
