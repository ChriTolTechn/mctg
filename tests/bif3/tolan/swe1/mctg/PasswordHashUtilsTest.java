package bif3.tolan.swe1.mctg;

import bif3.tolan.swe1.mctg.utils.PasswordHashUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PasswordHashUtilsTest {
    @Test
    public void testHashPassword() {
        String password = "password123";
        String passwordHash = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f";

        String hashedPassword = PasswordHashUtils.hashPassword(password);

        Assertions.assertNotEquals(password, hashedPassword);
        Assertions.assertEquals(passwordHash, hashedPassword);
    }
}
