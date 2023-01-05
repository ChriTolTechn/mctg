package bif3.tolan.swe1.mcg.database.respositories;

import java.sql.SQLException;

public interface PackageRepository {
    public int createPackageAndGetId() throws SQLException;
}
