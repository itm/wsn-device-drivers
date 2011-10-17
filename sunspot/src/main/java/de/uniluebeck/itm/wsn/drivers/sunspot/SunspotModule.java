package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.AbstractModule;
import de.uniluebeck.itm.wsn.drivers.core.Device;

/**
 * Created by IntelliJ IDEA.
 * User: evangelos
 * Date: 10/10/11
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class SunspotModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Device.class).to(SunspotDevice.class);
    }
}
