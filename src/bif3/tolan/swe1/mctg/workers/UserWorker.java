package bif3.tolan.swe1.mctg.workers;

import bif3.tolan.swe1.mctg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mctg.constants.RequestHeaders;
import bif3.tolan.swe1.mctg.constants.RequestPaths;
import bif3.tolan.swe1.mctg.exceptions.IdExistsException;
import bif3.tolan.swe1.mctg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mctg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mctg.httpserver.HttpRequest;
import bif3.tolan.swe1.mctg.httpserver.HttpResponse;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.model.jsonViews.UserViews;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mctg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

/**
 * Implementation of Workable responsible for various user data interactions and registration
 *
 * @author Christopher Tolan
 */
public class UserWorker implements Workable {
    private UserRepository userRepository;
    private DeckRepository deckRepository;

    public UserWorker(UserRepository userRepository, DeckRepository deckRepository) {
        this.userRepository = userRepository;
        this.deckRepository = deckRepository;
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
                    case RequestPaths.USER_WORKER_REGISTRATION:
                        return registerUser(request);
                }
            case PUT:
                return editUserData(request, requestedPath);
            case GET:
                return getUserData(request, requestedPath);
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private synchronized HttpResponse registerUser(HttpRequest request) {
        try {
            // Create user from jsonString
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = request.getBody();
            User userTryingToRegister = mapper
                    .readerWithView(UserViews.CreateUser.class)
                    .forType(User.class)
                    .readValue(jsonString);

            try {
                //Check if user already exists
                userRepository.getUserByUsername(userTryingToRegister.getUsername());
                return GenericHttpResponses.USER_EXISTS;
            } catch (UserDoesNotExistException e) {
                // create new user
                userRepository.addNewUser(userTryingToRegister);
                User newlyRegisteredUser = userRepository.getUserByUsername(userTryingToRegister.getUsername());

                // create a deck for new user
                deckRepository.createDeckForUser(newlyRegisteredUser.getId());

                return GenericHttpResponses.SUCCESS_CREATE;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (SQLException | UserDoesNotExistException | IdExistsException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (InvalidInputException e) {
            return GenericHttpResponses.INVALID_INPUT;
        }
    }

    private HttpResponse getUserData(HttpRequest request, String requestedUser) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // check if data for requested user is accessible for the requesting user
            if (requestingUser.getUsername().equals(requestedUser)) {
                // convert user data to json string
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper
                        .writerWithView(UserViews.ReadProfileUser.class)
                        .writeValueAsString(requestingUser);

                // return user data as json
                return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);
            } else {
                return GenericHttpResponses.UNAUTHORIZED;
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }

    private synchronized HttpResponse editUserData(HttpRequest request, String requestedUser) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            // check if the user requested is the same as requesting user
            if (requestingUser.getUsername().equals(requestedUser)) {
                // read data to update from json
                ObjectMapper mapper = new ObjectMapper();
                String newUserDataAsJsonString = request.getBody();
                User newUserWithUpdatedData = mapper
                        .readerWithView(UserViews.EditUser.class)
                        .forType(User.class)
                        .readValue(newUserDataAsJsonString);

                // update data in user object
                if (newUserWithUpdatedData.getName() != null)
                    requestingUser.setName(newUserWithUpdatedData.getName());
                if (newUserWithUpdatedData.getBio() != null)
                    requestingUser.setBio(newUserWithUpdatedData.getBio());
                if (newUserWithUpdatedData.getImage() != null)
                    requestingUser.setImage(newUserWithUpdatedData.getImage());

                // update user in db
                userRepository.updateUser(requestingUser);

                return GenericHttpResponses.SUCCESS_UPDATE;
            } else {
                return GenericHttpResponses.UNAUTHORIZED;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }
}
