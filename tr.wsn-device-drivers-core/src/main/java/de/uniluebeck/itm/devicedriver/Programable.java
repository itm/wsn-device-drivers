package de.uniluebeck.itm.devicedriver;

import de.uniluebeck.itm.devicedriver.operation.EnterProgramModeOperation;
import de.uniluebeck.itm.devicedriver.operation.LeaveProgramModeOperation;


/**
 * Interface for devices that need to enter or leave a mode for programming.
 * 
 * @author Malte Legenhausen
 */
public interface Programable {

	/**
	 * Create a operation for entering the programming mode of the device.
	 * 
	 * @return A new operation instance for entering the programming mode.
	 */
	EnterProgramModeOperation createEnterProgramModeOperation();
	
	/**
	 * Create operation for leaving the programming mode of the device.
	 * 
	 * @return A new operation instance for leaving the programming mode.
	 */
	LeaveProgramModeOperation createLeaveProgramModeOperation();
}
