package de.uniluebeck.itm.devicedriver.util;

import java.util.Timer;
import java.util.TimerTask;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;

/**
 * 
 * @author Malte Legenhausen
 *
 * @param <T>
 */
public class TimedOperationHandleWrapper<T> implements OperationHandle<T> {

	private final OperationHandle<T> operationHandle;
	
	private final Timer timer = new Timer();
	
	public TimedOperationHandleWrapper(OperationHandle<T> operationHandle) {
		this.operationHandle = operationHandle;
	}

	@Override
	public void cancel() {
		timer.cancel();
		operationHandle.cancel();
	}

	@Override
	public T get() {
		return operationHandle.get();
	}
	
	public OperationHandle<T> cancelAfter(final int delay) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				operationHandle.cancel();
			}
		}, delay);
		return this;
	}
}
