package dtu.agency.services;

import dtu.agency.board.Agent;
import dtu.agency.board.Level;

public class BDILevelService extends LevelService {
    Agent agent = BDIService.getAgent();
    private static BDILevelService instance;

    public static BDILevelService getInstance() {
        if (instance == null) {
            instance = new BDILevelService();
        }
        System.err.println("Getting BDILevelService from agent " + Thread.currentThread().getName());
        return instance;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
