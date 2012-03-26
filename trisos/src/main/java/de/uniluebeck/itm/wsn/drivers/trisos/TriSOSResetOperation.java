package de.uniluebeck.itm.wsn.drivers.trisos;

import com.google.inject.Inject;

import de.uniluebeck.itm.wsn.drivers.core.operation.OperationContext;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;
import de.uniluebeck.itm.wsn.drivers.core.operation.ResetOperation;
import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * TriSOS operation for reseting the TriSOS node.
 * 
 * @author Torsten Teubler
 */
public class TriSOSResetOperation implements ResetOperation {


    /**
     * The configuration
     */
    private TriSOSConfiguration configuration;

    /**
     * Constructor.
     *
     * @param configuration injected by Guice
     */
    @Inject
    public TriSOSResetOperation(TriSOSConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Void run(final ProgressManager progressManager, OperationContext context) throws Exception {

        progressManager.createSub(1.0f);

        // Fetching resetting command string ...
        String resetCommand = configuration.getResetCommandString();
        System.out.println("Execute: " + resetCommand);
        Process p = Runtime.getRuntime().exec(resetCommand);

        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String line;
        while ((line = bri.readLine()) != null) {
            System.out.println(line);
        }
        bri.close();
        while ((line = bre.readLine()) != null) {
            System.err.println(line);
        }
        bre.close();
        // Wait for process to finish ...
        p.waitFor();
        p.destroy();
        progressManager.worked(1.0f);
        progressManager.done();
        return null;
    }
}
