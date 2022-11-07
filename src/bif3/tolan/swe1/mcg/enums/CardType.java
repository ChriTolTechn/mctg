package bif3.tolan.swe1.mcg.enums;

public enum CardType {
    Goblin(CardGroup.Monster),
    Dragon(CardGroup.Monster),
    Wizard(CardGroup.Monster),
    Ork(CardGroup.Monster),
    Knight(CardGroup.Monster),
    Kraken(CardGroup.Monster),
    Elf(CardGroup.Monster),
    Spell(CardGroup.Spell);

    private CardGroup cardGroup;

    CardType(CardGroup cardGroup) {
        this.cardGroup = cardGroup;
    }

    public boolean isInGroup(CardGroup cardGroup) {
        return this.cardGroup == cardGroup;
    }

    public enum CardGroup {
        Monster,
        Spell;
    }
}
