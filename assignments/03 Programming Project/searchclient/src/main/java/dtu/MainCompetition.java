package dtu;

import server.CompetitionRunner;

public class MainCompetition {

    public static void main(String[] args) throws Exception {

        // Run the server.jar with the following arguments
        String[] serverArgs = {
                "-d",
                "competition_levels/combined",
                "-c",
                "java -jar out/artifacts/agency_jar/The_Agency.jar"
        };

        CompetitionRunner.main(serverArgs);
    }
}