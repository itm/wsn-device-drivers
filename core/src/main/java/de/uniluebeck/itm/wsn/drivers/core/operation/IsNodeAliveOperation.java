package de.uniluebeck.itm.wsn.drivers.core.operation;

/**
 * Interface that defines a operation that checks if a node is alive. The implementation of this
 * operation is device-specific and may involve serial port or even wireless communication.
 *
 * @author Daniel Bimschas
 */
public interface IsNodeAliveOperation extends Operation<Boolean> {

}
