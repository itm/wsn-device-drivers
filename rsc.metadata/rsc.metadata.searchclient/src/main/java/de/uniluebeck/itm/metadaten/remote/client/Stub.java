package de.uniluebeck.itm.metadaten.remote.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchRequest;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchResponse;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.remote.entity.Node;
import de.uniluebeck.itm.metadaten.remote.metadataclienthelper.NodeHelper;



public class Stub {

	//private static Log log = LogFactory.getLog(Client.class);
	
	PeerInfo server = null;
	PeerInfo client = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
//	TestOperations.Interface testService = null;
//	State state = null;
	
	Stub (String userName, String passWord, String uri, int port) throws Exception{
		this(userName,passWord,uri,port,1235);
	}

	Stub (String userName, String passWord, String uri, int port, int clientPort) throws Exception{
		
		// setzen der Server-Infos
		server = new PeerInfo(uri,port);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName+"client",clientPort);
		
		// aufruf des initialen Connects
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
		System.out.println("Setzen des Threadpools CLient");
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
			channel = bootstrap.peerWith(server);
		} catch (IOException e) {
			callback.onFailure(e);
		}
		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		System.out.println("RPC-Controller erzeugt");
		
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
				}
				else{
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}});
	}
	
	// Hinzufuegen eines Knotens in das MetaDatenverzeichnis
	public void search(final Node node, String queryString, final AsyncCallback<List<NODE>> callback) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		NodeHelper nhelper = new NodeHelper();
		// erzeugen einer Nachricht, der OperationKey wird aus der controllerID erzeugt
//		STRING request = STRING.newBuilder().setQuery(setMessage).setOperationKey(controller.toString()).build();
		// Node fuer die Uebertragung erzeugen
		
		//Result erzugen
		SearchRequest request = SearchRequest.newBuilder().setQueryMs(nhelper.changetoNODE(node)).setQueryString("123").build();
		//ausfuehren des async RPCs
//		operationService.add(controller, request, new RpcCallback<VOID>(){
		operationService.search(controller, request,  new RpcCallback<SearchResponse>(){
			
			// callback aufruf des Servers
			@Override
			public void run(SearchResponse arg0) {
				if(!controller.failed()){
					callback.onSuccess(arg0.getResponseList());
				}
				else{
					callback.onFailure(new Throwable("Fehler im Stub" + controller.errorText()));
				}
			}});
		
	}

}
	
