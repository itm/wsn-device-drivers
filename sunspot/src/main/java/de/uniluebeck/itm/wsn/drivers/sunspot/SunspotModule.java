package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.Device;

import java.util.Map;


public class SunspotModule extends AbstractModule {

    private Map<String, String> baseStationConfiguration;

    public SunspotModule(Map<String, String> baseStationConfiguration) {
        this.baseStationConfiguration = baseStationConfiguration;
    }

    @Override
    protected void configure() {
        bind(Device.class).to(SunspotDevice.class);
        bind(Connection.class).to(SunspotDevice.class);
        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("deviceConfiguration")).toInstance(baseStationConfiguration);
        bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("baseStationConfiguration")).toInstance(baseStationConfiguration);
    }
}
