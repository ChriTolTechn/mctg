package bif3.tolan.swe1.mctg;

import bif3.tolan.swe1.mctg.constants.DamageMap;
import bif3.tolan.swe1.mctg.constants.DefaultValues;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mctg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.utils.BattleUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class BattleUtilsTest {
    @Test
    public void testCritDamageCalculation() {
        float testfloat1 = 50.0f;
        float testfloat2 = Float.MAX_VALUE;

        float calculatedCritDamage1 = BattleUtils.calculateCritDamage(testfloat1);
        float calculatedCritDamage2 = BattleUtils.calculateCritDamage(testfloat2);

        float expectedFloat1 = testfloat1 * DefaultValues.CRIT_DAMAGE_MULTIPLIER;
        float expectedFloat2 = Float.MAX_VALUE;

        Assertions.assertEquals(expectedFloat1, calculatedCritDamage1);
        Assertions.assertEquals(expectedFloat2, calculatedCritDamage2);
    }

    @Test
    public void testGoblinAttackingDragon() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("1", "Goblin", 10f);
        Card defender = new Card("2", "FireDragon", 10f);

        float damage = BattleUtils.calculateSpecialCaseDamage(attacker, defender);

        Assertions.assertEquals(0, damage, 0.001);
    }

    @Test
    public void testOrkAttackingWizard() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("3", "Ork", 10f);
        Card defender = new Card("4", "Wizard", 10f);

        float damage = BattleUtils.calculateSpecialCaseDamage(attacker, defender);

        Assertions.assertEquals(0, damage, 0.001);
    }

    @Test
    public void testDragonAttackingFireElf() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("5", "Dragon", 10f);
        Card defender = new Card("6", "FireElf", 10f);

        float damage = BattleUtils.calculateSpecialCaseDamage(attacker, defender);

        Assertions.assertEquals(0, damage, 0.001);
    }

    @Test
    public void testWaterSpellAttackingKnight() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("7", "WaterSpell", 10f);
        Card defender = new Card("8", "Knight", 10f);

        float damage = BattleUtils.calculateSpecialCaseDamage(attacker, defender);

        Assertions.assertEquals(Float.MAX_VALUE, damage, 0.001);
    }

    @Test
    public void testFireSpellAttackingKraken() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("9", "FireSpell", 10f);
        Card defender = new Card("10", "Kraken", 10f);

        float damage = BattleUtils.calculateSpecialCaseDamage(attacker, defender);

        Assertions.assertEquals(0, damage, 0.001);
    }

    @Test
    public void testMonsterFightingMonster() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("11", "Elf", 100);
        Card defender = new Card("12", "Kraken", 50);

        float damage = BattleUtils.calculateRegularDamage(attacker, defender);

        Assertions.assertEquals(100, damage, 0.001);
    }

    @Test
    public void testSpellFightingMonster() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("13", "WaterSpell", 100);
        Card defender = new Card("14", "FireOrk", 50);

        float damage = BattleUtils.calculateRegularDamage(attacker, defender);

        Assertions.assertEquals(100 * DamageMap.GetDamageMultiplicator(attacker.getElement(), defender.getElement()), damage, 0.001);
    }

    @Test
    public void testMonsterFightingSpell() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("15", "WaterGoblin", 100);
        Card defender = new Card("16", "FireSpell", 50);

        float damage = BattleUtils.calculateRegularDamage(attacker, defender);

        Assertions.assertEquals(100 * DamageMap.GetDamageMultiplicator(attacker.getElement(), defender.getElement()), damage, 0.001);
    }

    @Test
    public void testSpecialCaseDamageCalculation() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("17", "WaterDragon", 100);
        Card defender = new Card("18", "FireElf", 50);

        float damage = BattleUtils.calculateDamage(attacker, defender);

        Assertions.assertEquals(0, damage, 0.001);
    }

    @Test
    public void testRegularCaseDamageCalculation() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card attacker = new Card("19", "WaterGoblin", 100);
        Card defender = new Card("20", "Wizard", 50);

        float damage = BattleUtils.calculateDamage(attacker, defender);

        Assertions.assertEquals(100, damage, 0.001);
    }

    @Test
    public void testGetRandomCard() {
        Map<String, Integer> map = Map.of("A", 1, "B", 2, "C", 3);

        Integer randomCard = BattleUtils.getRandomCard(map);

        Assertions.assertTrue(randomCard == 1 || randomCard == 2 || randomCard == 3);
    }

    @Test
    public void testEmptyMap() {
        Map<String, Integer> map = Map.of();

        Assertions.assertThrows(IllegalArgumentException.class, () -> BattleUtils.getRandomCard(map));
    }
}
