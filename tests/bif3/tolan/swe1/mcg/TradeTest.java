package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.Store;
import bif3.tolan.swe1.mcg.model.User;

public class TradeTest {

    User user1;
    User user2;
    Store store;
    Card card1;
    Card card2;

    /*
    @BeforeEach
    public void setup() {
        user1 = new User("test1", "test1");
        user2 = new User("test2", "test1");

        store = new Store();

        card1 = new Card("FireGoblin", ElementType.FIRE, 500, CardType.Goblin);
        card2 = new Card("WaterSpell", ElementType.WATER, 500, CardType.Spell);

        user1.addCardToStack(card1);
        user2.addCardToStack(card2);
    }

    @Test
    @DisplayName("Testing methods where exceptions should be thrown")
    public void testValidTrade() throws InvalidUserException, HasActiveTradeException {
        Assertions.assertEquals(
                store.hasActiveTrade(user1),
                false,
                "User1 does not have a trade yet");

        Assertions.assertDoesNotThrow(
                () -> store.addForTrade(user1, card1, 100, CardType.Spell),
                "User should be able to add a trade if they do not have one yet");

        Assertions.assertEquals(
                store.hasActiveTrade(user1),
                true,
                "User1 should have a trade now");

        Assertions.assertThrows(
                HasActiveTradeException.class,
                () -> store.addForTrade(user1, card1, 100, CardType.Spell),
                "User should not be able to create a new trade if they already have an open trade");

        Assertions.assertDoesNotThrow(
                () -> store.removeFromTrade(user1),
                "It should be possible to remove the active trade of user1"
        );
    }
     */
}
