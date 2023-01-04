package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.model.User;

abstract class BaseWorker implements Workable {
    public User getUserFromDatabase(int id) {
        //TODO
        return new User();
    }

    public User getUserFromDatabase(String authenticationToken) {
        //TODO
        return new User();
    }
}
