package bif3.tolan.swe1.mcg.enums;

/**
 * Enum that handles Card Types
 *
 * @author Christopher Tolan
 */
public enum CardType {
    GOBLIN(CardGroup.MONSTER),
    DRAGON(CardGroup.MONSTER),
    WIZARD(CardGroup.MONSTER),
    ORK(CardGroup.MONSTER),
    KNIGHT(CardGroup.MONSTER),
    KRAKEN(CardGroup.MONSTER),
    ELF(CardGroup.MONSTER),
    SPELL(CardGroup.SPELL);

    private CardGroup cardGroup;

    CardType(CardGroup cardGroup) {
        this.cardGroup = cardGroup;
    }

    public boolean isInGroup(CardGroup cardGroup) {
        return this.cardGroup == cardGroup;
    }

    /**
     * Enum that is used to assign the CardType enum to a group
     *
     * @author Christopher Tolan
     */
    public enum CardGroup {
        MONSTER,
        SPELL;
    }
}
