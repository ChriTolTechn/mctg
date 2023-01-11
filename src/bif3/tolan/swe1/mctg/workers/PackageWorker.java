package bif3.tolan.swe1.mctg.workers;

import bif3.tolan.swe1.mctg.constants.DefaultValues;
import bif3.tolan.swe1.mctg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mctg.constants.RequestHeaders;
import bif3.tolan.swe1.mctg.constants.RequestPaths;
import bif3.tolan.swe1.mctg.exceptions.*;
import bif3.tolan.swe1.mctg.httpserver.HttpRequest;
import bif3.tolan.swe1.mctg.httpserver.HttpResponse;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mctg.model.Card;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.model.jsonViews.CardViews;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.CardRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.PackageRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mctg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Implementation of Workable responsible for creating cards and packages
 *
 * @author Christopher Tolan
 */
public class PackageWorker implements Workable {
    private UserRepository userRepository;
    private CardRepository cardRepository;
    private PackageRepository packageRepository;

    public PackageWorker(UserRepository userRepository, CardRepository cardRepository, PackageRepository packageRepository) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
        this.packageRepository = packageRepository;
    }

    @Override
    public HttpResponse executeRequest(HttpRequest request) {
        String requestedPath = "";
        HttpMethod httpMethod = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        switch (httpMethod) {
            case POST:
                switch (requestedPath) {
                    case RequestPaths.PACKAGE_WORKER_CREATE:
                        return createPackage(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private synchronized HttpResponse createPackage(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // check if user is admin
            if (requestingUser.getUsername().equals(DefaultValues.ADMIN_USERNAME)) {
                // read cards to be created from json
                ObjectMapper mapper = new ObjectMapper();
                String newCardsAsJsonString = request.getBody();
                Vector<Card> newCards = mapper
                        .readerWithView(CardViews.CreateCard.class)
                        .forType(mapper.getTypeFactory().constructCollectionType(Vector.class, Card.class))
                        .readValue(newCardsAsJsonString);

                // trying to add all new cards
                for (Card c : newCards) {
                    cardRepository.addNewCard(c);
                }

                // create new package in database and get id
                int newCardPackageId = packageRepository.createNewPackageAndGetId();

                // assign new cards to package
                for (Card c : newCards) {
                    cardRepository.assignCardToPackage(c.getCardId(), newCardPackageId);
                }

                return GenericHttpResponses.SUCCESS_CREATE;
            } else {
                return GenericHttpResponses.UNAUTHORIZED;
            }
        } catch (SQLException | PackageNotFoundException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (InvalidInputException e) {
            return GenericHttpResponses.INVALID_INPUT;
        } catch (IdExistsException e) {
            return GenericHttpResponses.ID_EXISTS;
        } catch (UnsupportedCardTypeException e) {
            return GenericHttpResponses.UNSUPPORTED_CARD_TYPE;
        } catch (UnsupportedElementTypeException e) {
            return GenericHttpResponses.UNSUPPORTED_CARD_ELEMENT;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }
}
