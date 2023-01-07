package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.constants.Headers;
import bif3.tolan.swe1.mcg.constants.Paths;
import bif3.tolan.swe1.mcg.database.respositories.UserRepository;
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
                    case Paths.SCOREBOARD_WORKER_GET_SCOREBOARD:
                        return getScoreboard(request);
                }
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private HttpResponse getScoreboard(HttpRequest request) {
        String authorizationToken = request.getHeaderMap().get(Headers.AUTH_HEADER);
        String username = UserUtils.getUsernameFromToken(authorizationToken);

        try {
            User dbUser = userRepository.getByUsername(username);
            if (dbUser != null) {
                Vector<User> userStats = userRepository.getUsersOrderedByElo();
                String scoreboard = UserUtils.getScoreboard(userStats);
                
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
