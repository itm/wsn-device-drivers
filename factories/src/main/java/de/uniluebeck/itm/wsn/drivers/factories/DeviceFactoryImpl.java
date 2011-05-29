/**********************************************************************************************************************
 * Copyright (c) 2010, Institute of Telematics, University of Luebeck                                                 *
 * All rights reserved.                                                                                               *
 *                                                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the   *
 * following conditions are met:                                                                                      *
 *                                                                                                                    *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following *
 *   disclaimer.                                                                                                      *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the        *
 *   following disclaimer in the documentation and/or other materials provided with the distribution.                 *
 * - Neither the name of the University of Luebeck nor the names of its contributors may be used to endorse or        *
 *   promote products derived from this software without specific prior written permission.                           *
 *                                                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, *
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE      *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,         *
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE *
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF    *
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   *
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                                *
 **********************************************************************************************************************/

package de.uniluebeck.itm.wsn.drivers.factories;

import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.serialport.SerialPortConnection;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicDevice;
import de.uniluebeck.itm.wsn.drivers.mock.MockDevice;
import de.uniluebeck.itm.wsn.drivers.nulldevice.NullConnection;
import de.uniluebeck.itm.wsn.drivers.nulldevice.NullDevice;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateDevice;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbDevice;

public class DeviceFactoryImpl implements DeviceFactory {

	@Override
	public <C extends Connection> Device<? extends Connection> create(DeviceType deviceType, C connection) {
		switch (deviceType) {
			case ISENSE:
				return new JennicDevice((SerialPortConnection) connection);
			case PACEMATE:
				return new PacemateDevice((SerialPortConnection) connection);
			case TELOSB:
				return new TelosbDevice((SerialPortConnection) connection);
			case MOCK:
				return new MockDevice(connection);
			case NULL:
				return new NullDevice((NullConnection) connection);
		}
		throw new RuntimeException("Unhandled device type \"" + deviceType
				+ "\". Maybe someone forgot to add this (new) device type to " + ConnectionFactoryImpl.class.getName()
				+ "?"
		);
	}

	@Override
	public <C extends Connection> Device<? extends Connection> create(String deviceType, C connection) {
		return create(DeviceType.fromString(deviceType), connection);
	}

}
