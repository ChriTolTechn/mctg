package bif3.tolan.swe1.mctg.persistence.respositories.interfaces;

import java.sql.SQLException;

/**
 * Repository responsible for accessing card deck
 *
 * @author Christopher Tolan
 */
public interface DeckRepository {
    void createDeckForUser(int userId) throws SQLException;

    int getDeckIdByUserId(int userId) throws SQLException;
}
