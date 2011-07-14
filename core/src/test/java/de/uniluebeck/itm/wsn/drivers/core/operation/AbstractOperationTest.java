package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.common.util.concurrent.FakeTimeLimiter;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import de.uniluebeck.itm.tr.util.ExecutorUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.wsn.drivers.core.OperationAdapter;
import de.uniluebeck.itm.wsn.drivers.core.exception.TimeoutException;
import de.uniluebeck.itm.wsn.drivers.core.operation.AbstractOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.Operation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgressManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	}
	
	@Test
	public void testCallSuccess() {
		operation.setTimeLimiter(new FakeTimeLimiter());
		operation.setAsyncCallback(new OperationAdapter<Object>() {
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
		try {
			operation.call();
		} catch (Exception e) {
			Assert.assertEquals(operation.getState(), State.EXCEPTED);
			throw e;
		}
	}
	
	@Test(expected=TimeoutException.class)
	public void testCallTimeout() throws Exception {
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final SimpleTimeLimiter timeLimiter = new SimpleTimeLimiter(executor);
		try {
			// Test timeout
			Operation<Void> operationUnderTest = new AbstractOperation<Void>() {
				@Override
				public Void execute(ProgressManager progressManager) throws Exception {
					Thread.sleep(200);
					return null;
				}
			};
			operationUnderTest.setTimeout(100);
			operationUnderTest.setTimeLimiter(timeLimiter);
			try {
				operationUnderTest.call();
			} catch(Exception e) {
				Assert.assertEquals(State.TIMEDOUT, operationUnderTest.getState());
				throw e;
			}
		} finally {
			ExecutorUtils.shutdown(executor, 0, TimeUnit.SECONDS);
		}
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
