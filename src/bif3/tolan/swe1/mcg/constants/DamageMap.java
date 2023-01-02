package bif3.tolan.swe1.mcg.constants;

import bif3.tolan.swe1.mcg.enums.ElementType;

import java.util.Map;

public final class DamageMap {

    private static final Map<ElementType, Map<ElementType, Float>> damageMap = Map.of(
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
    );

    public static float GetDamageMultiplicator(ElementType attack, ElementType defend) {
        return damageMap.get(attack).get(defend);
    }
}
