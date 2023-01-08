package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.DeckRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeckRepositoryImplementation extends BaseRepository implements DeckRepository {
    public DeckRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public synchronized void createDeckForUser(int userId) throws SQLException {
        if (getDeckIdByUserId(userId) == -1) {
            String sql = "INSERT INTO mctg_deck (user_id) VALUES (?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, userId);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }

    @Override
    public int getDeckIdByUserId(int userId) throws SQLException {
        String sql = "SELECT id FROM mctg_deck WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, userId);

        ResultSet resultSet = preparedStatement.executeQuery();

        int deckId;

        if (resultSet.next()) {
            deckId = resultSet.getInt("id");
        } else {
            deckId = -1;
        }

        resultSet.close();
        preparedStatement.close();

        return deckId;
    }
}
