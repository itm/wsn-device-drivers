package de.uniluebeck.itm.wsn.drivers.core.async;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.event.StateChangedEvent;
import de.uniluebeck.itm.wsn.drivers.core.io.LockedInputStream;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;


public class LockedInputStreamManager {

	private final OperationQueue queue;
	
	private final LockedInputStream inputStream;
	
	public LockedInputStreamManager(OperationQueue queue, LockedInputStream inputStream) {
		this.queue = queue;
		this.inputStream = inputStream;
		
		queue.addListener(new OperationQueueAdapter<Object>() {
			@Override
			public void onStateChanged(StateChangedEvent<Object> event) {
				lockIfAnyRunning();
			}
		});
	}
	
	private void lockIfAnyRunning() {
		boolean locked = Iterators.any(queue.getOperations().iterator(), new Predicate<Operation<?>>() {
			@Override
			public boolean apply(Operation<?> input) {
				return State.RUNNING.equals(input.getState());
			}
		});
		inputStream.setLocked(locked);
	}
	
}
