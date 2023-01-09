package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.database.DbConnector;

public class BaseRepository {
    protected DbConnector connector;

    public BaseRepository(DbConnector connector) {
        if (connector != null) {
            this.connector = connector;
        } else {
            throw new NullPointerException("Connection cannot be null");
        }
    }
}
