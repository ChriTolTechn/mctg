package bif3.tolan.swe1.mctg.persistence.respositories.interfaces;

import bif3.tolan.swe1.mctg.exceptions.IdExistsException;
import bif3.tolan.swe1.mctg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mctg.exceptions.NoDataException;
import bif3.tolan.swe1.mctg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mctg.model.User;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Repository responsible for accessing user data
 *
 * @author Christopher Tolan
 */
public interface UserRepository {
    User getUserById(int id) throws SQLException, UserDoesNotExistException;

    User getUserByUsername(String username) throws SQLException, UserDoesNotExistException;

    void addNewUser(User user) throws SQLException, InvalidInputException, IdExistsException;

    void updateUser(User user) throws SQLException;

    Vector<User> getUsersOrderedByEloDescendingAsList() throws SQLException, NoDataException;
}
