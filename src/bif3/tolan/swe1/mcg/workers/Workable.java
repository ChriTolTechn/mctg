package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;

public interface Workable {
    public HttpResponse executeRequest(HttpRequest request);
}
