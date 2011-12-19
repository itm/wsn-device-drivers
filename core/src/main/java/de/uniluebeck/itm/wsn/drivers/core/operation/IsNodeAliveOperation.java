package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.ImplementedBy;

/**
 * Interface that defines the operation for testing if the device is alive.
 *
 * @author TLMAT UC
 */
@ImplementedBy(SimpleIsNodeAliveOperation.class)
public interface IsNodeAliveOperation extends OperationRunnable<Boolean> {

}
