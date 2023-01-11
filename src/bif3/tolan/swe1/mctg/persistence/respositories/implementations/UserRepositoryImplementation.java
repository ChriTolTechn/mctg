package bif3.tolan.swe1.mctg.persistence.respositories.implementations;

import bif3.tolan.swe1.mctg.exceptions.IdExistsException;
import bif3.tolan.swe1.mctg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mctg.exceptions.NoDataException;
import bif3.tolan.swe1.mctg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.persistence.DatabaseConnector;
import bif3.tolan.swe1.mctg.persistence.respositories.BaseRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mctg.utils.UserUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Implementation Repository responsible for accessing user data
 *
 * @author Christopher Tolan
 */
public class UserRepositoryImplementation extends BaseRepository implements UserRepository {

    public UserRepositoryImplementation(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public User getUserById(int id) throws SQLException, UserDoesNotExistException {
        String sql = "SELECT id, username, password_hash, elo, coins, games_played, wins, name, bio, image FROM mctg_user WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return convertResultSetToUserModel(resultSet);
            }
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException, UserDoesNotExistException {
        String sql = "SELECT id, username, password_hash, elo, coins, games_played, wins, name, bio, image FROM mctg_user WHERE username = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return convertResultSetToUserModel(resultSet);
            }
        }
    }

    @Override
    public synchronized void addNewUser(User user) throws SQLException, InvalidInputException, IdExistsException {
        if (UserUtils.isValidNewUser(user)) {
            try {
                getUserByUsername(user.getUsername());
                throw new IdExistsException();
            } catch (UserDoesNotExistException e) {
                String sql = "INSERT INTO mctg_user (username, password_hash, elo, coins, games_played, wins, name, bio, image) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?);";

                try (
                        Connection connection = connector.getDatabaseConnection();
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ) {
                    preparedStatement.setString(1, user.getUsername());
                    preparedStatement.setString(2, user.getPasswordHash());
                    preparedStatement.setInt(3, user.getElo());
                    preparedStatement.setInt(4, user.getCoins());
                    preparedStatement.setInt(5, user.getGamesPlayed());
                    preparedStatement.setInt(6, user.getWins());
                    preparedStatement.setString(7, user.getName());
                    preparedStatement.setString(8, user.getBio());
                    preparedStatement.setString(9, user.getImage());

                    preparedStatement.executeUpdate();
                }
            }
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public synchronized void updateUser(User user) throws SQLException {
        String sql = "UPDATE mctg_user " +
                "SET password_hash = ?, " +
                "elo = ?, " +
                "coins = ?, " +
                "games_played = ?, " +
                "username = ?, " +
                "wins = ?, " +
                "name = ?, " +
                "bio = ?, " +
                "image = ? " +
                "WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, user.getPasswordHash());
            preparedStatement.setInt(2, user.getElo());
            preparedStatement.setInt(3, user.getCoins());
            preparedStatement.setInt(4, user.getGamesPlayed());
            preparedStatement.setString(5, user.getUsername());
            preparedStatement.setInt(6, user.getWins());
            preparedStatement.setString(7, user.getName());
            preparedStatement.setString(8, user.getBio());
            preparedStatement.setString(9, user.getImage());
            preparedStatement.setInt(10, user.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Vector<User> getUsersOrderedByEloDescendingAsList() throws SQLException, NoDataException {
        String sql = "SELECT id, username, elo, games_played, wins FROM mctg_user ORDER BY elo DESC";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            return convertResultSetToUserModelLightAsList(resultSet);
        }
    }

    private Vector<User> convertResultSetToUserModelLightAsList(ResultSet resultSet) throws SQLException, NoDataException {
        Vector<User> users = new Vector<>();

        while (resultSet.next()) {
            String username = resultSet.getString("username");
            int elo = resultSet.getInt("elo");
            int gamesPlayed = resultSet.getInt("games_played");
            int wins = resultSet.getInt("wins");
            int id = resultSet.getInt("id");

            users.add(new User(username, "", elo, 0, gamesPlayed, id, wins, "", "", ""));
        }

        if (users.isEmpty())
            throw new NoDataException();

        return users;
    }

    private User convertResultSetToUserModel(ResultSet res) throws SQLException, UserDoesNotExistException {
        if (res.next()) {
            String username = res.getString("username");
            String passwordHash = res.getString("password_hash");
            int elo = res.getInt("elo");
            int coins = res.getInt("coins");
            int gamesPlayed = res.getInt("games_played");
            int id = res.getInt("id");
            int wins = res.getInt("wins");
            String name = res.getString("name");
            String bio = res.getString("bio");
            String image = res.getString("image");

            return new User(username, passwordHash, elo, coins, gamesPlayed, id, wins, name, bio, image);
        } else {
            throw new UserDoesNotExistException();
        }
    }
}
