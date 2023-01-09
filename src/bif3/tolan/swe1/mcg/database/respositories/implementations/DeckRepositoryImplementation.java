package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.DbConnector;
import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.DeckRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeckRepositoryImplementation extends BaseRepository implements DeckRepository {
    public DeckRepositoryImplementation(DbConnector connector) {
        super(connector);
    }

    @Override
    public synchronized void createDeckForUser(int userId) throws SQLException {
        if (getDeckIdByUserId(userId) == -1) {
            String sql = "INSERT INTO mctg_deck (user_id) VALUES (?);";

            try (
                    Connection connection = connector.getConnection();
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
                Connection connection = connector.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("id") : -1;
            }
        }
    }
}
