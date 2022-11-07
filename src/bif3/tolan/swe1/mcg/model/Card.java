package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;

import java.util.Objects;

public class Card {
    private final String name;

    private final ElementType element;

    private final CardType cardType;

    private final float damage;

    public Card(String name, ElementType element, float damage, CardType cardType) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.cardType = cardType;
    }

    public Card(String name, ElementType element, float damage) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.cardType = null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return name.equals(card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
