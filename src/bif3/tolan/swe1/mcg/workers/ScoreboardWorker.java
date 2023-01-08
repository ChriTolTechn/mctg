package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.RequestHeaders;
import bif3.tolan.swe1.mcg.constants.RequestPaths;
import bif3.tolan.swe1.mcg.database.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mcg.httpserver.*;
import bif3.tolan.swe1.mcg.model.User;
import bif3.tolan.swe1.mcg.utils.UserUtils;

import java.sql.SQLException;
import java.util.Vector;

public class ScoreboardWorker implements Workable {
    private UserRepository userRepository;

    public ScoreboardWorker(UserRepository userRepository) {
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
                    case RequestPaths.SCOREBOARD_WORKER_GET_SCOREBOARD:
                        return getScoreboard(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getScoreboard(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            User requestingUser = userRepository.getByUsername(username);
            if (requestingUser != null) {
                Vector<User> allUsersOrderedByEloDescending = userRepository.getUsersOrderedByEloDescending();
                String scoreboard = UserUtils.getScoreboard(allUsersOrderedByEloDescending);

                return new HttpResponse(HttpStatus.OK, ContentType.PLAIN_TEXT, scoreboard);
            } else {
                return new HttpResponse(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not logged in");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new HttpResponse(HttpStatus.NOT_ACCEPTABLE, ContentType.PLAIN_TEXT, "The json string is not formatted properly");
    }
}
