package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class UserRepositoryImplementation extends BaseRepository implements UserRepository {

    public UserRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, elo, coins, games_played, wins, name, bio, image FROM mctg_user WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, id);

        ResultSet res = preparedStatement.executeQuery();

        return extractUser(res);
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, elo, coins, games_played, wins, name, bio, image FROM mctg_user WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, username);

        ResultSet res = preparedStatement.executeQuery();

        return extractUser(res);
    }

    @Override
    public void add(User user) throws SQLException, InvalidInputException, IdExistsException {
        if (isValidNewUser(user)) {
            if (getByUsername(user.getUsername()) != null) {
                throw new IdExistsException();
            }

            String sql = "INSERT INTO mctg_user (username, password_hash, elo, coins, games_played, wins, name, bio, image) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

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
        } else {
            throw new InvalidInputException();
        }
    }

    @Override
    public User updateUser(User user) throws SQLException {
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
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

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
        return getById(user.getId());
    }

    @Override
    public Vector<User> getUsersOrderedByElo() throws SQLException {
        String sql = "SELECT username, elo, games_played, wins FROM mctg_user ORDER BY elo DESC";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        Vector<User> users = new Vector<>();
        while (resultSet.next()) {
            String username = resultSet.getString("username");
            int elo = resultSet.getInt("elo");
            int gamesPlayed = resultSet.getInt("games_played");
            int wins = resultSet.getInt("wins");

            users.add(new User(username, "", elo, 0, gamesPlayed, 0, wins, "", "", ""));
        }
        return users;
    }

    private boolean isValidNewUser(User user) {
        if (user == null) return false;
        if (user.getUsername() == null || user.getName() == null || user.getBio() == null || user.getImage() == null)
            return false;
        if (user.getUsername().length() > 50 || user.getName().length() > 50) return false;
        if (user.getUsername().isEmpty()) return false;
        if (user.getName().length() > 255 || user.getBio().length() > 255) return false;

        return true;
    }

    private User extractUser(ResultSet res) throws SQLException {
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
            return null;
        }
    }
}
