package bif3.tolan.swe1.mcg.database;

import java.sql.*;

import static bif3.tolan.swe1.mcg.constants.DbConstants.*;

public class DbConnection {
    private Connection connection;

    public DbConnection() {
        establishConnection();
    }

    private void establishConnection() {
        System.out.println("----------------------------------------------");
        System.out.println("Trying to connect to database...");
        System.out.println("----------------------------------------------");
        try {
            connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USERNAME, DB_PASSWORD);
            System.out.println("----------------------------------------------");
            System.out.println("Connected to database...");
            System.out.println("----------------------------------------------");
            test();
        } catch (SQLException e) {
            System.err.println("----------------------------------------------");
            e.printStackTrace();
            System.err.println("----------------------------------------------");
        }
    }

    private void test() throws SQLException {
        //TODO DELETE
        PreparedStatement statement = connection.prepareStatement("SELECT * from mctg_user");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String username = resultSet.getString("username");
            String password_hash = resultSet.getString("password_hash");
            String token = resultSet.getString("token");
            int elo = resultSet.getInt("elo");
            int coins = resultSet.getInt("coins");
            int games_played = resultSet.getInt("games_played");

            System.out.println(id + " " + username + " " + password_hash + " " + token + " " + elo + " " + coins + " " + games_played);
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
