package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.enums.CardType;
import bif3.tolan.swe1.mcg.enums.ElementType;
import bif3.tolan.swe1.mcg.exceptions.*;
import bif3.tolan.swe1.mcg.model.Battle;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Vector;

public class BattleTest {
    User user1;
    User user2;
    Battle battle;

    @BeforeEach
    public void setup() {
        user1 = new User("test1", "test1");
        user2 = new User("test2", "test1");
    }

    @Test
    @DisplayName("User 1 wins because")
    public void TestMixedBattle() throws InvalidDeckException, InvalidUserException, CardsNotInStackException, CardStackNullException, InvalidDeckSizeException, BattleFinishedException {
        user1.addCardToStack(new Card("FireGoblin", ElementType.FIRE, 400, CardType.Goblin));
        user2.addCardToStack(new Card("WaterDragon", ElementType.WATER, 600, CardType.Dragon));
        user1.addCardToStack(new Card("NormalWizard", ElementType.NORMAL, 700, CardType.Wizard));
        user2.addCardToStack(new Card("WaterOrk", ElementType.WATER, 800, CardType.Ork));
        user1.addCardToStack(new Card("FireKnight", ElementType.FIRE, 650, CardType.Knight));
        user2.addCardToStack(new Card("WaterSpell", ElementType.WATER, 500, CardType.Spell));
        user1.addCardToStack(new Card("NormalKraken", ElementType.NORMAL, 550, CardType.Kraken));
        user2.addCardToStack(new Card("FireElf", ElementType.FIRE, 350, CardType.Elf));

        user1.setDeck(new Vector<>(user1.getStack()));
        user2.setDeck(new Vector<>(user2.getStack()));

        battle = new Battle(user1, user2);

        while (battle.getGameFinished() == false) {
            battle.nextRound();
        }

        System.out.println(battle.getBattleLog());
        Assertions.assertTrue(battle.getGameFinished());

        if (user1.getElo() > 1000) {
            Assertions.assertTrue(user2.getElo() < 1000);
        } else {
            Assertions.assertTrue(user2.getElo() > 1000);
        }
    }
}
