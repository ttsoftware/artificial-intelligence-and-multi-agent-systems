package dtu.agency.services;

import java.util.Collections;

public class DebugService {
    private static boolean inDebugMode = false;
    private static int currentIndentation = 0;

    private static String getIndent() {
        return String.join("", Collections.nCopies(currentIndentation, " "));
    }

    private static void changeIndentation(int indentationChange) {
        currentIndentation += indentationChange;
    }

    public static void print(String msg, int indentationChange) {
        if (DebugService.inDebugMode) {
            if (msg == null) {
                System.err.println( DebugService.getIndent() + "NULL-DEBUG-MESSAGE!");
            } else {
                System.err.println( DebugService.getIndent() + msg);
            }
            DebugService.changeIndentation(indentationChange);
        }
    }

    public static boolean setDebugMode(boolean newDebugMode) {
        boolean oldDebugMode = inDebugMode;
        inDebugMode = newDebugMode;
        return oldDebugMode;
    }
}
