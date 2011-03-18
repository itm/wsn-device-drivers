package de.uniluebeck.itm.metadatenservice;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations.BlockingInterface;
import de.uniluebeck.itm.metadaten.files.MetaDataService.ServerIP;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper.NodeHelper;
import de.uniluebeck.itm.metadatenservice.config.ConfigData;
import de.uniluebeck.itm.metadatenservice.config.Node;

public class ClientStub {
	// TODO connect gleich im Konstruktor oder soll Verbindung fuer jeden
	// Refresh/add-Zyklus extra
	// durchgefuehrt werden?
	private static Logger log = LoggerFactory.getLogger(ClientStub.class);

	PeerInfo server = null;
	PeerInfo client = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
	String userName = "";
	String passWord = "";

	ClientStub(ConfigData config) throws Exception {
		this(config.getUsername(), config.getPassword(), config.getServerIP(),
				config.getServerPort().intValue(), config.getClientPort().intValue());
	}

	ClientStub(String userName, String passWord, String uri, int port,
			int clientPort) throws Exception {
		// setzen der Server-Infos
		server = new PeerInfo(uri, port);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName + "client", clientPort);
		this.userName = userName;
		this.passWord = passWord;
	}

	// initialer Connect
	public void connect(String userName, String passWord) {
		// setzen des Thread-Pools
		channel = null;
		log.info("setting Threadpools CLient");
		executor = new ThreadPoolCallExecutor(3, 10);
		// setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(client,
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()), executor);

		// registrieren der Reverse-RPC Services
		// bootstrap.getRpcServiceRegistry().registerService(PacketService.newReflectiveService(new
		// PacketServiceImpl()));

		// setzen der Verbindungs-Optionen, siehe Netty
		log.info("Verbindungsoptionen CLient werden gesetzt");
		bootstrap.setOption("connectTimeoutMillis", 10000);
		bootstrap.setOption("connectResponseTimeoutMillis", 10000);
		bootstrap.setOption("receiveBufferSize", 1048576);
		bootstrap.setOption("tcpNoDelay", false);

		try {
			// herstellen der Verbindung zum Server
			channel = bootstrap.peerWith(server);
		} catch (IOException e) {
			log.error("Fehler beim bootstrap" + e.getMessage());
			log.error(e.getMessage(),e);
			// channel = bootstrap.peerWith(server);
			// --- callback.onFailure(e);
		}
		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		log.info("RPC-Controller erzeugt");
		BlockingInterface syncOperationService = Operations
				.newBlockingStub(channel);
		// // aufbauen eines Identification-Packets
		Identification id = Identification.newBuilder().setUsername(userName)
				.setPassword(passWord).build();

		try {
			// herstellen der Verbindung zum Server
			log.info("connect to Server: " + server);
			// channel = bootstrap.peerWith(server);
			syncOperationService.connect(controller, id);
		} catch (ServiceException e) {
			log.debug("Fehler beim connect" + e.getMessage());
		}
		// // erzeugen eines async RPC-Objekts fuer die Operationen
		operationService = Operations.newStub(channel);
		//
		// //durchfuehren eines RPC-calls (das connect sollte vlt blockierend
		// sein)
		// operationService.connect(controller, id, new RpcCallback<VOID>(){
		//
		// // callback aufruf des Servers
		// @Override
		// public void run(VOID arg0) {
		// if(!controller.failed()){
		// callback.onSuccess("Meldung vom Server: Die Authentifikation war erfolgreich");
		// }
		// else{
		// callback.onFailure(new Throwable(controller.errorText()));
		// }
		// }});
	}

	public void disconnect() {
		final RpcController controller = channel.newRpcController();

		BlockingInterface syncOperationService = Operations
				.newBlockingStub(channel);
		// // aufbauen eines Identification-Packets
		Identification id = Identification.newBuilder().setUsername(userName)
				.setPassword(passWord).build();

		try {
			// herstellen der Verbindung zum Server
			log.info("DisConnect from Server: " + server);
			// channel = bootstrap.peerWith(server);
			syncOperationService.disconnect(controller, id);
		} catch (ServiceException e) {
			log.error("Fehler beim disconnect" + e.getMessage());
		}
		channel.close();
		executor.shutdown();
		bootstrap.releaseExternalResources();
		log.info("Disconnect () nach channel.close");

	}

	/**
	 * Adds a node to the Metadatedirectory
	 * 
	 * @param node
	 * @param callback
	 */
	public void add(final Node node, final AsyncCallback<String> callback) {
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// Node fuer die Uebertragung erzeugen
		NodeHelper nhelper = new NodeHelper();

		// ausfuehren des async RPCs
		operationService.add(controller, nhelper.changetoNODE(node),
				new RpcCallback<VOID>() {

					// callback aufruf des Servers
					@Override
					public void run(VOID arg0) {
						if (!controller.failed()) {
							callback.onSuccess("Knoten " + node.getNodeid()
									+ "erfolgreich dem Verzeichnis hinzugefuegt");
						} else {
							callback.onFailure(new Throwable(controller
									.errorText()));
						}
					}
				});
	}

	/**
	 * Removes all Data from the MetaDataDirectory
	 */
	public void removeAllData() {
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();

		BlockingInterface syncOperationService = Operations
				.newBlockingStub(channel);
		ServerIP.Builder tcpserveripbuilder = ServerIP.newBuilder();

		try {
			InetAddress address = InetAddress.getLocalHost();
			tcpserveripbuilder.setIP(address.getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			log.error("Ip-Adresse des TCP-Servers konnte nicht ermittelt werden"
					+ e.getMessage() + e.getCause());

		}

		try {
			// herstellen der Verbindung zum Server
			log.info("connect to Server: " + server);
			// channel = bootstrap.peerWith(server);
			syncOperationService.removeallServerNodes(controller,
					tcpserveripbuilder.build());
		} catch (ServiceException e) {
			log.debug("Fehler beim connect" + e.getMessage());
		}

	}

	/**
	 * Refreshes nodeentry in the Metadatarepository
	 * 
	 * @param node
	 * @param callback
	 */
	// Hinzufuegen eines Knotens in das MetaDatenverzeichnis
	public void refresh(final Node node, final AsyncCallback<String> callback) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// Node fuer die Uebertragung erzeugen
		NodeHelper nhelper = new NodeHelper();
		// ausfuehren des async RPCs
		operationService.refresh(controller, nhelper.changetoNODE(node),
				new RpcCallback<VOID>() {

					// callback aufruf des Servers
					@Override
					public void run(VOID arg0) {
						if (!controller.failed()) {
							callback.onSuccess("Knoten " + node.getNodeid()
									+ "erfolgreich dem Verzeichnis hinzugefuegt");
						} else {
							callback.onFailure(new Throwable(controller
									.errorText()));
							log.info("Error refreshing node");
						}
					}
				});

	}

	/**
	 * Refreshes nodeentry in the Metadatarepository (sync - Operation
	 * 
	 * @param node
	 * @param callback
	 */
	// Hinzufuegen eines Knotens in das MetaDatenverzeichnis
	public void refreshSync(final Node node) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// Node fuer die Uebertragung erzeugen
		NodeHelper nhelper = new NodeHelper();
		BlockingInterface syncOperationService = Operations
				.newBlockingStub(channel);
		try {
			// herstellen der Verbindung zum Server
			log.info("connect to Server: " + server);
			// channel = bootstrap.peerWith(server);
			syncOperationService
					.refresh(controller, nhelper.changetoNODE(node));
		} catch (ServiceException e) {
			log.debug("Fehler beim connect" + e.getMessage());
		}

	}

}
