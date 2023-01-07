package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.model.TradeOffer;

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
}
