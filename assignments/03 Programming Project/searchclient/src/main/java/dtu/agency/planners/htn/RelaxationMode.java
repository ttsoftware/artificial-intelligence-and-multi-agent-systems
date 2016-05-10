package dtu.agency.planners.htn;

/**
 * Different modes of relaxation,
 * needed when it is necessary to discover a path through cells that are not free
 */
public enum RelaxationMode {
    None,            // All board objects are considered
    NoAgents,        // Boxes and Walls are considered
    NoAgentsOnlyForeignBoxes,  // Only Walls and boxes with different colors are considered
    NoAgentsNoBoxes  // Only Walls are considered
}
