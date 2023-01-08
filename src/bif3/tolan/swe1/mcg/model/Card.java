package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.CommonRegex;
import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

import static bif3.tolan.swe1.mcg.utils.CardUtils.extractCardType;
import static bif3.tolan.swe1.mcg.utils.CardUtils.extractElementType;

/**
 * Card class
 *
 * @author Christopher Tolan
 */
public class Card {
    @JsonProperty("Id")
    private String cardId;
    @JsonIgnore
    private String name;
    @JsonIgnore
    private ElementType element;
    @JsonIgnore
    private CardType cardType;
    @JsonProperty("Damage")
    private float damage;

    public Card(String cardId, String name, float damage) throws InvalidCardParameterException {
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
        setCardElementAndTypeByCardName(this.name);
    }

    // Default Constructor for Jackson
    public Card() {
    }

    public String getName() {
        return name;
    }

    @JsonSetter("Name")
    public void setName(String name) throws InvalidCardParameterException {
        this.name = name;

        setCardElementAndTypeByCardName(name);
    }

    public ElementType getElement() {
        return element;
    }

    public float getDamage() {
        return damage;
    }

    public CardType getMonsterType() {
        return cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public CardType getCardType() {
        return cardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardId.equals(card.cardId);
    }

    @Override
    public String toString() {
        return "Id = " + cardId +
                ", Name = " + name +
                ", Element = " + element +
                ", Type = " + cardType +
                ", Damage = " + damage;
    }

    private void setCardElementAndTypeByCardName(String name) throws InvalidCardParameterException {
        List<String> nameSplit = new ArrayList<>(List.of(name.split(CommonRegex.SPLIT_STRING_BY_UPPERCASE_LETTERS)));
        try {
            if (nameSplit.size() > 0) {
                if (nameSplit.size() == 2) {
                    this.element = extractElementType(nameSplit.get(0));
                    nameSplit.remove(0);
                }

                this.cardType = extractCardType(nameSplit.get(0));
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidCardParameterException();
        }
    }
}
