package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

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
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        try {
            // Create user from jsonString
            User userTryingToRegister = mapper.readValue(jsonString, User.class);

            //Check if user already exists
            try {
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

            if (requestingUser.getUsername().equals(requestedUser)) {
                return new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, UserUtils.getUserProfile(requestingUser));
            } else {
                return GenericHttpResponses.UNAUTHORIZED;
            }
        } catch (SQLException e) {
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
                // read data to update
                ObjectMapper mapper = new ObjectMapper();
                String newUserDataAsJsonString = request.getBody();
                User newUserWithUpdatedData = mapper.readValue(newUserDataAsJsonString, User.class);

                // update data
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
