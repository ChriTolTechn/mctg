package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
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
                    case Paths.USER_WORKER_REGISTRATION:
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
            User user = mapper.readValue(jsonString, User.class);

            //Check if user already exists
            User dbUser = userRepository.getByUsername(user.getUsername());
            if (dbUser != null) {
                return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "User with the username \"" + user.getUsername() + "\" already exists");
            } else {
                userRepository.add(user);
                dbUser = userRepository.getByUsername(user.getUsername());
                deckRepository.createDeckForUser(dbUser.getId());
                return new HttpResponse(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User " + user.getUsername() + " successfully created.");
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
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                if (dbUser.getUsername().equals(requestedUser)) {
                    return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, UserUtils.getUserProfile(dbUser));
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
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                if (dbUser.getUsername().equals(requestedUser)) {
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonString = request.getBody();

                    User jsonUser = mapper.readValue(jsonString, User.class);

                    if (jsonUser.getName() != null)
                        dbUser.setName(jsonUser.getName());
                    if (jsonUser.getBio() != null)
                        dbUser.setBio(jsonUser.getBio());
                    if (jsonUser.getImage() != null)
                        dbUser.setImage(jsonUser.getImage());

                    userRepository.updateUser(dbUser);
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
