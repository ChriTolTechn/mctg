package bif3.tolan.swe1.mctg.model;

import bif3.tolan.swe1.mctg.constants.CommonRegex;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mctg.model.enums.CardType;
import bif3.tolan.swe1.mctg.model.enums.ElementType;
import bif3.tolan.swe1.mctg.model.jsonViews.CardViews;
import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static bif3.tolan.swe1.mctg.utils.CardUtils.extractCardType;
import static bif3.tolan.swe1.mctg.utils.CardUtils.extractElementType;

/**
 * Card model class
 *
 * @author Christopher Tolan
 */
@JsonClassDescription("Card")
public class Card {
    @JsonProperty("Id")
    @JsonView({CardViews.CreateCard.class, CardViews.ReadCard.class})
    private String cardId;
    @JsonProperty("Name")
    @JsonView(CardViews.ReadCard.class)
    private String name;
    @JsonProperty("Element")
    @JsonView(CardViews.ReadCard.class)
    private ElementType element;
    @JsonProperty("CardType")
    @JsonView(CardViews.ReadCard.class)
    private CardType cardType;
    @JsonProperty("Damage")
    @JsonView({CardViews.CreateCard.class, CardViews.ReadCard.class})
    private float damage;

    public Card(String cardId, String name, float damage) throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
        setCardElementAndTypeByCardName(name);
    }

    /**
     * Default constructor for jackson
     *
     * @throws UnsupportedCardTypeException
     * @throws UnsupportedElementTypeException
     */
    public Card() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        this.name = "Goblin";
        this.cardId = "";
        this.damage = 0;
        setCardElementAndTypeByCardName(name);
    }

    public String getName() {
        return name;
    }

    @JsonSetter("Name")
    @JsonView(CardViews.CreateCard.class)
    public void setName(String name) throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        this.name = name;

        setCardElementAndTypeByCardName(name);
    }

    public ElementType getElement() {
        return element;
    }

    public float getDamage() {
        return damage;
    }

    @JsonIgnore
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

    private void setCardElementAndTypeByCardName(String name) throws UnsupportedElementTypeException, UnsupportedCardTypeException {
        List<String> nameSplit = new ArrayList<>(List.of(name.split(CommonRegex.SPLIT_STRING_BY_UPPERCASE_LETTERS)));
        if (nameSplit.size() > 0) {
            if (nameSplit.size() == 2) {
                this.element = extractElementType(nameSplit.get(0));
                nameSplit.remove(0);
            } else {
                this.element = ElementType.NORMAL;
            }

            this.cardType = extractCardType(nameSplit.get(0));
        }
    }
}
