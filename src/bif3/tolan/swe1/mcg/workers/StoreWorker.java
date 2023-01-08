package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.PackageRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.*;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
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
        HttpMethod httpMethod = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        switch (httpMethod) {
            case POST:
                switch (requestedPath) {
                    case RequestPaths.SHOP_WORKER_BUY_PACKAGE:
                        return buyPackage(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private synchronized HttpResponse buyPackage(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            // check if user has enough funds
            if (requestingUser.getCoins() >= DefaultValues.PACKAGE_COST) {
                // get next available package and its cards and assign it to the user
                int nextAvailablePackage = packageRepository.getNextAvailablePackage();
                Vector<Card> cardsInPackage = cardRepository.getAllCardsByPackageIdAsList(nextAvailablePackage);
                for (Card c : cardsInPackage) {
                    cardRepository.assignCardToUserStack(c.getCardId(), requestingUser.getId());
                }

                // delete empty package
                packageRepository.deletePackage(nextAvailablePackage);

                // subtract coins from user and update them
                requestingUser.setCoins(requestingUser.getCoins() - DefaultValues.PACKAGE_COST);
                userRepository.updateUser(requestingUser);

                return GenericHttpResponses.SUCCESS_BUY;
            } else {
                return GenericHttpResponses.NOT_ENOUGH_COINS;
            }
        } catch (SQLException | InvalidInputException | UnsupportedCardTypeException |
                 UnsupportedElementTypeException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (PackageNotFoundException e) {
            return GenericHttpResponses.NOT_AVAILABLE_FOR_PURCHASE;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }
}
