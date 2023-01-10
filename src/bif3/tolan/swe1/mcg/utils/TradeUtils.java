package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.TradeOffer;

public class TradeUtils {

    public static boolean isValidTrade(TradeOffer tradeOffer) {
        if (tradeOffer == null) return false;
        if (tradeOffer.getTradeCardId() == null) return false;
        if (tradeOffer.getTradeId() == null) return false;
        if (tradeOffer.getUserId() < 0) return false;
        if (tradeOffer.getTradeId().length() > 50) return false;
        if (tradeOffer.getMinDamage() < 0) return false;

        return true;
    }

    /**
     * Checks if the requirement for the trade is met.
     * Since a trade offer can be specified by either monster type or card grouop, an XOR is used to check
     *
     * @param card       Card offering from the buyer
     * @param tradeOffer Requirements from the seller
     * @return True if the requirements are met
     */
    public static boolean cardMeetsRequirement(Card card, TradeOffer tradeOffer) {
        return card.getDamage() >= tradeOffer.getMinDamage() &&
                (
                        (tradeOffer.getRequestedCardGroup() == null && tradeOffer.getRequestedCardType() == card.getMonsterType())
                                ^
                                (tradeOffer.getRequestedCardType() == null && card.getMonsterType().isInGroup(tradeOffer.getRequestedCardGroup()))
                );
    }
}
