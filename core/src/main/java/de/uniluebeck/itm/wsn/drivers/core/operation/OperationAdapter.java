package de.uniluebeck.itm.wsn.drivers.core.operation;



/**
 * Adapter class for the <code>OperationListener</code>.
 * 
 * @author Malte Legenhausen
 *
 * @param <T> Return type of the operation.
 */
public class OperationAdapter<T> implements OperationListener<T> {

	@Override
	public void beforeStateChanged(StateChangedEvent<T> event) {
		
	}
	
	@Override
	public void afterStateChanged(StateChangedEvent<T> event) {
		
	}
}
