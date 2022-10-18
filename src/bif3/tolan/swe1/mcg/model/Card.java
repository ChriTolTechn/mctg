package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.enums.MonsterType;

import java.util.Objects;

public class Card {
    private final String name;

    private final ElementType element;

    private final MonsterType monsterType;

    private final float damage;

    public Card(String name, ElementType element, float damage, MonsterType monsterType) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.monsterType = monsterType;
    }

    public Card(String name, ElementType element, float damage) {
        this.name = name;
        this.element = element;
        this.damage = damage;
        this.monsterType = null;
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

    public MonsterType getMonsterType() {
        return monsterType;
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
