package dtu.agency.planners;

import dtu.agency.agent.actions.Action;
import dtu.agency.board.Level;
import dtu.agency.planners.actions.AbstractAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rasmus on 3/23/16.
 */
public class Searcher {

    private Level level;

    public Searcher(Level level) {
        this.level = level;
    }

    public List<Action> Search(AbstractAction action) {
        List<Action> actions = new ArrayList<Action>();



        return actions;
    }
}
