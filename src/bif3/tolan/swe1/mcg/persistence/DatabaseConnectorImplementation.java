package bif3.tolan.swe1.mcg.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static bif3.tolan.swe1.mcg.constants.DatabaseConstants.*;

public class DatabaseConnectorImplementation implements DatabaseConnector {
    @Override
    public Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME, DB_USERNAME, DB_PASSWORD);
    }
}
