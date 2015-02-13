package de.uniluebeck.itm.wsn.drivers.core.util;

import com.google.common.io.ByteStreams;

import java.io.PrintStream;


/**
 * Utitlity class for muting and unmuting the System.out print stream.
 *
 * @author Malte Legenhausen
 */
public final class SysOutUtil {

    /**
     * Default System.out for this system.
     */
    private static PrintStream defaultSystemOut = System.out;

    /**
     * Constructor.
     */
    private SysOutUtil() {

    }

    /**
     * Mutes the current print stream.
     */
    public static void mute() {
        // Save the old system.out cause it can be manipulated during the execution.
        defaultSystemOut = System.out;
        System.setOut(new PrintStream(ByteStreams.nullOutputStream()));
    }

    /**
     * Restores the default print stream for System.out.
     */
    public static void restore() {
        System.setOut(defaultSystemOut);
    }
}
