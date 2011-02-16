package de.uniluebeck.itm.metadaten.metadatenserver;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerBootstrap;

import de.uniluebeck.itm.metadaten.entities.ConfigData;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchRequest;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchResponse;
import de.uniluebeck.itm.metadaten.files.MetaDataService.ServerIP;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.server.helper.NodeHelper;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;
//import de.uniluebeck.itm.tr.util.TimedCache;

public class MetaDatenServer {

	private static Log log = LogFactory.getLog(MetaDatenServer.class);
	// werden nach 30 min alle eintraege des Cache geloescht?
	// wie Timeout fuer einen Eintrag neu starten?
	// private static TimedCache<RpcClientChannel, ClientID> idList = new
	// TimedCache<RpcClientChannel, ClientID>();
	// private static TimedCache<RpcClientChannel, Subject> authList = new
	// TimedCache<RpcClientChannel, Subject>();
//	private static Map<RpcClientChannel, ClientID> idList = new HashMap<RpcClientChannel, ClientID>();
	private static Map<RpcClientChannel, Subject> authList = new HashMap<RpcClientChannel, Subject>();
	public static List<Node> knotenliste = new ArrayList<Node>();

	public static void main(String[] args) throws URISyntaxException {
		ConfigData config = loadConfig("config.xml");
		log.info("Startup Server!");
		CleanRepository cleaner = new CleanRepository(config.getOveragetime());
		cleaner.timer.schedule(cleaner, config.getTimerdelay(),
				config.getTimerinterval());
		// setzen der server-Informationen
		PeerInfo serverInfo = new PeerInfo(config.getServerIP(),
				config.getPort());

		// setzen des ThreadPools
		RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10, 10);

