package bif3.tolan.swe1.mctg.persistence.respositories;

import bif3.tolan.swe1.mctg.persistence.DatabaseConnector;

/**
 * Base class for the repository providing a method to get the connection to the database;
 *
 * @author Christopher Tolan
 */
public class BaseRepository {
    protected DatabaseConnector connector;

    public BaseRepository(DatabaseConnector connector) {
        if (connector != null) {
            this.connector = connector;
        } else {
            throw new NullPointerException("Connection cannot be null");
        }
    }
}
