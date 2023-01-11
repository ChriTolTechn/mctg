package bif3.tolan.swe1.mctg.persistence.respositories.implementations;

import bif3.tolan.swe1.mctg.persistence.DatabaseConnector;
import bif3.tolan.swe1.mctg.persistence.respositories.BaseRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.DeckRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation Repository responsible for accessing deck data
 *
 * @author Christopher Tolan
 */
public class DeckRepositoryImplementation extends BaseRepository implements DeckRepository {
    public DeckRepositoryImplementation(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public synchronized void createDeckForUser(int userId) throws SQLException {
        if (getDeckIdByUserId(userId) == -1) {
            String sql = "INSERT INTO mctg_deck (user_id) VALUES (?);";

            try (
                    Connection connection = connector.getDatabaseConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setInt(1, userId);

                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public int getDeckIdByUserId(int userId) throws SQLException {
        String sql = "SELECT id FROM mctg_deck WHERE user_id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : -1;
            }
        }
    }
}
