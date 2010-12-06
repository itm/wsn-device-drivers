package de.uniluebeck.itm.devicedriver.async.singlethread;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.operation.AbstractOperation;
import de.uniluebeck.itm.devicedriver.operation.Operation;

public class SingleThreadOperationQueueTest {

	private OperationQueue queue;
	
	@Before
	public void setUp() {
		queue = new SingleThreadOperationQueue();
	}
	
	@Test
	public void testAddOperation() {
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(Monitor monitor) throws Exception {
				System.out.println("Execute");
				return true;
			}
		};
		AsyncCallback<Boolean> callback = new AsyncAdapter<Boolean>() {
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
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(Monitor monitor) throws Exception {
				Thread.sleep(100);
				return true;
			}
		};
		queue.addOperation(operation, 1000, new AsyncAdapter<Boolean>());
		Assert.assertTrue(!queue.getOperations().isEmpty());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(queue.getOperations().isEmpty());
	}
}
