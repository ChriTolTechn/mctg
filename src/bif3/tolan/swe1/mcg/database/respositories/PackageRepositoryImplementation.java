package bif3.tolan.swe1.mcg.database.respositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepositoryImplementation extends BaseRepository implements PackageRepository {
    public PackageRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public int createPackageAndGetId() throws SQLException {
        String sql = "INSERT INTO mctg_package DEFAULT VALUES RETURNING *";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) return resultSet.getInt("id");
        throw new NullPointerException();
    }
}
