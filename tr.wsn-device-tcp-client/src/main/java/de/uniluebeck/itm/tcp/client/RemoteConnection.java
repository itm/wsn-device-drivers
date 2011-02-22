package de.uniluebeck.itm.tcp.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.devicedriver.AbstractConnection;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Identification;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.client.files.MessageServiceFiles.Operations.BlockingInterface;

/**
 * establish a TCP-Connection to a device via a TCP-Server
 * @author Andreas Maier
 * @author Bjoern Schuett
 *
 */
public class RemoteConnection extends AbstractConnection{
	
	/**
	 * 
	 */
	private static final int PORT = 1234;
	/**
	 * 
	 */
	private static final int CORE_POOL_SIZE = 3;
	/**
	 * 
	 */
	private static final int MAX_POOL_SIZE = 10;
	/**
	 * 
	 */
	private static final int CONNECT_TIMEOUT = 10000;
	/**
	 * 
	 */
	private static final int CONNECT_RESPONSE_TIMEOUT = 10000;
	/**
	 * 
	 */
	private static final int RECEIVE_BUFFER_SIZE = 1048576;
	
	/**
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(RemoteConnection.class);
	
	/**
	 * 
	 */
	private PeerInfo server = null;
	/**
	 * 
	 */
	private PeerInfo client = null;
	/**
	 * 
	 */
	private ThreadPoolCallExecutor executor = null;
	/**
	 * 
	 */
	private DuplexTcpClientBootstrap bootstrap = null;
	/**
	 * 
	 */
	private RpcClientChannel channel = null;
	/**
	 * 
	 */
	private BlockingInterface syncOperationService = null;


	@Override
	public void connect(final String uri) {
		
		URL url = null;
		try {
			url = new URL("http://"+uri);
		} catch (final MalformedURLException e) {
			
			log.debug(e.getMessage());
		}
		
		server = new PeerInfo(url.getHost(),url.getPort());
		final String deviceID = url.getUserInfo().split(":")[0];
		final String username = url.getUserInfo().split(":")[1];
		final String password = url.getUserInfo().split(":")[2];

		client = new PeerInfo(username+"client",RemoteConnection.PORT);
		
		// setzen des Thread-Pools
		executor = new ThreadPoolCallExecutor(RemoteConnection.CORE_POOL_SIZE, RemoteConnection.MAX_POOL_SIZE);
		//setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(
                client, 
                new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()),
        executor);
		
		// setzen der Verbindungs-Optionen, siehe Netty
		bootstrap.setOption("connectTimeoutMillis",RemoteConnection.CONNECT_TIMEOUT);
		bootstrap.setOption("connectResponseTimeoutMillis",RemoteConnection.CONNECT_RESPONSE_TIMEOUT);
		bootstrap.setOption("receiveBufferSize", RemoteConnection.RECEIVE_BUFFER_SIZE);
		bootstrap.setOption("tcpNoDelay", false);
		
		//try to connect with different client ports 
		//	until there is a port not already in use:
		boolean peered = false;
		while(!peered){
			try {
				// herstellen der Verbindung zum Server
				channel = bootstrap.peerWith(server);
				peered = true;
			} catch (final IOException e) {
				if(e.getMessage().contains("ALREADY_CONNECTED")){
					log.info(e.getMessage());
					client = new PeerInfo(username+"client",client.getPort()+1);
					bootstrap.setClientInfo(client);
				}
				else{
					peered = true;
					log.debug(e.getMessage());
				}
			}
		}
		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		
		// erzeugen eines sync RPC-Objekts fuer die Operationen
		this.syncOperationService = Operations.newBlockingStub(channel);
		
		// aufbauen eines Identification-Packets
		final Identification id = Identification.newBuilder().setDeviceID(deviceID).setUsername(username).setPassword(password).build();
		//durchfuehren eines RPC-calls (das connect sollte vlt blockierend sein)
		try {
			syncOperationService.connect(controller, id);
		} catch (final ServiceException e) {
			log.debug(e.getMessage());
		}
		
	}

	@Override
	public void shutdown(final boolean force) {
		
		if(force){
			channel.close();
			server = null;
			client = null;
			executor.shutdown();
			bootstrap.releaseExternalResources();
		}
		else {
			// erzeugen eines Controlles fuer diese Operation
			final RpcController controller = channel.newRpcController();
			
			try {
				this.syncOperationService.shutdown(controller, EmptyAnswer.newBuilder().build());
				controller.startCancel();
				channel.close();
				server = null;
				client = null;
				executor.shutdown();
				bootstrap.releaseExternalResources();
			} catch (final ServiceException e) {
				log.debug(e.getMessage());
			}
		}
	}
	
	public DuplexTcpClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public RpcClientChannel getChannel() {
		return channel;
	}
	
}
