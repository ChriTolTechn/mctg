package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;

import java.util.Objects;

/**
 * Card class
 *
 * @author Christopher Tolan
 */
public class Card {

    private final String cardId;
    private final String name;

    private final ElementType element;

    private final CardType cardType;

    private final float damage;

    public Card(String cardId, String name, ElementType element, float damage, CardType cardType) {
        this.cardId = cardId;
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.cardType = cardType;
    }

    public String getName() {
        return name;
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
    public int hashCode() {
        return Objects.hash(cardId);
    }
}
