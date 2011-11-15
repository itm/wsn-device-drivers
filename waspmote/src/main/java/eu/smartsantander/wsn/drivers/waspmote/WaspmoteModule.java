package eu.smartsantander.wsn.drivers.waspmote;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import de.uniluebeck.itm.wsn.drivers.core.Connection;
import de.uniluebeck.itm.wsn.drivers.core.MacAddress;
import de.uniluebeck.itm.wsn.drivers.core.operation.ProgramOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.ReadMacAddressOperation;
import de.uniluebeck.itm.wsn.drivers.core.operation.SendOperation;
import eu.smartsantander.util.guice.CustomScopes;
import eu.smartsantander.util.guice.JvmSingleton;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.NodeAddressingHelper;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.NodeAddressingHelperMemoryImpl;
import eu.smartsantander.wsn.drivers.waspmote.multiplexer.NodeConnectionInfo;
import eu.smartsantander.wsn.drivers.waspmote.operation.Waspmote802154SendOperation;
import eu.smartsantander.wsn.drivers.waspmote.operation.WaspmoteFlashNodeOperation;
import eu.smartsantander.wsn.drivers.waspmote.operation.WaspmoteReadDigiMacAddressOperation;

import java.util.Map;

/**
 * @author TLMAT UC
 */
public class WaspmoteModule extends AbstractModule {

    private final Map<String, String> configuration;

    public WaspmoteModule(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {

        bindScope(JvmSingleton.class, CustomScopes.JVM_SINGLETON);

        bind(Connection.class).to(WaspmoteVirtualSerialPortConnection.class);
        bind(NodeAddressingHelper.class).to(NodeAddressingHelperMemoryImpl.class).in(CustomScopes.JVM_SINGLETON);

        bind(SendOperation.class).to(Waspmote802154SendOperation.class);

        bind(new TypeLiteral<Map<String, String>>() {})
                .annotatedWith(Names.named("configuration")).toInstance(this.configuration);

        String value = this.configuration.get("nodeID");
        bind(Integer.class).annotatedWith(Names.named("nodeID")).toInstance(
                value.startsWith("0x")
                        ? Integer.parseInt(value.substring(2), 16)
                        : Integer.parseInt(value, 10));

        NodeConnectionInfo nodeMACs = new NodeConnectionInfo(
                new MacAddress(this.configuration.get("mac_Digimesh")),
                new MacAddress(this.configuration.get("mac_802.15.4")));
        bind(NodeConnectionInfo.class).annotatedWith(Names.named("nodeMACs")).toInstance(nodeMACs);
        bind(String.class).annotatedWith(Names.named("uri")).toInstance(this.configuration.get("uri"));

    }
}
