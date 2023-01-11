package bif3.tolan.swe1.mctg;

import bif3.tolan.swe1.mctg.model.TradeOffer;
import bif3.tolan.swe1.mctg.model.enums.CardType;
import bif3.tolan.swe1.mctg.utils.TradeOfferUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TradeOfferUtilsTest {
    @Test
    public void testValidTrade() {
        TradeOffer tradeOffer = new TradeOffer("123", 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("1");

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

        Assertions.assertTrue(isValid);
    }

    @Test
    public void testNullTrade() {
        TradeOffer tradeOffer = null;

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidTradeCardId() {
        TradeOffer tradeOffer = new TradeOffer("123", 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId(null);

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidTradeId() {
        TradeOffer tradeOffer = new TradeOffer(null, 1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("a");

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }

    @Test
    public void testInvalidUserId() {
        TradeOffer tradeOffer = new TradeOffer("a", -1, 10, CardType.CardGroup.MONSTER);
        tradeOffer.setTradeCardId("a");

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

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

        boolean isValid = TradeOfferUtils.isValidTrade(tradeOffer);

        Assertions.assertFalse(isValid);
    }
}
