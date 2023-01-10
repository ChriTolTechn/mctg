package bif3.tolan.swe1.mcg.persistence.respositories;

import bif3.tolan.swe1.mcg.persistence.DatabaseConnector;

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
