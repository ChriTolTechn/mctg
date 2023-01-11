package bif3.tolan.swe1.mctg.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Connection handler for the repositories
 *
 * @author Christopher Tolan
 */
public interface DatabaseConnector {
    /**
     * Returns a connection to a database
     *
     * @return Connection
     * @throws SQLException
     */
    Connection getDatabaseConnection() throws SQLException;
}
