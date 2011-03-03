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
 *  @author babel
 * MetaDatenClient gives the possibility to search fpr node in
 * the Metadata-Directory
 */
public class MetaDatenClient implements MetaDataClient {
	/** Logger**/
	private static Log log = LogFactory.getLog(MetaDatenClient.class);
	/** list of nodes holds the searchresult**/
	private List<Node> nodelist = new ArrayList<Node>();
	/** Peerinfo of the server**/
	private PeerInfo server = null;
	/** Peerinfo of the client**/
	private PeerInfo client = null;
	/**Threadpoolexecutor for communication Requests**/
	private ThreadPoolCallExecutor executor = null;
	/** **/
	private DuplexTcpClientBootstrap bootstrap = null;
	/** Channel for the communication with the server**/
	private RpcClientChannel channel = null;
	/** interface for the operations metadaten.proto**/
	private Operations.Interface operationService = null;
	/** user for authentication to the server**/
	private String user = "frager";
	/** password for the user**/
	private String password = "testPassword";
	
	/**
	 * Constructor
	 * @param userName Name of the user for authentication
	 * @param passWord password of the user
	 * @param serverIP IP of the server to which you connect
	 * @param serverPort port of the server to which you connect
	 * @throws Exception Exception while establishing the instance
	 */
	public MetaDatenClient(final String userName, final String passWord, final String serverIP,
			final int serverPort) throws Exception {
		final int  port = 1235;
		// setzen der Server-Infos
		server = new PeerInfo(serverIP, serverPort);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName + "client", port);
		user = userName;
		password = passWord;

	}
	/**
	 * Constructor
	 * @param userName Name of the user for authentication
	 * @param passWord password of the user
	 * @param serverIP IP of the server to which you connect
	 * @param serverPort port of the server to which you connect
	 * @param clientPort clientport which will be used for communication with the server
	 * @throws Exception Exception while establishing the instance
	 */
	public MetaDatenClient(final String userName, final String passWord, final String serverIP,
			final int serverPort, final int clientPort) throws Exception {

		// setzen der Server-Infos
		server = new PeerInfo(serverIP, serverPort);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName + "client", clientPort);
		user = userName;
		password = passWord;

	}

	/**
	 * Sets up connection to server
	 * 
	 * @param userName user for authentication
	 * @param passWord password for the user
	 */
	private void connect(final String userName, final String passWord) {
		final int corePoolsize = 3;
		final int maxPoolsize = 10;
		final int timeout = 10000;
		final int responsetimeout = 10000;
		final int bufferSize = 1048576;
		// setzen des Thread-Pools
		System.out.println("Setzen des Threadpools CLient");
		executor = new ThreadPoolCallExecutor(corePoolsize, maxPoolsize);
		// setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(client,
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()), executor);

		// setzen der Verbindungs-Optionen, siehe Netty
		System.out.println("Verbindungsoptionen CLient werden gesetzt");
		bootstrap.setOption("connectTimeoutMillis", timeout);
		bootstrap.setOption("connectResponseTimeoutMillis", responsetimeout);
		bootstrap.setOption("receiveBufferSize", bufferSize);
		bootstrap.setOption("tcpNoDelay", false);
		// herstellen der Verbindung zum Server
		try {
			channel = bootstrap.peerWith(server);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			log.error(e1.getStackTrace());
		}

		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		log.info("RPC-Controller erzeugt");

		// erzeugen eines async RPC-Objekts fuer die Operationen
		operationService = Operations.newStub(channel);
		log.info("create Identification");
		// aufbauen eines Identification-Packets
		final Identification id = Identification.newBuilder().setUsername(userName)
				.setPassword(passWord).build();
		// erzeugen eines synchronen RPC-Objekts fuer den connect
		log.info("Connect to server");
		final BlockingInterface blockOperationService = Operations
				.newBlockingStub(channel);
		try {
			log.info("sync RPC-call");
			// sync RPC-Aufruf
			blockOperationService.connect(controller, id);
		} catch (final ServiceException e) {
			e.printStackTrace();
		}
		log.info("Connection to server established");
	}

	/**
	 * Disconnect the Channel to the server
	 * @param userName username to athenticate to the server
	 * @param passWord password for the user
	 */
	public void disconnect(final String userName, final String passWord) {
		final RpcController controller = channel.newRpcController();

		final BlockingInterface syncOperationService = Operations
				.newBlockingStub(channel);
		// // aufbauen eines Identification-Packets
		final Identification id = Identification.newBuilder().setUsername(userName)
				.setPassword(passWord).build();

		try {
			// herstellen der Verbindung zum Server
			log.info("DisConnect from Server: " + server);
			// channel = bootstrap.peerWith(server);
			syncOperationService.disconnect(controller, id);
		} catch (final ServiceException e) {
			log.error("Fehler beim disconnect" + e.getMessage());
		}
		channel.close();
		executor.shutdown();
		bootstrap.releaseExternalResources();
		log.info("Disconnect () nach channel.close");
	}

	/**
	 * Synchrone Suche
	 * @param queryexmpl Examplenode for the search in  the MetaDataDictionary
	 * @param query Querystring for the search
	 * @throws Exception error while connection to server
	 * @return List<Node> result of the search
	 */
	public List<Node> search(final Node queryexmpl, String query) throws NullPointerException {

		this.connect(user, password);
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		final NodeHelper nhelper = new NodeHelper();
		if(query==null){
			query="";
		}
		// Result erzeugen
		final SearchRequest request = SearchRequest.newBuilder()
				.setQueryMs(nhelper.changetoNODE(queryexmpl))
				.setQueryString(query).build();
		// erzeugen eines synchronen RPC-Objekts fuer die Operationen
		final BlockingInterface blockOperationService = Operations
				.newBlockingStub(channel);
		try {
			// sync RPC-Aufruf
			final SearchResponse resultresp = blockOperationService.search(
					controller, request);
			List<NODE> result = new ArrayList<NODE>();
			result = resultresp.getResponseList();
			for (int i = 0; i < result.size(); i++) {
				nodelist.add(nhelper.changeToNode(result.get(i)));
			}
		} catch (final ServiceException e) {
			e.printStackTrace();
		}

		this.disconnect(user, password);
		return nodelist;
	}

	/**
	 * Asynchrone Suche
	 * @param queryexmpl Examplenode for the search in  the MetaDataDictionary
	 * @param query Querystring for the search
	 * @param callback for calling back after ending operation
	 * @throws Exception error while connection to server
	 * 
	 */
	public void searchasync(final Node queryexmpl, final String query,
			final AsyncCallback<List<Node>> callback) throws Exception {

		this.connect(user, password);
		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		final NodeHelper nhelper = new NodeHelper();

		final SearchRequest request = SearchRequest.newBuilder()
				.setQueryMs(nhelper.changetoNODE(queryexmpl))
				.setQueryString(query).build();

		// erzeugen eines synchronen RPC-Objekts fuer die Operationen
		final BlockingInterface blockOperationService = Operations
				.newBlockingStub(channel);
		try {
			// synchroner RPC-Aufruf
			final SearchResponse resultresp = blockOperationService.search(
					controller, request);
			System.out.println("Groesse der Sresponse"
					+ resultresp.getResponseList().size());
			List<NODE> result = new ArrayList<NODE>();
			result = resultresp.getResponseList();
			System.out.println("Groesse des Results: " + result.size());
			for (int i = 0; i < result.size(); i++) {
				System.out.println("Knoten hinzufuegen");
				nodelist.add(nhelper.changeToNode(result.get(i)));
			}
		} catch (final ServiceException e) {
			callback.onFailure(e);
		}

		this.disconnect("frager", "testPassword");
		callback.onSuccess(nodelist);
	}

}
