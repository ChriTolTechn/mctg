package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class StoreWorker {
    private ConcurrentHashMap<String, Vector<Card>> packageMap;

    public StoreWorker() {
        //TODO load from database
        packageMap = new ConcurrentHashMap<>();
    }

    public HttpResponse executeRequest(HttpRequest request) {
        String requestedMethod = "";
        if (request.getPathArray().length > 1) {
            requestedMethod = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (requestedMethod) {
            default:
                return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
        }
    }

    /**
     * Buys a package for a user
     *
     * @param user        the user the package will be bought for
     * @param packageName the name of the package
     * @throws PackageNotFoundException   if no package with the specified name was found
     * @throws InsufficientFundsException if the user does not have enough coins to buy the package
     */
    private void buyPackage(User user, String packageName) throws PackageNotFoundException, InsufficientFundsException {
        Vector<Card> wantedPackage = packageMap.get(packageName);

        if (wantedPackage != null) {
            if (user.canPurchase(DefaultValues.DEFAULT_PACKAGE_COST)) {
                user.addCardsToStack(wantedPackage);
                user.payCoins(DefaultValues.DEFAULT_PACKAGE_COST);
            } else {
                throw new InsufficientFundsException();
            }
        } else {
            throw new PackageNotFoundException();
        }
    }
}
