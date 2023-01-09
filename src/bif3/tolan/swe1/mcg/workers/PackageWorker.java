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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

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
                // read cards from json to be created
                ObjectMapper mapper = new ObjectMapper();
                String newCardsAsJsonString = request.getBody();
                Vector<Card> newCards = mapper.readValue(
                        newCardsAsJsonString,
                        mapper.getTypeFactory().constructCollectionType(Vector.class, Card.class));

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
