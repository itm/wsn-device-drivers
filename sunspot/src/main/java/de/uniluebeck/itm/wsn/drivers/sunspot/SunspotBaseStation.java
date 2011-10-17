package de.uniluebeck.itm.wsn.drivers.sunspot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uniluebeck.itm.wsn.drivers.core.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: evangelos
 * Date: 10/10/11
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SunspotBaseStation {

    @Inject
    @Named("senderPort")
    private String senderPort;

    @Inject
    @Named("receiverPort")
    private String receiverPort;

}
