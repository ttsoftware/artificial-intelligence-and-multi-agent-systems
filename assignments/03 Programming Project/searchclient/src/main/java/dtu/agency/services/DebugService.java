package dtu.agency.services;

import java.util.Collections;

/**
 * Created by koeus on 4/8/16.
 */
public class DebugService {
    public static boolean inDebugMode = false;
    public static int currentIndentation = 0;

    public static String getIndent() {
        return String.join("", Collections.nCopies(currentIndentation, " "));
    }

    public static void changeIndentation(int indentationChange) {
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
