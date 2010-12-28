package de.uniluebeck.itm.metadaten.remote.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations.BlockingInterface;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchRequest;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchResponse;
import de.uniluebeck.itm.metadaten.remote.entity.Node;
import de.uniluebeck.itm.metadaten.remote.metadataclienthelper.NodeHelper;


/**
 * 
 * @author tora
 *
 */


public class MetaDatenClient implements MetaDataClient {
	
	private static Log log = LogFactory.getLog(MetaDatenClient.class);
	
	PeerInfo server = null;
	PeerInfo client = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;	
	private static List<Node> nodelist = new ArrayList<Node> ();
	private String password ="testPassword";
	private String user ="frager";
	private String serverip ="localhost";
	private int serverport =8080;
	private int clientPort=1235;
	
	public MetaDatenClient () {
		// setzen der Server-Infos
		server = new PeerInfo(serverip,serverport);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(user+"client",clientPort);
	};
	
	public MetaDatenClient (String userName, String passWord, String uri, int port, int clientPort) throws Exception{
		
		// setzen der Server-Infos
		server = new PeerInfo(uri,port);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName+"client",clientPort);
		user=userName;
		password=passWord;
		
	}
		/**
		 * Sets up connection to server
		 * @param userName
		 * @param passWord
		 * @param callback
		 */
		private void connect(String userName, String passWord){
			
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

			// setzen der Verbindungs-Optionen, siehe Netty
			System.out.println("Verbindungsoptionen CLient werden gesetzt");
			bootstrap.setOption("connectTimeoutMillis",10000);
			bootstrap.setOption("connectResponseTimeoutMillis",10000);
			bootstrap.setOption("receiveBufferSize", 1048576);
			bootstrap.setOption("tcpNoDelay", false);
			// herstellen der Verbindung zum Server
				try {
					channel = bootstrap.peerWith(server);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			// erzeugen eines Controlles fuer diese Operation
			final RpcController controller = channel.newRpcController();
			System.out.println("RPC-Controller erzeugt");
			
			// erzeugen eines async RPC-Objekts fuer die Operationen
			operationService = Operations.newStub(channel);
			
			// aufbauen eines Identification-Packets
			Identification id = Identification.newBuilder().setUsername(userName).setPassword(passWord).build();
			// erzeugen eines synchronen RPC-Objekts fuer den connect
			BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
			try {
				// sync RPC-Aufruf
				 blockOperationService.connect(controller, id);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		
		
		/**
		 * Disconnect the Channel to the server
		 */
		private void disconnect ()
		{
			channel.close();
		}
	

	
	public List<Node> search (Node queryexmpl, String query) throws Exception
	{

		this.connect(user, password);
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		NodeHelper nhelper = new NodeHelper();
		// Node f�r die �bertragung erzeugen
		
		//Result erzeugen
		SearchRequest request = SearchRequest.newBuilder().setQueryMs(nhelper.changetoNODE(queryexmpl)).setQueryString("123").build();
	
		// erzeugen eines synchronen RPC-Objekts fuer die Operationen
		BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		try {
			// sync RPC-Aufruf
			SearchResponse resultresp = blockOperationService.search(controller, request);
			System.out.println("Gr��e der Sresponse" + resultresp.getResponseList().size());
			List <NODE> result = new ArrayList<NODE>();
			result=resultresp.getResponseList();
			System.out.println("Gr��e des Results: " + result.size());
			for (int i=0 ;i < result.size(); i++)
				{
					nodelist.add(nhelper.changeToNode(result.get(i)));
				}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		this.disconnect();
		return nodelist;
	}
	/**
	 * Asynchrone Suche
	 */
	public void searchasync (Node queryexmpl, String query, final AsyncCallback<List<Node>> callback) throws Exception
	{

		this.connect(user, password);
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		NodeHelper nhelper = new NodeHelper();	
		
		SearchRequest request = SearchRequest.newBuilder().setQueryMs(nhelper.changetoNODE(queryexmpl)).setQueryString(query).build();
	
		// erzeugen eines synchronen RPC-Objekts fuer die Operationen
		BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		try {
			// synchroner RPC-Aufruf
			SearchResponse resultresp = blockOperationService.search(controller, request);
			System.out.println("Gr��e der Sresponse" + resultresp.getResponseList().size());
			List <NODE> result = new ArrayList<NODE>();
			result=resultresp.getResponseList();
			System.out.println("Gr��e des Results: " + result.size());
			for (int i=0 ;i < result.size(); i++)
				{
					System.out.println("Knoten hinzuf�gen");
					nodelist.add(nhelper.changeToNode(result.get(i)));
				}
		} catch (ServiceException e) {
			callback.onFailure(e);
		}
		
		this.disconnect();
		callback.onSuccess(nodelist);
	}

}