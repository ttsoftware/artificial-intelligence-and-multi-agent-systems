package dtu.agency.services;

import java.util.Collections;

public class DebugService {
    public enum DebugLevel {
        LOW,
        MEDIUM,
        HIGH,
        HIGHEST,
        PICKED
    }

    private static boolean inDebugMode = false;
    private static int currentIndentation = 0;
    private static DebugLevel debugLevel = DebugLevel.LOW;

    private static String getIndent() {
        return String.join("", Collections.nCopies(currentIndentation, " "));
    }

    private static void changeIndentation(int indentationChange) {
        currentIndentation += indentationChange;
    }

    public static void setDebugLevel(DebugLevel debugLevel) {
        DebugService.debugLevel = debugLevel;
    }

    public static boolean inDebugMode(){return inDebugMode;}

    public static boolean setDebugMode(boolean newDebugMode) {
        boolean oldDebugMode = inDebugMode;
        inDebugMode = newDebugMode;
        return oldDebugMode;
    }

    public static void print(String msg, int indentationChange) {
        if (DebugService.inDebugMode) {
            String indent = DebugService.getIndent();
            DebugService.changeIndentation(indentationChange);

            switch (debugLevel) {
                case LOW: // print only first level of debug messages  indent < 2
                    if (currentIndentation>3) return;
                    break;

                case MEDIUM:
                    if (currentIndentation>5) return;
                    break;

                case HIGH:
                    if (currentIndentation>7) return;
                    break;

                case HIGHEST:
                    if (currentIndentation>9) return;
                    break;

                case PICKED: // get only handpicked debug messages
                    if (currentIndentation<20) return;
                    indent="";
                    break;
            }
            System.err.println( "(debug)" + indent + ((msg == null) ? "NULL-DEBUG-MESSAGE!" : msg ) );
        }
    }
}
