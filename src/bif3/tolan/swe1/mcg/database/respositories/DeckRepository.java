package bif3.tolan.swe1.mcg.database.respositories;

import java.sql.SQLException;

public interface DeckRepository {
    void createDeckForUser(int userId) throws SQLException;

    int getDeckIdForUser(int userId) throws SQLException;
}
