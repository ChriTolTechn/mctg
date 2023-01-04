package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.model.User;

public interface Workable {
    public HttpResponse executeRequest(HttpRequest request);

    public User getUserFromDatabase(int id);

    public User getUserFromDatabase(String authenticationToken);
}
