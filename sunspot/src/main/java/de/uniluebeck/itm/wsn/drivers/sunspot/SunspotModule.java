package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.AbstractModule;

import java.util.Map;


public class SunspotModule extends AbstractModule {

    private final Map<String, String> configuration;

    public SunspotModule(final Map<String, String> configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
		// TODO implement
    }
}
