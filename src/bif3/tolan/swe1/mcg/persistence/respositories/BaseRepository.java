package bif3.tolan.swe1.mcg.persistence.respositories;

import bif3.tolan.swe1.mcg.persistence.PersistenceManager;

public class BaseRepository {
    protected PersistenceManager connector;

    public BaseRepository(PersistenceManager connector) {
        if (connector != null) {
            this.connector = connector;
        } else {
            throw new NullPointerException("Connection cannot be null");
        }
    }
}
