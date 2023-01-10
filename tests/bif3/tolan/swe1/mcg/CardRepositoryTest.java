package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedCardTypeException;
import bif3.tolan.swe1.mcg.exceptions.UnsupportedElementTypeException;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.persistence.DatabaseConnector;
import bif3.tolan.swe1.mcg.persistence.respositories.implementations.CardRepositoryImplementation;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.CardRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardRepositoryTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private DatabaseConnector mockDatabaseConnector;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private Card card;

    private CardRepository cardRepository;

    @Before
    public void setup() throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

        card = new Card("asdf", "Dragon", 100f);

        when(mockResultSet.getString("id")).thenReturn(card.getCardId());
        when(mockResultSet.getString("name")).thenReturn(card.getName());
        when(mockResultSet.getFloat("damage")).thenReturn(card.getDamage());

        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(true)).when(mockStatement).setString(eq(1), eq("asdf"));
        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(false)).when(mockStatement).setString(eq(1), eq("yxcv"));

        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(true, false)).when(mockStatement).setInt(eq(1), eq(1));
        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(false)).when(mockStatement).setInt(eq(1), eq(2));

        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockDatabaseConnector.getDatabaseConnection()).thenReturn(mockConnection);

        cardRepository = new CardRepositoryImplementation(mockDatabaseConnector);
    }

    @Test
    public void testGetById() throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card testCard1 = cardRepository.getCardById("asdf");
        Card testCard2 = cardRepository.getCardById("yxcv");

        Assertions.assertEquals(card, testCard1);
        Assertions.assertNull(testCard2);
    }

    @Test
    public void testGetBy() throws SQLException, UnsupportedCardTypeException, UnsupportedElementTypeException {
        List<Card> testCardList1 = cardRepository.getAllCardsByUserIdAsList(1);
        List<Card> testCardList2 = cardRepository.getAllCardsByUserIdAsList(2);

        Assertions.assertEquals(1, testCardList1.size());
        Assertions.assertEquals(card, testCardList1.get(0));
        Assertions.assertEquals(0, testCardList2.size());
    }

    @Test
    public void testAddCard() throws UnsupportedCardTypeException, UnsupportedElementTypeException {
        Card card1 = new Card("asdf", "Dragon", 250f);
        Card card2 = new Card("yxcv", "WaterGoblin", 300f);
        Card card3 = new Card("gPoPVKU6YkKJKJ3l83YjRhyC1IOOr18Bp9Cz8w0mt4WYM3Pzdwv", "FireSpell", 200f);

        Assertions.assertThrows(IdExistsException.class, () -> cardRepository.addNewCard(card1));
        Assertions.assertDoesNotThrow(() -> cardRepository.addNewCard(card2));
        Assertions.assertThrows(InvalidInputException.class, () -> cardRepository.addNewCard(card3));
    }
}
