package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
import bif3.tolan.swe1.mcg.httpserver.*;
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
        Method method = request.getMethod();

        if (request.getPathArray().length > 1) {
            requestedPath = request.getPathArray()[1];
        }

        // Executes requested methods
        switch (method) {
            case GET:
                switch (requestedPath) {
                    case Paths.STATISTICS_WORKER_GET_STATS:
                        return getStatistics(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getStatistics(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, UserUtils.getUserStatsAsFormattedString(dbUser));
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
