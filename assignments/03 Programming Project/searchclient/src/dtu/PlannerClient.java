package dtu;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PlannerClient {

    public static void main(String[] args) throws Exception {

        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));

        // Use stderr to print to console
        System.err.println("PlannerClient initializing. I am sending this using the error output stream.");

        // Create the level
        Level level = ProblemMarshaller.marshall(serverMessages);


    }
}
