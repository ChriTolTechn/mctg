package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

public class LoginWorker implements Workable {

    private UserRepository userRepository;

    public LoginWorker(UserRepository userRepository) {
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
            case Paths.LOGIN_WORKER_LOGIN:
                return loginUser(request);
            default:
                return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
        }
    }

    private HttpResponse loginUser(HttpRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        try {
            // Create user from jsonString
            User jsonUser = mapper.readValue(jsonString, User.class);

            // Get user from the database
            User dbUser = userRepository.getByUsername(jsonUser.getUsername());

            if (dbUser == null) {
                return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User with name: " + jsonUser.getUsername() + " does not exist");
            } else if (jsonUser.getPasswordHash().equals(dbUser.getPasswordHash())) {
                return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, "Successfully logged in. Session token: " + dbUser.getToken());
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid credentials");
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
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
