package dtu.agency.services;

import dtu.agency.board.Level;

public class BDILevelService extends LevelService {

    private static ThreadLocal<BDILevelService> THREAD_LOCAL = new ThreadLocal() {
        @Override
        protected BDILevelService initialValue() {
            return new BDILevelService();
        }
    };

    public static BDILevelService getInstance() {
        System.err.println("Getting BDILevelService from agent " + Thread.currentThread().getName());
        return THREAD_LOCAL.get();
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
