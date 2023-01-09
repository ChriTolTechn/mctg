package bif3.tolan.swe1.mcg;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.persistence.respositories.implementations.UserRepositoryImplementation;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.UserRepository;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {
    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private User user;

    private UserRepository userRepository;

    @Before
    public void setup() throws SQLException {
        when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);

        user = new User("test", "test", 100, 100, 100);

        when(mockResultSet.getInt("elo")).thenReturn(user.getElo());
        when(mockResultSet.getInt("coins")).thenReturn(user.getCoins());
        when(mockResultSet.getInt("games_played")).thenReturn(user.getGamesPlayed());
        when(mockResultSet.getString("username")).thenReturn(user.getUsername());
        when(mockResultSet.getString("password_hash")).thenReturn(user.getPasswordHash());

        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(true)).when(mockStatement).setInt(eq(1), eq(1));
        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(false)).when(mockStatement).setInt(eq(1), eq(2));
        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(true)).when(mockStatement).setString(eq(1), eq("test"));
        doAnswer(invocationOnMock -> when(mockResultSet.next()).thenReturn(false)).when(mockStatement).setString(eq(1), eq("notTest"));
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);

        userRepository = new UserRepositoryImplementation(mockConnection);
    }

    @Test
    public void testGetById() throws SQLException {
        User testUser1 = userRepository.getUserById(1);
        User testUser2 = userRepository.getUserById(2);

        Assertions.assertEquals(user, testUser1);
        Assertions.assertNull(testUser2);
    }

    @Test
    public void testGetByUserName() throws SQLException {
        User testUser = userRepository.getUserByUsername("test");
        Assertions.assertEquals(user, testUser);

        testUser = userRepository.getUserByUsername("notTest");
        Assertions.assertNull(testUser);
    }

    @Test
    public void testAddUser() throws InvalidInputException, SQLException, IdExistsException {
        User user1 = new User("admin", "test", 100, 100, 100);
        User user2 = new User("test", "test", 100, 100, 100);
        User user3 = new User("gPoPVKU6YkKJKJ3l83YjRhyC1IOOr18Bp9Cz8w0mt4WYM3Pzdwv",
                "test", 100, 100, 100);

        Assertions.assertDoesNotThrow(() -> userRepository.addNewUser(user1));
        Assertions.assertThrows(IdExistsException.class, () -> userRepository.addNewUser(user2));
        Assertions.assertThrows(InvalidInputException.class, () -> userRepository.addNewUser(user3));
    }
}
