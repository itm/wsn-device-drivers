package de.uniluebeck.itm.devicedriver.async.singlethread;

import static org.junit.Assert.fail;

import org.junit.Test;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.Operation;

public class SingleThreadOperationQueueTest {

	@Test
	public void testAddOperation() {
		SingleThreadOperationQueue queue = new SingleThreadOperationQueue();		
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(Monitor monitor) throws Exception {
				System.out.println("Execute");
				return true;
			}
		};
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				System.out.println("Success");
			}

			@Override
			public void onProgressChange(float fraction) {
				System.out.println("Progress " + fraction);
			}

			@Override
			public void onCancel() {
				fail("No cancel was triggered");
			}

			@Override
			public void onFailure(Throwable throwable) {
				fail("No failure was expected");
			}
		};
		OperationHandle<Boolean> handle = queue.addOperation(operation, 1000, callback);
		if (!handle.get()) {
			fail("Execution failed");
		}
	}

	@Test
	public void testGetOperations() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddListener() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveQueueListener() {
		fail("Not yet implemented");
	}

}
