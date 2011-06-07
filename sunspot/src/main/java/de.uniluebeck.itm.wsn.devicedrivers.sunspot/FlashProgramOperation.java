/**********************************************************************************************************************
 * Copyright (c) 2010, coalesenses GmbH                                                                               *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the coalesenses GmbH nor the names of its contributors may be used to endorse or promote     *
 *   products derived from this software without specific prior written permission.                                   *
 *                                                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, *
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE      *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,         *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE *
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF    *
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   *
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                                *
 **********************************************************************************************************************/

package de.uniluebeck.itm.wsn.devicedrivers.sunspot;

import de.uniluebeck.itm.wsn.devicedrivers.generic.IDeviceBinFile;
import de.uniluebeck.itm.wsn.devicedrivers.generic.Operation;
import de.uniluebeck.itm.wsn.devicedrivers.generic.iSenseDeviceOperation;

public class FlashProgramOperation extends iSenseDeviceOperation {

	private SunspotDevice device;

	private IDeviceBinFile program;

	private boolean rebootAfterFlashing;

	public FlashProgramOperation(SunspotDevice device, IDeviceBinFile program, boolean rebootAfterFlashing) {
		super(device);
		this.device = device;
		this.program = program;
		this.rebootAfterFlashing = rebootAfterFlashing;
	}

	// -------------------------------------------------------------------------

	/**
	 *
	 */
	private boolean programFlash() throws Exception {
		SunspotBinFile sunspotProgram = null;
		// Enter programming mode
		if (!device.enterProgrammingMode()) {
			logError("Unable to enter programming mode");
			return false;
		}

		sunspotProgram = (SunspotBinFile) program;



		try {
			device.flash(sunspotProgram.getProgram(),sunspotProgram.name);
		} catch (Exception e) {
			logDebug("Error while configure flash! Operation will be cancelled!");
			device.operationCancelled(this);
			return false;
		}


		// Reboot (if requested by the user)
		if (rebootAfterFlashing) {
			logDebug("Rebooting device");
			device.reset();
		}
		return true;
	}

	/**
	 * @param line
	 */
	public void printLine(byte[] line) {
		for (int i = 0; i < line.length; i++)
			System.out.print(line[i]);
		System.out.println("");
	}

	// -------------------------------------------------------------------------

	/**
	 *
	 */
	public void run() {
		try {
			if (programFlash() && program != null) {
				operationDone(program);
				return;
			}
		} catch (Throwable t) {
			logError("Unhandled error in thread: " + t, t);
			operationDone(t);
			return;
		} finally {
			try {
				device.leaveProgrammingMode();
			} catch (Throwable e) {
				logWarn("Unable to leave programming mode:" + e, e);
			}
		}

		// Indicate failure
		operationDone(null);
	}

	// -------------------------------------------------------------------------

	/**
	 *
	 */
	@Override
	public Operation getOperation() {
		return Operation.PROGRAM;
	}

}
