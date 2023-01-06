package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.PackageRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.InsufficientFundsException;
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
                    case Paths.SHOP_WORKER_BUY_PACKAGE:
                        return buyPackage(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    /**
     * Buys a package for a user
     */
    private HttpResponse buyPackage(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                if (dbUser.canPurchase(DefaultValues.DEFAULT_PACKAGE_COST)) {
                    int nextPackage = packageRepository.getPackageWithLowestId();
                    Vector<Card> cards = cardRepository.getCardPackageByPackageId(nextPackage);
                    for (Card c : cards) {
                        cardRepository.assignCardToUserStack(c.getCardId(), dbUser.getId());
                    }
                    packageRepository.deletePackage(nextPackage);
                    dbUser.payCoins(DefaultValues.DEFAULT_PACKAGE_COST);
                    userRepository.updateUser(dbUser);
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
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
        } catch (PackageNotFoundException e) {
            return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "No packages available at the moment");
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
