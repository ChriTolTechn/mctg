package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepositoryImplementation extends BaseRepository implements UserRepository {
    public UserRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public User getById(int id) throws SQLException {
        String sql = "SELECT username, password_hash, elo, coins, games_played FROM mctg_user WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, id);

        ResultSet res = preparedStatement.executeQuery();
        preparedStatement.close();

        return extractUser(res);
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT username, password_hash, elo, coins, games_played FROM mctg_user WHERE username = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, username);

        ResultSet res = preparedStatement.executeQuery();
        preparedStatement.close();

        return extractUser(res);
    }

    @Override
    public void add(User user) throws SQLException, InvalidInputException, IdExistsException {
        if (isValidNewUser(user)) {
            if (getByUsername(user.getUsername()) != null) {
                throw new IdExistsException();
            }

            String sql = "INSERT INTO mctg_user (username, password_hash, elo, coins, games_played) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setInt(3, user.getElo());
            preparedStatement.setInt(4, user.getCoins());
            preparedStatement.setInt(5, user.getGamesPlayed());

            preparedStatement.executeQuery();
            preparedStatement.close();
        } else {
            throw new InvalidInputException();
        }
    }

    private boolean isValidNewUser(User user) throws SQLException {
        if (user.getUsername().length() > 50) return false;

        return true;
    }

    private User extractUser(ResultSet res) throws SQLException {
        if (res.first()) {
            String username = res.getString("username");
            String passwordHash = res.getString("password_hash");
            int elo = res.getInt("elo");
            int coins = res.getInt("coins");
            int gamesPlayed = res.getInt("games_played");

            return new User(username, passwordHash, elo, coins, gamesPlayed);
        } else {
            return null;
        }
    }
}