		// setzen des bootstraps
		DuplexTcpServerBootstrap bootstrap = new DuplexTcpServerBootstrap(
				serverInfo, new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()), executor);

		// setzen eines ConnectionLoggers
		RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
		RpcConnectionEventListener listener = new RpcConnectionEventListener() {

			@Override
			public void connectionReestablished(RpcClientChannel clientChannel) {
				log.info("connectionReestablished " + clientChannel);
			}

			@Override
			public void connectionOpened(RpcClientChannel clientChannel) {
				log.info("connectionOpened " + clientChannel);
			}

			@Override
			public void connectionLost(RpcClientChannel clientChannel) {
				System.out.println("connectionLost " + clientChannel);
			}

			@Override
			public void connectionChanged(RpcClientChannel clientChannel) {
				System.out.println("connectionChanged " + clientChannel);
			}
		};
		rpcEventNotifier.setEventListener(listener);
		bootstrap.registerConnectionEventListener(rpcEventNotifier);

		// registrieren der benutzten Services
		// bootstrap.getRpcServiceRegistry().registerService(TestOperations.newReflectiveService(new
		// TestOperationsImpl()));
		bootstrap.getRpcServiceRegistry().registerService(
				Operations.newReflectiveService(new OperationsImpl()));

		// starten des Servers
		bootstrap.bind();

		// ein wenig Kommunikation
		System.out.println("Serving started: " + bootstrap);

		/* Initialiesieren von Shiro */
		// URI fileuri = null;
		// try {
		// fileuri =
		// ClassLoader.getSystemResource("shiro.ini").getPath().toURI();
		// } catch (URISyntaxException e) {
		// log.error(e.getMessage());
		// }
		// File source = new File(fileuri);

		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				ClassLoader.getSystemResource("shiro.ini").toURI().getPath());
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

	}

	/**
	 * Load of ConfigData needed for communication
	 * 
	 * @param fileurl
	 * @return
	 */
	public static ConfigData loadConfig(String fileurl) {
		ConfigData config = new ConfigData();
		Serializer serializer = new Persister();
		URI fileuri = null;
		try {
			fileuri = ClassLoader.getSystemResource(fileurl).toURI();
		} catch (URISyntaxException e) {
			log.error(e.getMessage());
		}
		File source = new File(fileuri);

		try {
			config = serializer.read(ConfigData.class, source);
			// serializer.read(ConfigData, source);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return config;
	}

	/**
	 * Class implements the operations used of client and server
	 * 
	 * @author tora
	 * 
	 */
	static class OperationsImpl implements Operations.Interface {

		// Methode zum verbinden auf den MetaDatenServer
		// hier sollte die Authentifikation stattfinden

		@Override
		public void connect(RpcController controller, Identification request,
				RpcCallback<VOID> done) {

			// eine Moeglichkeit den benutzten channel zu identifizieren
			RpcClientChannel channel = ServerRpcController
					.getRpcChannel(controller);
			// erzeugen einer channel bezogenen User Instanz
//			ClientID id = new ClientID();

			// Abgleich der Userdaten
			log.info("Checking password");
			/* Shiro: */
			Subject currentUser;
			// System.err.println("AuthList" + authList.get(channel));
			if (authList.get(channel) == null) {
				log.info("!!!!!!Nicht in der authListe");
				currentUser = SecurityUtils.getSubject();
			} else {
				log.info("####### in der authListe");
				currentUser = authList.get(channel);
			}
			// System.out.println( authList.get(channel)..toString());
			// System.err.println("!!!User logged in?"
			// +currentUser.isAuthenticated());
			if (!currentUser.isAuthenticated()) {
				log.info("Saving clientdata");
				UsernamePasswordToken token = new UsernamePasswordToken(
						request.getUsername(), request.getPassword());
				token.setRememberMe(true);
				try {

					currentUser.login(token);
					// eintragen der ClientID-Instanz zusammen mit den benutzten
					// Channel in eine Liste
//					idList.put(channel, id);
					authList.put(channel, currentUser);
					// ausfuehren des Callback
					done.run(VOID.newBuilder().build());

				} catch (UnknownAccountException uae) {
					controller.setFailed("There is no user with username of "
							+ token.getPrincipal());
					done.run(null);
					return;
				} catch (IncorrectCredentialsException ice) {
					controller.setFailed("Password for account "
							+ token.getPrincipal() + " was incorrect!");
					done.run(null);
					return;
				} catch (LockedAccountException lae) {
					controller
							.setFailed("The account for username "
									+ token.getPrincipal()
									+ " is locked.  "
									+ "Please contact your administrator to unlock it.");
					done.run(null);
					return;
				} catch (AuthenticationException ae) {
					controller.setFailed(ae.getMessage());
					done.run(null);
					return;
				}
			} else {
				done.run(VOID.newBuilder().build());
			}
			/* Shiro END */
			log.info("All things checked");
		}

		@Override
		public void add(RpcController controller, NODE request,
				RpcCallback<VOID> done) {
			NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Authentification failed!");
				done.run(null);

				return;
			}

			// ClientID id =
			// idList.get(ServerRpcController.getRpcChannel(controller));
			node = nhelper.changeToNode(request);
			node.setTimestamp(new Date());
			StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.storeNode(node);
			} catch (Exception e) {
				e.printStackTrace();
				controller.setFailed("Error saving Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Node with Id: " + node.getId() + "added to repository");

			done.run(VOID.newBuilder().build());

		}

		@Override
		public void remove(RpcController controller, NODE request,
				RpcCallback<VOID> done) {
			NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			node = nhelper.changeToNode(request);
			StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.deleteNode(node);
			} catch (Exception e) {
				e.printStackTrace();
				controller.setFailed("Error deleting Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Node with Id: " + node.getId()
					+ "removed from repository");

			done.run(VOID.newBuilder().build());

		}

		@Override
		public void refresh(RpcController controller, NODE request,
				RpcCallback<VOID> done) {
			NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			// ClientID id =
			// idList.get(ServerRpcController.getRpcChannel(controller));
			node = nhelper.changeToNode(request);
			node.setTimestamp(new Date());
			StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.updateNode(node);
			} catch (Exception e) {
				e.printStackTrace();
				controller.setFailed("Error updating Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Updated Node with Id: " + node.getId());

			done.run(VOID.newBuilder().build());
		}

		@Override
		public void search(RpcController controller, SearchRequest request,
				RpcCallback<SearchResponse> done) {
			log.info("Searchquery by Client: "
					+ ServerRpcController.getRpcChannel(controller)
							.getPeerInfo().getHostName() + " started");
			log.info("NodeID vom CLient" + request.getQueryMs().getKnotenid()+ "Rest des Requests" +  request.getQueryMs().getMicrocontroller());
			log.info("true? " + (request.getQueryMs().getMicrocontroller().matches("")));
			DatabaseToStore getfromDB = new DatabaseToStore();
			NodeHelper nhelper = new NodeHelper();
			List<Node> resultlist = new ArrayList<Node>();
			SearchResponse.Builder responsebuilder = SearchResponse
					.newBuilder();
			if (!(request.getQueryMs() == null)) {
				Node node = nhelper.changeToNode(request.getQueryMs());
				  Node getnode = new Node();
			        getnode.setId("280120101");
//			        getnode.setMicrocontroller("mic1");
			        getnode.setIpAddress("192.168.0.101");
			        nhelper.changeToNode(nhelper.changetoNODE(getnode));
				log.info("Im request querynode if" + node.getId() + node.getIpAddress() + "null?" + node.getMicrocontroller());
				System.out.println("querynode size" + getfromDB.getNodes(node).size());
				resultlist = getfromDB.getNodes(getnode);
				System.err.println("Größe der Anwtort" +resultlist.size());
			}
			if (!(request.getQueryString() == null)) {
				// TODO
				log.info("Operation: search by QueryString - Not supported yet");
			}
			log.info("Result Größe!!!" + resultlist.size());
			for (Node result : resultlist) {
				log.info("Result" + result.getId());
				responsebuilder.addResponse(nhelper.changetoNODE(result));
			}

			log.info("Finales mopped erstellen");
			SearchResponse response = responsebuilder.build();
			log.info("Send searchResponse to client: "
					+ ServerRpcController.getRpcChannel(controller)
							.getPeerInfo().getHostName());
			done.run(response);
		}

		@Override
		public void disconnect(RpcController controller,
				Identification request, RpcCallback<VOID> done) {
			log.info("!'!'!'!'!'!'!'!'!'!'!''!''!'!'!'!'!'Disconnect-Methode");
			// eine Moeglichkeit den benutzten channel zu identifizieren
			RpcClientChannel channel = ServerRpcController
					.getRpcChannel(controller);
			Subject currentUser = authList.get(ServerRpcController
					.getRpcChannel(controller));
			authList.remove(channel);
//			idList.remove(ServerRpcController.getRpcChannel(controller));
			// authList.remove(ServerRpcController.getRpcChannel(controller));
			currentUser.logout();
			log.info("Und user noch in der Liste"
					+ authList.get(ServerRpcController
							.getRpcChannel(controller)));
			done.run(VOID.newBuilder().build());
		}

		@Override
		public void removeallServerNodes(RpcController controller,
				ServerIP request, RpcCallback<VOID> done) {
			Node node = new Node();
			Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			node.setIpAddress(request.getIP());
			DatabaseToStore fromDB = new DatabaseToStore();
			StoreToDatabase storeDB = new StoreToDatabase();
			List<Node> nodelist = fromDB.getNodes(node);
			for (Node nodeex : nodelist) {
				try {
					storeDB.deleteNode(nodeex);
				} catch (Exception e) {
					e.printStackTrace();
					controller.setFailed("Error deleting Node: "
							+ e.getMessage());
					done.run(VOID.newBuilder().build());
				}
				log.info("Node with Id: " + node.getId()
						+ "removed from repository");

			}

			done.run(VOID.newBuilder().build());

		}
	}

}
