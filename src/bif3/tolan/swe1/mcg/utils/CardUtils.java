package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardUtils {

    public static Card buildCard(String cardId, String name, float damage) throws InvalidCardParameterException {
        List<String> nameSplit = new ArrayList<>(List.of(name.split(CommonRegex.SPLIT_STRING_BY_UPPERCASE_LETTERS)));

        ElementType element = ElementType.NORMAL;
        CardType type = null;

        try {
            if (nameSplit.size() > 0) {
                if (nameSplit.size() == 2) {
                    element = ElementType.valueOf(nameSplit.get(0).toUpperCase());
                    nameSplit.remove(0);
                }

                type = CardType.valueOf(nameSplit.get(0).toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidCardParameterException();
        }

        return new Card(cardId, name, element, damage, type);
    }
}
