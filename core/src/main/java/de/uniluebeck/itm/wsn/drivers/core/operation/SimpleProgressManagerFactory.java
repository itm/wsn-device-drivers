package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.Singleton;

/**
 * Simple Factory for creating a RootProgressManager instance.
 * 
 * @author Malte Legenhausen
 */
@Singleton
public class SimpleProgressManagerFactory implements ProgressManagerFactory {

	@Override
	public ProgressManager create(ProgressCallback callback) {
		return new RootProgressManager(callback);
	}

}
