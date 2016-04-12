package dtu.agency.planners.htn;

/**
 * Created by koeus on 4/11/16.
 */
public enum RelaxationMode {
    None,            // All board objects are considered
    NoAgents,        // Boxes and Walls are considered
    NoAgentsNoBoxes  // Only Walls are considered
}
