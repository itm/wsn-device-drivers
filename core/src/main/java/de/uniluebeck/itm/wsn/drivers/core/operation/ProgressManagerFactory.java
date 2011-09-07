package de.uniluebeck.itm.wsn.drivers.core.operation;

import com.google.inject.ImplementedBy;


/**
 * Factory for creating ProgressManagers with an ProgressCallback.
 * 
 * @author Malte Legenhausen
 */
@ImplementedBy(SimpleProgressManagerFactory.class)
public interface ProgressManagerFactory {

	ProgressManager create(ProgressCallback callback);
}
