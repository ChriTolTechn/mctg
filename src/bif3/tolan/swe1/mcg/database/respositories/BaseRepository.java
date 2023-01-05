package bif3.tolan.swe1.mcg.database.respositories;

import java.sql.Connection;

public class BaseRepository {
    protected Connection connection;

    public BaseRepository(Connection connection) {
        if (connection != null) {
            this.connection = connection;
        } else {
            throw new NullPointerException("Connection cannot be null");
        }
    }
}
