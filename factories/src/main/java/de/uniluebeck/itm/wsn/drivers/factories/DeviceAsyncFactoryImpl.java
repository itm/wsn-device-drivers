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

import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Singleton;

import de.uniluebeck.itm.wsn.drivers.core.async.DeviceAsync;
import de.uniluebeck.itm.wsn.drivers.core.async.ExecutorServiceOperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.OperationQueue;
import de.uniluebeck.itm.wsn.drivers.core.async.QueuedDeviceAsync;
import de.uniluebeck.itm.wsn.drivers.jennic.JennicModule;
import de.uniluebeck.itm.wsn.drivers.mock.MockModule;
import de.uniluebeck.itm.wsn.drivers.pacemate.PacemateModule;
import de.uniluebeck.itm.wsn.drivers.telosb.TelosbModule;

public class DeviceAsyncFactoryImpl implements DeviceAsyncFactory {
	
	private class FactoryModule extends AbstractModule {
		
		private ScheduledExecutorService executorService;
		
		public FactoryModule(ScheduledExecutorService executorService) {
			this.executorService = executorService;
		}
		
		@Override
		protected void configure() {
			bind(ScheduledExecutorService.class).toInstance(executorService);
			bind(OperationQueue.class).to(ExecutorServiceOperationQueue.class).in(Singleton.class);
			bind(DeviceAsync.class).to(QueuedDeviceAsync.class).in(Singleton.class);
		}
	}
	
	@Override
	public DeviceAsync create(ScheduledExecutorService executorService, DeviceType deviceType) {
		Module deviceModule = null;
		switch (deviceType) {
		case ISENSE:
			deviceModule = new JennicModule();
			break;
		case PACEMATE:
			deviceModule = new PacemateModule();
			break;
		case TELOSB:
			deviceModule = new TelosbModule();
			break;
		case MOCK:
			deviceModule = new MockModule();
			break;
		default:
			throw new RuntimeException("Unhandled device type \"" + deviceType
					+ "\". Maybe someone forgot to add this (new) device type to " + DeviceAsyncFactoryImpl.class.getName()
					+ "?"
			);
		}
		
		return Guice.createInjector(new FactoryModule(executorService), deviceModule).getInstance(DeviceAsync.class);
	}

	@Override
	public DeviceAsync create(ScheduledExecutorService executorService, String deviceType) {
		return create(executorService, DeviceType.fromString(deviceType));
	}

}
