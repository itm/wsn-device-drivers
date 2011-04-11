package de.uniluebeck.itm.devicedriver.operation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.uniluebeck.itm.rsc.drivers.core.Monitor;
import de.uniluebeck.itm.rsc.drivers.core.operation.ChildProgressManager;
import de.uniluebeck.itm.rsc.drivers.core.operation.RootProgressManager;


/**
 * Test for the AbstractProgressManager.
 * 
 * @author Malte Legenhausen
 */
public class ProgressManagerTest {
	
	/**
	 * Delta for the equals methods on float.
	 */
	private static final float DELTA = 0.001f;

	/**
	 * The tested progress manager.
	 */
	private RootProgressManager manager;
	
	/**
	 * The fraction that is returned by the Monitor callback.
	 */
	private float fraction = 0.0f;
	
	/**
	 * SetUp.
	 */
	@Before
	public void setUp() {
		manager = new RootProgressManager(new Monitor() {
			@Override
			public void onProgressChange(final float fraction) {
				ProgressManagerTest.this.fraction = fraction;
			}
		});
		fraction = 0.0f;
	}
	
	/**
	 * Test the normal super working.
	 */
	@Test
	public void testWorked() {
		manager.worked(0.5f);
		Assert.assertEquals(0.5f, fraction, DELTA);
	}
	
	/**
	 * Test the submanager capabilities.
	 */
	@Test
	public void testSubWorked() {
		manager.worked(0.5f);
		final ChildProgressManager subManager = manager.createSub(0.5f);
		subManager.worked(0.5f);
		Assert.assertEquals(0.75f, fraction, DELTA);
		final ChildProgressManager subSubManager = subManager.createSub(0.25f);
		subSubManager.worked(0.25f);
		Assert.assertEquals(0.78125, fraction, DELTA);
		subSubManager.worked(0.75f);
		Assert.assertEquals(0.875, fraction, DELTA);
		subManager.worked(0.25f);
		Assert.assertEquals(1.0f, fraction, DELTA);
	}
	
	@Test
	public void testOnlySubWorked() {
		final ChildProgressManager subManager = manager.createSub(0.25f);
		subManager.worked(0.5f);
		Assert.assertEquals(0.125f, fraction, DELTA);
		subManager.done();
		Assert.assertEquals(0.25f, fraction, DELTA);
		final ChildProgressManager subManager2 = manager.createSub(0.5f);
		subManager2.worked(0.5f);
		Assert.assertEquals(0.5f, fraction, DELTA);
		subManager2.done();
		Assert.assertEquals(0.75f, fraction, DELTA);
		final ChildProgressManager subManager3 = manager.createSub(0.25f);
		subManager3.worked(0.5f);
		Assert.assertEquals(0.875f, fraction, DELTA);
		subManager3.done();
		Assert.assertEquals(1.0f, fraction, DELTA);
	}
	
	@Test
	public void testWrongSubWorked() {
		final ChildProgressManager subManager = manager.createSub(0.25f);
		subManager.worked(0.5f);
		Assert.assertEquals(0.125f, fraction, DELTA);
		subManager.worked(1.0f);
		Assert.assertEquals(0.25f, fraction, DELTA);
	}
	
	/**
	 * Test that negative values are not allowed.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void negativeWorked() {
		manager.worked(-1.0f);
	}
	
	/**
	 * Test that the fraction will set to 1.0f when the done method was called.
	 */
	@Test
	public void testDone() {
		manager.done();
		Assert.assertEquals(1.0f, fraction, DELTA);
	}
}
