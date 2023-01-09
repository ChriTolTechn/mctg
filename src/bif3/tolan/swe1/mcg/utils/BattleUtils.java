package bif3.tolan.swe1.mcg.utils;

import bif3.tolan.swe1.mcg.constants.DamageMap;
import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.enums.CardType;
import bif3.tolan.swe1.mcg.model.enums.ElementType;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BattleUtils {
    public static float calculateCritDamage(float damage) {
        float result = damage * DefaultValues.CRIT_DAMAGE_MULTIPLIER;

        if (result > Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        } else {
            return result;
        }
    }

    /**
     * Calculate damage based on special cases defined in the requirements
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     */
    public static float calculateSpecialCaseDamage(Card attacker, Card defender) {
        switch (attacker.getMonsterType()) {
            // goblins cant attack dragons
            case GOBLIN:
                if (defender.getMonsterType() == CardType.DRAGON)
                    return 0;
                return -1;
            // orks cant attack wizards
            case ORK:
                if (defender.getMonsterType() == CardType.WIZARD)
                    return 0;
                return -1;
            // dragons cant attack elves with a fire type
            case DRAGON:
                if (defender.getMonsterType() == CardType.ELF && defender.getElement() == ElementType.FIRE)
                    return 0;
                return -1;
            case SPELL:
                // Knights are instant K.O.'d by Water spells
                if (attacker.getElement() == ElementType.WATER) {
                    if (defender.getMonsterType() == CardType.KNIGHT)
                        return Float.MAX_VALUE;
                    // Kraken dont get damaged by spell cards
                } else if (defender.getMonsterType() == CardType.KRAKEN)
                    return 0;
                return -1;
            default:
                return -1;
        }
    }

    /**
     * Calculates damage the regular way
     * If both cards are monsters, its just about their base damage
     * If any card is a spell card, the damage is determined by their base damage multiplied with the value the damage map returns based on the defender
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     */
    public static float calculateRegularDamage(Card attacker, Card defender) {
        if (attacker.getMonsterType().isInGroup(CardType.CardGroup.MONSTER)
                && defender.getMonsterType().isInGroup(CardType.CardGroup.MONSTER)) {
            // Fights between Monster don't affect their damage output
            return attacker.getDamage();
        } else if (attacker.getMonsterType().isInGroup(CardType.CardGroup.SPELL)
                || defender.getMonsterType().isInGroup(CardType.CardGroup.SPELL)) {
            // Fights with at least one spell card involved will trigger effectiveness
            float damageMultiplicator = DamageMap.GetDamageMultiplicator(attacker.getElement(), defender.getElement());
            return attacker.getDamage() * damageMultiplicator;
        }
        // Return should not be reachable in normal cases
        return -1;
    }

    public static boolean didCrit() {
        float randomValue = ThreadLocalRandom.current().nextFloat();
        return randomValue < DefaultValues.CRIT_PROBABILITY;
    }

    /**
     * Calculates damage by checking if any of the special cases are met described in the requirement or calculates it with regular settings
     *
     * @param attacker The attacking card
     * @param defender The defending card
     * @return damage that the attacker deals to the defender as float value
     * @throws IllegalStateException if something went wrong during damage calculation
     */
    public static float calculateDamage(Card attacker, Card defender) throws IllegalStateException {
        float damage = -1;

        // Check if there are special cases in battle
        damage = calculateSpecialCaseDamage(attacker, defender);

        // If there was no special case, regular damage calculation is applied
        if (damage == -1) {
            damage = calculateRegularDamage(attacker, defender);
        }

        // Should be unreachable
        if (damage == -1) {
            throw new IllegalStateException("Card-State not valid for damage calculation");
        }

        return damage;
    }

    /**
     * Gets a random value from a map
     * Using a thread safe random
     *
     * @param map Map from which the value should be extracted
     * @param <K> Key object type
     * @param <V> Value object type
     * @return Random value
     */
    public static <K, V> V getRandomCard(Map<K, V> map) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Object[] keys = map.keySet().toArray();
        K randomKey = (K) keys[random.nextInt(keys.length)];
        return map.get(randomKey);
    }
}
