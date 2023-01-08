package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.TradeOffer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Vector;

public class TradeUtils {

    public static String printAllTradeOffers(Vector<TradeOffer> tradeOffers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (TradeOffer trade : tradeOffers) {
            stringBuilder.append(trade.toString());
            if (trade.getCard() != null) {
                stringBuilder.append(" Card: ");
                stringBuilder.append(trade.getCard().toString());
            }
            stringBuilder.append(" \n");
        }
        return stringBuilder.toString();
    }

    public static boolean isValidTrade(TradeOffer tradeOffer) {
        if (tradeOffer == null) return false;
        if (tradeOffer.getTradeCardId() == null) return false;
        if (tradeOffer.getTradeId() == null) return false;
        if (tradeOffer.getUserId() < 0) return false;
        if (tradeOffer.getTradeId().length() > 50) return false;

        return true;
    }

    public static String extractStringFromJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String tradeInCardId = mapper.readValue(jsonString, String.class);
        return tradeInCardId;
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
                        (tradeOffer.getCardGroup() == null && tradeOffer.getCardType() == card.getMonsterType())
                                ^
                                (tradeOffer.getCardType() == null && card.getMonsterType().isInGroup(tradeOffer.getCardGroup()))
                );
    }
}
