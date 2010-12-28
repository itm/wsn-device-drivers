package de.uniluebeck.itm.metadatenservice;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.entity.ConfigData;
import de.uniluebeck.itm.entity.Node;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.serverclient.metadataclienthelper.NodeHelper;



public class ClientStub  {
	//TODO connect gleich im Konstruktor oder soll Verbindung für jeden Refresh/add-Zyklus extra
	//durchgeführt werden?
	private static Log log = LogFactory.getLog(ClientStub.class);
	
	PeerInfo server = null;
	PeerInfo client = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;

	ClientStub (ConfigData config) throws Exception{
		this(config.getUsername(), config.getPassword(), config.getServerIP(), config.getPort(), config.getClientport());
	}
	ClientStub (String userName, String passWord, String uri, int port) throws Exception{
		this(userName,passWord,uri,port,1234);
	}

	ClientStub (String userName, String passWord, String uri, int port, int clientPort) throws Exception{
		
		// setzen der Server-Infos
		server = new PeerInfo(uri,port);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName+"client",clientPort);
		
		// aufruf des initialen Connects
		log.info("Start connecting to MetaDataServer");
		connect(userName, passWord, new AsyncCallback<String>() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(throwable.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
			}

			@Override
			public void onProgressChange(float fraction) {
			}
		});
	}
	
	// initialer Connect
	private void connect(String userName, String passWord, final AsyncCallback<String> callback){
		
		// setzen des Thread-Pools
		log.info("set of Threadpool Metadataservice");
		executor = new ThreadPoolCallExecutor(3, 10);
		//setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(
                client, 
                new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()),
        executor);

		// registrieren der Reverse-RPC Services
//		bootstrap.getRpcServiceRegistry().registerService(PacketService.newReflectiveService(new PacketServiceImpl()));
		
		// setzen der Verbindungs-Optionen, siehe Netty
		System.out.println("Verbindungsoptionen CLient werden gesetzt");
		bootstrap.setOption("connectTimeoutMillis",10000);
		bootstrap.setOption("connectResponseTimeoutMillis",10000);
		bootstrap.setOption("receiveBufferSize", 1048576);
		bootstrap.setOption("tcpNoDelay", false);
		
		try {
			// herstellen der Verbindung zum Server
			log.info("connect to Server: " + server );
			channel = bootstrap.peerWith(server);
		} catch (IOException e) {
			callback.onFailure(e);
		}
		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		System.out.println("RPC-Controller erzeugt");
		
		// erzeugen eines async RPC-Objekts fuer die TestOperationen
//		testService = TestOperations.newStub(channel);
		// erzeugen eines async RPC-Objekts fuer die Operationen
		operationService = Operations.newStub(channel);
		
		// aufbauen eines Identification-Packets
		Identification id = Identification.newBuilder().setUsername(userName).setPassword(passWord).build();
		//durchfuehren eines RPC-calls (das connect sollte vlt blockierend sein)
		operationService.connect(controller, id, new RpcCallback<VOID>(){

			// callback aufruf des Servers
			@Override
			public void run(VOID arg0) {
				if(!controller.failed()){
					callback.onSuccess("Meldung vom Server: Die Authentifikation war erfolgreich");
					log.info("connection to " + server + " established");
				}
				else{
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}});
	}
	
	// Hinzufügen eines Knotens in das MetaDatenverzeichnis
	public void add(final Node node, final AsyncCallback<String> callback) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// Node für die Übertragung erzeugen
		NodeHelper nhelper = new NodeHelper();
		
		//ausfuehren des async RPCs
		operationService.add(controller, nhelper.changetoNODE(node), new RpcCallback<VOID>(){

			// callback aufruf des Servers
			@Override
			public void run(VOID arg0) {
				if(!controller.failed()){
					callback.onSuccess("Knoten " + node.getId() + "erfolgreich dem Verzeichnis hinzugefügt");
				}
				else{
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}});
		
	}
	
	/**
	 * Refreshes nodeentry in the Metadatarepository
	 * @param node
	 * @param callback
	 */
	 // Hinzufügen eines Knotens in das MetaDatenverzeichnis
	public void refresh(final Node node, final AsyncCallback<String> callback) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// Node für die Übertragung erzeugen
		NodeHelper nhelper = new NodeHelper();
		
		//ausfuehren des async RPCs
		operationService.refresh(controller, nhelper.changetoNODE(node), new RpcCallback<VOID>(){

			// callback aufruf des Servers
			@Override
			public void run(VOID arg0) {
				if(!controller.failed()){
					callback.onSuccess("Knoten " + node.getId() + "erfolgreich dem Verzeichnis hinzugefügt");
				}
				else{
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}});
		
	}

	
}
	
