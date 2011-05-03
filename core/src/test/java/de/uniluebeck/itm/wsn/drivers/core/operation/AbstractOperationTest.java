package de.uniluebeck.itm.wsn.drivers.core.operation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.wsn.drivers.core.State;
import de.uniluebeck.itm.wsn.drivers.core.async.AsyncAdapter;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

public class AbstractOperationTest {

	private Operation<Object> operation;
	
	@Before
	public void setUp() {
		operation = new AbstractOperation<Object>() {
			@Override
			public Void execute(ProgressManager progressManager) throws Exception {
				return null;
			}
		};
		operation.setTimeout(1000);
		operation.setAsyncCallback(new AsyncAdapter<Object>());
	}
	
	@Test
	public void testCallSuccess() {
		operation.setAsyncCallback(new AsyncAdapter<Object>() {
			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		// Test success
		try {
			operation.call();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(State.DONE, operation.getState());
	}
	
	@Test
	public void testCallCancel() {
		// Test cancel
		operation.setAsyncCallback(new AsyncAdapter<Object>());
		operation.cancel();
		try {
			operation.call();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(operation.getState(), State.CANCELED);
	}
	
	@Test(expected=Exception.class)
	public void testCallException() throws Exception {
		// Test exception
		Operation<Void> operation = new AbstractOperation<Void>() {
			@Override
			public Void execute(ProgressManager progressManager) throws Exception {
				throw new Exception("Some exception");
			}
		};
		operation.setTimeout(1000);
		operation.setAsyncCallback(new AsyncAdapter<Void>());
		try {
			operation.call();
		} catch (Exception e) {
			Assert.assertEquals(operation.getState(), State.EXCEPTED);
			throw e;
		}
	}
	
	@Test
	public void testCallTimeout() {
		// Test timeout
		Operation<Void> operation = new AbstractOperation<Void>() {
			@Override
			public Void execute(ProgressManager progressManager) throws Exception {
				Thread.sleep(200);
				return null;
			}
		};
		operation.setAsyncCallback(new AsyncAdapter<Void>());
		operation.setTimeout(100);
		try {
			operation.call();
		} catch(Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertEquals(State.TIMEDOUT, operation.getState());
	}

	@Test
	public void testExecuteSubOperation() {
		try {
			Assert.assertNull(operation.execute(null));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCancel() {
		operation.cancel();
		Assert.assertTrue(operation.isCanceled());
	}

	@Test
	public void testIsCanceled() {
		Assert.assertTrue(!operation.isCanceled());
	}

	@Test
	public void testGetTimeout() {
		Operation<Void> operation = new AbstractOperation<Void>() {
			@Override
			public Void execute(ProgressManager progressManager) throws Exception {
				return null;
			}
		};
		operation.setTimeout(100);
		Assert.assertEquals(100, operation.getTimeout());
	}
}
