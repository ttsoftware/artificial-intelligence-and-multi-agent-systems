package dtu.agency;

public class PlannerClient {

    public static void main(String[] args) throws Exception {

        // We start the plannerClientThread (Actually runs in main thread)
        new PlannerClientThread().run();
    }
}