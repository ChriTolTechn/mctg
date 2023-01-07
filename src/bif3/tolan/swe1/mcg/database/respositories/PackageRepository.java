package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;

import java.sql.SQLException;

public interface PackageRepository {
    public int createPackageAndGetId() throws SQLException;

    public int getPackageWithLowestId() throws SQLException, PackageNotFoundException;

    public void deletePackage(int packageId) throws SQLException;
}
