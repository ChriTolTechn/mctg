package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class UserWorker implements Workable {

    private List<User> users;

    public UserWorker() {
        users = loadFromDatabase();
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
            boolean userExists = users.stream()
                    .anyMatch(u -> u.getUsername().equals(user.getUsername()));

            if (userExists) {
                return new HttpResponse(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "User with the username \"" + user.getUsername() + "\" already exists");
            } else {
                users.add(user);
                saveToDatabase();
                return new HttpResponse(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User successfully created. Token:" + user.getToken());
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }

    private List<User> loadFromDatabase() {
        //TODO
        return new Vector<>();
    }

    private void saveToDatabase() {
        //TODO
    }
}
