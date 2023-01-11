package bif3.tolan.swe1.mctg.persistence.respositories.interfaces;

import bif3.tolan.swe1.mctg.exceptions.PackageNotFoundException;

import java.sql.SQLException;

/**
 * Repository responsible for accessing package data
 *
 * @author Christopher Tolan
 */
public interface PackageRepository {
    public int createNewPackageAndGetId() throws SQLException, PackageNotFoundException;

    public int getNextAvailablePackage() throws SQLException, PackageNotFoundException;

    public void deletePackage(int packageId) throws SQLException;
}
