package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.DefaultValues;
import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.CardRepository;
import bif3.tolan.swe1.mcg.database.respositories.PackageRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
            case POST:
                switch (requestedPath) {
                    case Paths.PACKAGE_WORKER_CREATE:
                        return createPackage(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private synchronized HttpResponse createPackage(HttpRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null && dbUser.getUsername().equals(DefaultValues.ADMIN_USERNAME)) {
                Vector<Card> cards = mapper.readValue(
                        jsonString,
                        mapper.getTypeFactory().constructCollectionType(Vector.class, Card.class));
                for (Card c : cards) {
                    cardRepository.addCard(c);
                }
                int packageId = packageRepository.createPackageAndGetId();
                for (Card c : cards) {
                    cardRepository.assignCardToPackage(c.getCardId(), packageId);
                }

                return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Package created successfully");
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "You are not authorized to perform this action");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (IdExistsException e) {
            return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "Cards with this ID already exist");
        } catch (InvalidCardParameterException e) {
            e.printStackTrace();
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
