package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.DeckRepository;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.IdExistsException;
import bif3.tolan.swe1.mcg.exceptions.InvalidInputException;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;

public class RegistrationWorker implements Workable {

    private UserRepository userRepository;

    private DeckRepository deckRepository;

    public RegistrationWorker(UserRepository userRepository, DeckRepository deckRepository) {
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
                    case Paths.REGISTRATION_WORKER_REGISTRATION:
                        return registerUser(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse registerUser(HttpRequest request) {
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
}
