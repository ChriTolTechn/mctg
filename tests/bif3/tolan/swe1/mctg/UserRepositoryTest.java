package bif3.tolan.swe1.mctg;

import bif3.tolan.swe1.mctg.exceptions.IdExistsException;
import bif3.tolan.swe1.mctg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mctg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.persistence.DatabaseConnector;
import bif3.tolan.swe1.mctg.persistence.respositories.implementations.UserRepositoryImplementation;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
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
    private DatabaseConnector mockDatabaseConnector;

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

        user = new User("test", "test", 100, 100, 100, 1, 10, "", "", "");

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
        when(mockDatabaseConnector.getDatabaseConnection()).thenReturn(mockConnection);

        userRepository = new UserRepositoryImplementation(mockDatabaseConnector);
    }

    @Test
    public void testGetById() throws SQLException, UserDoesNotExistException {
        User testUser1 = userRepository.getUserById(1);

        Assertions.assertEquals(user, testUser1);
        Assertions.assertThrows(UserDoesNotExistException.class, () -> userRepository.getUserById(2));
    }

    @Test
    public void testGetByUserName() throws SQLException, UserDoesNotExistException {
        User testUser = userRepository.getUserByUsername("test");
        Assertions.assertEquals(user, testUser);

        Assertions.assertThrows(UserDoesNotExistException.class, () -> userRepository.getUserByUsername("notTest"));
    }

    @Test
    public void testAddUser() {
        User user1 = new User("admin", "test", 100, 100, 100, 1, 10, "", "", "");
        User user2 = new User("test", "test", 100, 100, 100, 2, 10, "", "", "");
        User user3 = new User("gPoPVKU6YkKJKJ3l83YjRhyC1IOOr18Bp9Cz8w0mt4WYM3Pzdwv",
                "test", 100, 100, 100, 100, 10, "", "", "");

        Assertions.assertDoesNotThrow(() -> userRepository.addNewUser(user1));
        Assertions.assertThrows(IdExistsException.class, () -> userRepository.addNewUser(user2));
        Assertions.assertThrows(InvalidInputException.class, () -> userRepository.addNewUser(user3));
    }
}
