package bif3.tolan.swe1.mctg.workers;

import bif3.tolan.swe1.mctg.httpserver.HttpRequest;
import bif3.tolan.swe1.mctg.httpserver.HttpResponse;

/**
 * Interface for workers that handle HTTP requests
 *
 * @author Christopher Tolan
 */
public interface Workable {
    /**
     * executes a HTTP request and returns a HTTP response
     *
     * @param request The HTTP request to be executed
     * @return HTTP response with information about the results of the request execution
     */
    HttpResponse executeRequest(HttpRequest request);
}
