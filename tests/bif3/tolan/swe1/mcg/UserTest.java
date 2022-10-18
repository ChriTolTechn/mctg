package bif3.tolan.swe1.mcg;


import bif3.tolan.swe1.mcg.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    User user;

    @BeforeEach
    public void Setup() {
        user = new User("crazyJoe", "verySecure");
    }

    @Test
    @DisplayName("Testing methods where exceptions should be thrown")
    public void testExceptions() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            user.hasUserCardInStack(null);
        }, "Should throw IllegalArgumentException if provided card is null");
    }
}
