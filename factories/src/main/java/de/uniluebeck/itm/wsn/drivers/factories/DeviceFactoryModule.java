package de.uniluebeck.itm.wsn.drivers.factories;

import com.google.inject.AbstractModule;

@SuppressWarnings("unused")
public class DeviceFactoryModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DeviceFactory.class).to(DeviceFactoryImpl.class);
	}
}
