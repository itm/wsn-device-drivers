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
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Identification;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations.BlockingInterface;

public class RemoteConnection extends AbstractConnection{

	private static Logger log = LoggerFactory.getLogger(RemoteConnection.class);
	
	private PeerInfo server = null;
	private PeerInfo client = null;
	private ThreadPoolCallExecutor executor = null;
	private DuplexTcpClientBootstrap bootstrap = null;
	private RpcClientChannel channel = null;
	
	/**
	 * establishes a connection to the server running on the given host.
	 * @param uri: ConnectionString der Form DeviceID:Username:password@host:port
	 */
	@Override
	public void connect(String uri) {
		
		URL url = null;
		try {
			url = new URL("http://"+uri);
		} catch (MalformedURLException e) {
			
			log.debug(e.getMessage());
		}
		
		server = new PeerInfo(url.getHost(),url.getPort());
		String deviceID = url.getUserInfo().split(":")[0];
		String username = url.getUserInfo().split(":")[1];
		String password = url.getUserInfo().split(":")[2];

		client = new PeerInfo(username+"client",1234);
		
		// setzen des Thread-Pools
		executor = new ThreadPoolCallExecutor(3, 10);
		//setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(
                client, 
                new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()),
        executor);
		
		// setzen der Verbindungs-Optionen, siehe Netty
		bootstrap.setOption("connectTimeoutMillis",10000);
		bootstrap.setOption("connectResponseTimeoutMillis",10000);
		bootstrap.setOption("receiveBufferSize", 1048576);
		bootstrap.setOption("tcpNoDelay", false);
		
		//try to connect with different client ports 
		//	until there is a port not already in use:
		boolean peered = false;
		while(!peered){
			try {
				// herstellen der Verbindung zum Server
				channel = bootstrap.peerWith(server);
				peered = true;
			} catch (IOException e) {
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
		BlockingInterface syncOperationService = Operations.newBlockingStub(channel);
		
		// aufbauen eines Identification-Packets
		Identification id = Identification.newBuilder().setDeviceID(deviceID).setUsername(username).setPassword(password).build();
		//durchfuehren eines RPC-calls (das connect sollte vlt blockierend sein)
		try {
			syncOperationService.connect(controller, id);
		} catch (ServiceException e) {
			log.debug(e.getMessage());
		}
		
	}

	@Override
	public void shutdown(boolean force) {
		// TODO Auto-generated method stub
		
	}
	public DuplexTcpClientBootstrap getBootstrap() {
		return bootstrap;
	}

	public RpcClientChannel getChannel() {
		return channel;
	}

}
