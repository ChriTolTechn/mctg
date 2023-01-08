package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.PackageRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.PackageNotFoundException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.UserUtils;

import java.sql.SQLException;
import java.util.Vector;

public class StoreWorker implements Workable {

    private UserRepository userRepository;
    private CardRepository cardRepository;
    private PackageRepository packageRepository;

    public StoreWorker(UserRepository userRepository, CardRepository cardRepository, PackageRepository packageRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.packageRepository = packageRepository;
    }

    public HttpResponse executeRequest(HttpRequest request) {
        String requestedPath = "";
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
            case POST:
                switch (requestedPath) {
                    case RequestPaths.SHOP_WORKER_BUY_PACKAGE:
                        return buyPackage(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    /**
     * Buys a package for a user
     */
    private synchronized HttpResponse buyPackage(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            if (requestingUser != null) {
                if (requestingUser.getCoins() >= DefaultValues.PACKAGE_COST) {
                    int nextAvailablePackage = packageRepository.getNextAvailablePackage();

                    Vector<Card> cardsInPackage = cardRepository.getAllCardsByPackageIdAsList(nextAvailablePackage);
                    for (Card c : cardsInPackage) {
                        cardRepository.assignCardToUserStack(c.getCardId(), requestingUser.getId());
                    }

                    packageRepository.deletePackage(nextAvailablePackage);

                    requestingUser.setCoins(requestingUser.getCoins() - DefaultValues.PACKAGE_COST);
                    userRepository.updateUser(requestingUser);

                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Successfully acquired new cards");
                } else {
                    return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "Not enough coins");
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (PackageNotFoundException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No packages available at the moment");
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
