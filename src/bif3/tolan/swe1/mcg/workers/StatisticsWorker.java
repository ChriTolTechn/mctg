package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.UserUtils;

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
            // check if user is logged in
            if (requestingUser != null) {
                // return user stats
                return new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, UserUtils.getUserStats(requestingUser));
            } else {
                return GenericHttpResponses.NOT_LOGGED_IN;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        }
    }
}
