package bif3.tolan.swe1.mcg.database.respositories.implementations;

import bif3.tolan.swe1.mcg.database.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.PackageRepository;
import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepositoryImplementation extends BaseRepository implements PackageRepository {
    public PackageRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public synchronized int createNewPackageAndGetId() throws SQLException {
        String sql = "INSERT INTO mctg_package DEFAULT VALUES RETURNING *";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        int packageId;

        if (resultSet.next()) {
            packageId = resultSet.getInt("id");
        } else {
            packageId = -1;
        }

        resultSet.close();
        preparedStatement.close();

        //TODO exception
        return packageId;
    }

    @Override
    public int getNextAvailablePackage() throws SQLException, PackageNotFoundException {
        String sql = "SELECT id FROM mctg_package WHERE id = (SELECT MIN(id) FROM mctg_package)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();

        int packageId;

        if (resultSet.next()) {
            packageId = resultSet.getInt("id");
        } else {
            packageId = -1;
        }

        resultSet.close();
        preparedStatement.close();

        if (packageId == -1) {
            throw new PackageNotFoundException();
        } else {
            return packageId;
        }
    }

    @Override
    public void deletePackage(int packageId) throws SQLException {
        String sql = "DELETE FROM mctg_package WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, packageId);

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }
}
