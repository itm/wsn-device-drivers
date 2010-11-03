package de.uniluebeck.itm.devicedriver.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.devicedriver.Monitor;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncAdapter;

public class AbstractOperationTest {

	private Operation<Object> operation;
	
	@Before
	public void setUp() {
		operation = new AbstractOperation<Object>() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				return null;
			}
		};
	}
	
	@Test
	public void testCallSuccess() {
		operation.init(1000, new AsyncAdapter<Object>());
		// Test success
		try {
			operation.call();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(operation.getState(), State.DONE);
	}
	
	@Test
	public void testCallCancel() {
		// Test cancel
		operation.init(1000, new AsyncAdapter<Object>());
		operation.cancel();
		try {
			operation.call();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(operation.getState(), State.CANCELED);
	}
	
	@Test
	public void testCallException() {
		// Test exception
		Operation<Void> operation = new AbstractOperation<Void>() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				throw new Exception("Some exception");
			}
		};
		operation.init(1000, new AsyncAdapter<Void>());
		try {
			operation.call();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(operation.getState(), State.EXCEPTED);
		
	}
	
	@Test
	public void testCallTimeout() {
		// Test timeout
		Operation<Void> operation = new AbstractOperation<Void>() {
			@Override
			public Void execute(Monitor monitor) throws Exception {
				Thread.sleep(100);
				return null;
			}
		};
		operation.init(100, new AsyncAdapter<Void>());
		try {
			operation.call();
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(State.TIMEDOUT, operation.getState());
	}

	@Test
	public void testExecuteSubOperation() {
		try {
			assertNull(operation.execute(null));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCancel() {
		operation.cancel();
		assertTrue(operation.isCanceled());
	}

	@Test
	public void testIsCanceled() {
		assertTrue(!operation.isCanceled());
	}

}
