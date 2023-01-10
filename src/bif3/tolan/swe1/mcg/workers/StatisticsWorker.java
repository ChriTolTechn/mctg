package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.json.UserViews;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;

public class StatisticsWorker implements Workable {

    private UserRepository userRepository;

    public StatisticsWorker(UserRepository userRepository) {
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
            case GET:
                switch (requestedPath) {
                    case RequestPaths.STATISTICS_WORKER_GET_STATS:
                        return getStatistics(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse getStatistics(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getUserByUsername(username);

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper
                    .writerWithView(UserViews.ReadStatsUser.class)
                    .writeValueAsString(requestingUser);
            // return user stats
            return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        }
    }
}
