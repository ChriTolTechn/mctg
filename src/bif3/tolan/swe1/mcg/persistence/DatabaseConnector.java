package bif3.tolan.swe1.mcg.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseConnector {
    Connection getDatabaseConnection() throws SQLException;
}
