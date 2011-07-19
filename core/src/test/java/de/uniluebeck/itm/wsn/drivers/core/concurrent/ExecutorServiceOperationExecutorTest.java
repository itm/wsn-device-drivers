package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.util.concurrent.FakeTimeLimiter;

import de.uniluebeck.itm.wsn.drivers.core.OperationAdapter;
import de.uniluebeck.itm.wsn.drivers.core.OperationCallback;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class ExecutorServiceOperationExecutorTest {
	
	private OperationExecutor queue;
	
	@Before
	public void setUp() {
		queue = new ExecutorServiceOperationExecutor();
	}
	
	@Test
	public void testAddOperation() throws InterruptedException, ExecutionException {
		
		OperationCallback<Boolean> callback = new OperationAdapter<Boolean>() {

			@Override
			public void onCancel() {
				fail("No cancel was triggered");
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
				fail("No failure was expected");
			}
		};
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(ProgressManager progressManager) throws Exception {
				return true;
			}
		};
		operation.setTimeLimiter(new FakeTimeLimiter());
		operation.setTimeout(1000);
		operation.setAsyncCallback(callback);
		OperationFuture<Boolean> handle = queue.submitOperation(operation);
		if (!handle.get()) {
			fail("Execution failed");
		}
	}

	@Test
	public void testGetOperations() {
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(ProgressManager progressManager) throws Exception {
				Thread.sleep(100);
				return true;
			}
		};
		operation.setTimeout(1000);
		queue.submitOperation(operation);
		Assert.assertTrue(!queue.getOperations().isEmpty());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(queue.getOperations().isEmpty());
	}
	
	@Test(expected=ExecutionException.class)
	public void testOperationHandleException() throws ExecutionException, InterruptedException {
		Operation<Boolean> operation = new AbstractOperation<Boolean>() {
			@Override
			public Boolean execute(ProgressManager progressManager) throws Exception {
				Thread.sleep(500);
				throw new NullPointerException();
			}
		};
		operation.setTimeout(1000);
		final OperationFuture<Boolean> handle = queue.submitOperation(operation);
		handle.get();
	}
}
