package bif3.tolan.swe1.mcg.persistence.respositories.implementations;

import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;
import bif3.tolan.swe1.mcg.persistence.PersistenceManager;
import bif3.tolan.swe1.mcg.persistence.respositories.BaseRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.PackageRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepositoryImplementation extends BaseRepository implements PackageRepository {
    public PackageRepositoryImplementation(PersistenceManager connector) {
        super(connector);
    }

    @Override
    public synchronized int createNewPackageAndGetId() throws SQLException, PackageNotFoundException {
        String sql = "INSERT INTO mctg_package DEFAULT VALUES RETURNING *";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new PackageNotFoundException();
            }
        }
    }

    @Override
    public int getNextAvailablePackage() throws SQLException, PackageNotFoundException {
        String sql = "SELECT id FROM mctg_package WHERE id = (SELECT MIN(id) FROM mctg_package)";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new PackageNotFoundException();
            }
        }
    }

    @Override
    public void deletePackage(int packageId) throws SQLException {
        String sql = "DELETE FROM mctg_package WHERE id = ?";

        try (
                Connection connection = connector.getDatabaseConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setInt(1, packageId);

            preparedStatement.executeUpdate();
        }
    }
}
