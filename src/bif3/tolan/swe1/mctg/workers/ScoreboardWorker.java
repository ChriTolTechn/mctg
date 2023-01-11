package bif3.tolan.swe1.mctg.workers;

import bif3.tolan.swe1.mctg.constants.GenericHttpResponses;
import bif3.tolan.swe1.mctg.constants.RequestHeaders;
import bif3.tolan.swe1.mctg.constants.RequestPaths;
import bif3.tolan.swe1.mctg.exceptions.NoDataException;
import bif3.tolan.swe1.mctg.exceptions.UserDoesNotExistException;
import bif3.tolan.swe1.mctg.httpserver.HttpRequest;
import bif3.tolan.swe1.mctg.httpserver.HttpResponse;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpMethod;
import bif3.tolan.swe1.mctg.httpserver.enums.HttpStatus;
import bif3.tolan.swe1.mctg.model.User;
import bif3.tolan.swe1.mctg.model.jsonViews.UserViews;
import bif3.tolan.swe1.mctg.persistence.respositories.interfaces.UserRepository;
import bif3.tolan.swe1.mctg.utils.UserUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.Vector;

/**
 * Implementation of Workable responsible for the scoreboard
 *
 * @author Christopher Tolan
 */
public class ScoreboardWorker implements Workable {
    private UserRepository userRepository;

    public ScoreboardWorker(UserRepository userRepository) {
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
                    case RequestPaths.SCOREBOARD_WORKER_GET_SCOREBOARD:
                        return getScoreboard(request);
                }
        }

        return GenericHttpResponses.INVALID_PATH;
    }

    private HttpResponse getScoreboard(HttpRequest request) {
        // get username from token
        String authorizationToken = request.getHeaderMap().get(RequestHeaders.AUTH_HEADER);
        String username = UserUtils.extractUsernameFromToken(authorizationToken);

        try {
            userRepository.getUserByUsername(username);

            // get all users orderd by elo descending and get scoreboard as json string
            Vector<User> allUsersOrderedByEloDescending = userRepository.getUsersOrderedByEloDescendingAsList();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper
                    .writerWithView(UserViews.ReadStatsUser.class)
                    .writeValueAsString(allUsersOrderedByEloDescending);

            // return scoreboard as json
            return new HttpResponse(HttpStatus.OK, HttpContentType.JSON, jsonString);

        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
            return GenericHttpResponses.INTERNAL_ERROR;
        } catch (UserDoesNotExistException e) {
            return GenericHttpResponses.INVALID_TOKEN;
        } catch (NoDataException e) {
            return GenericHttpResponses.NO_DATA;
        }
    }
}
