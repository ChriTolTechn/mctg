package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;

public class UserWorker implements Workable {

    private UserRepository userRepository;

    public UserWorker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public HttpResponse executeRequest(HttpRequest request) {
        String requestedMethod = "";
        if (request.getPathArray().length > 1) {
            requestedMethod = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (requestedMethod) {
            case Paths.USER_WORKER_REGISTRATION:
                return RegisterUsers(request);
            default:
                return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
        }
    }

    private HttpResponse RegisterUsers(HttpRequest request) {
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
                return new HttpResponse(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User successfully created. Token:" + user.getToken());
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
}
