package bif3.tolan.swe1.mcg.constants;

import bif3.tolan.swe1.mcg.enums.ElementType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static class the helps with type based damage calculations
 *
 * @author Christopher Tolan
 */
public final class DamageMap {
    private static final ConcurrentHashMap<ElementType, Map<ElementType, Float>> damageMap =
            new ConcurrentHashMap<>(Map.of(
                    ElementType.NORMAL, Map.of(
                            ElementType.NORMAL, 1f,
                            ElementType.FIRE, 0.5f,
                            ElementType.WATER, 2f),
                    ElementType.FIRE, Map.of(
                            ElementType.NORMAL, 2f,
                            ElementType.FIRE, 1f,
                            ElementType.WATER, 0.5f),
                    ElementType.WATER, Map.of(
                            ElementType.NORMAL, 0.5f,
                            ElementType.FIRE, 2f,
                            ElementType.WATER, 1f
                    )
            ));

    /**
     * Gets the damage multiplicator of the attackers element based on the defenders element
     *
     * @param attack The attacking cards element type
     * @param defend The defending cards element type
     * @return the multiplicator
     */
    public static float GetDamageMultiplicator(ElementType attack, ElementType defend) {
        return damageMap.get(attack).get(defend);
    }
}
