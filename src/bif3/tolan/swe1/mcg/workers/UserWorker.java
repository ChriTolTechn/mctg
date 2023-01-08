package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
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

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private synchronized HttpResponse registerUser(HttpRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        try {
            // Create user from jsonString
            User userTryingToRegister = mapper.readValue(jsonString, User.class);

            //Check if user already exists
            User registeredUserWithSameUsername = userRepository.getUserByUsername(userTryingToRegister.getUsername());
            if (registeredUserWithSameUsername != null) {
                return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "User with the username \"" + userTryingToRegister.getUsername() + "\" already exists");
            } else {
                userRepository.addNewUser(userTryingToRegister);
                User newlyRegisteredUser = userRepository.getUserByUsername(userTryingToRegister.getUsername());

                deckRepository.createDeckForUser(newlyRegisteredUser.getId());

                return new HttpResponse(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User " + newlyRegisteredUser.getUsername() + " successfully created.");
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        } catch (IdExistsException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private HttpResponse getUserData(HttpRequest request, String requestedUser) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            if (requestingUser != null) {
                if (requestingUser.getUsername().equals(requestedUser)) {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, UserUtils.getUserProfile(requestingUser));
                } else {
                    return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "You are not authorized to access this information");
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private synchronized HttpResponse editUserData(HttpRequest request, String requestedUser) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);
            if (requestingUser != null) {
                if (requestingUser.getUsername().equals(requestedUser)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String newUserDataAsJsonString = request.getBody();

                    User newUserWithUpdatedData = mapper.readValue(newUserDataAsJsonString, User.class);

                    if (newUserWithUpdatedData.getName() != null)
                        requestingUser.setName(newUserWithUpdatedData.getName());
                    if (newUserWithUpdatedData.getBio() != null)
                        requestingUser.setBio(newUserWithUpdatedData.getBio());
                    if (newUserWithUpdatedData.getImage() != null)
                        requestingUser.setImage(newUserWithUpdatedData.getImage());

                    userRepository.updateUser(requestingUser);
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "User data updates successfully!");
                } else {
                    return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "You are not authorized to access this information");
                }
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
