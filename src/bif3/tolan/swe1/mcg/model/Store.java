package bif3.tolan.swe1.mcg.model;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;

public class Store {
    public Dictionary<String, Set<Card>> packages;

    public Store() {
        packages = new Hashtable<String, Set<Card>>();
    }

    public void buyPackage(User user, String packageName) throws PackageNotFoundException, InsufficientFundsException {
        Set<Card> wantedPackage = packages.get(packageName);

        if (wantedPackage != null) {
            user.payForPackages(DefaultValues.PACKAGE_COST);
            user.addCardsToStack(wantedPackage);
            return;
        }
        throw new PackageNotFoundException();
    }
}
