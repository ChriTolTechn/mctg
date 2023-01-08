package bif3.tolan.swe1.mcg.database.respositories.interfaces;

import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.model.User;

import java.sql.SQLException;
import java.util.Vector;

public interface UserRepository {
    User getUserById(int id) throws SQLException;

    User getUserByUsername(String username) throws SQLException;

    void addNewUser(User user) throws SQLException, InvalidInputException, IdExistsException;

    User updateUser(User user) throws SQLException;

    Vector<User> getUsersOrderedByEloDescendingAsList() throws SQLException;
}
