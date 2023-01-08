package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

public class LoginWorker implements Workable {

    private UserRepository userRepository;

    public LoginWorker(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                    case RequestPaths.LOGIN_WORKER_LOGIN:
                        return loginUser(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse loginUser(HttpRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        try {
            // create user from input data
            User loginValues = mapper.readValue(jsonString, User.class);

            User requestedUser = userRepository.getUserByUsername(loginValues.getUsername());
            // Check if passwords match
            if (loginValues.getPasswordHash().equals(requestedUser.getPasswordHash())) {
                return new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Successfully logged in. Session token: " + requestedUser.getToken());
            } else {
                return GenericHttpResponses.WRONG_CREDENTIALS;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INVALID_INPUT;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.WRONG_CREDENTIALS;
        }
    }
}
