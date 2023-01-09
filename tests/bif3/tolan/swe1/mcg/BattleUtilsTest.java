package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.utils.BattleUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class BattleUtilsTest {
    @Test
    public void critDamageCalculationTest() {
        float testfloat1 = 50.0f;
        float testfloat2 = Float.MAX_VALUE;

        float calculatedCritDamage1 = BattleUtils.calculateCritDamage(testfloat1);
        float calculatedCritDamage2 = BattleUtils.calculateCritDamage(testfloat2);

        float expectedFloat1 = testfloat1 * DefaultValues.CRIT_DAMAGE_MULTIPLIER;
        float expectedFloat2 = Float.MAX_VALUE;

        Assertions.assertEquals(expectedFloat1, calculatedCritDamage1);
        Assertions.assertEquals(expectedFloat2, calculatedCritDamage2);
    }

}
