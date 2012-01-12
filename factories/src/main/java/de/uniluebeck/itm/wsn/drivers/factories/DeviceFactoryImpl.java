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

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Singleton;
import de.uniluebeck.itm.wsn.drivers.core.Device;
import de.uniluebeck.itm.wsn.drivers.core.DeviceModule;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicModule;
import de.uniluebeck.itm.wsn.drivers.mock.MockModule;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateModule;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbModule;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Singleton
public class DeviceFactoryImpl implements DeviceFactory {

	@Override
	public Device create(ExecutorService executorService, DeviceType deviceType) {
		return create(executorService, deviceType, null);
	}

	@Override
	public Device create(ExecutorService executorService, String deviceType) {
		return create(executorService, DeviceType.fromString(deviceType), null);
	}

	@Override
	public Device create(final ExecutorService executorService, final DeviceType deviceType,
						 @Nullable final Map<String, String> configuration) {

		Module deviceModule;

		switch (deviceType) {
			case ISENSE:
				deviceModule = new JennicModule(configuration);
				break;
			case PACEMATE:
				deviceModule = new PacemateModule(configuration);
				break;
			case TELOSB:
				deviceModule = new TelosbModule(configuration);
				break;
			case MOCK:
				deviceModule = new MockModule(configuration);
				break;
			default:
				throw new RuntimeException("Unknown device type \"" + deviceType + "\". Maybe someone forgot to add"
						+ "this (new) device type to " + DeviceFactoryImpl.class.getName() + "?"
				);
		}

		return Guice.createInjector(new DeviceModule(executorService), deviceModule).getInstance(Device.class);
	}

	@Override
	public Device create(final ExecutorService executorService, final String deviceType,
						 @Nullable final Map<String, String> configuration) {

		return create(executorService, DeviceType.fromString(deviceType), configuration);
	}

}
