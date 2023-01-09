package bif3.tolan.swe1.mcg.database.respositories.interfaces;

import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;

import java.sql.SQLException;

public interface PackageRepository {
    public int createNewPackageAndGetId() throws SQLException, PackageNotFoundException;

    public int getNextAvailablePackage() throws SQLException, PackageNotFoundException;

    public void deletePackage(int packageId) throws SQLException;
}
