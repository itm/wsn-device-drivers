package de.uniluebeck.itm.metadaten.metadatenserver;

import java.io.File;
import java.util.ArrayList;
import de.uniluebeck.itm.metadaten.entities.ConfigData;
import java.util.List;
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
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchRequest;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchResponse;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.server.helper.NodeHelper;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;
import de.uniluebeck.itm.tr.util.TimedCache;

public class MetaDatenServer {

	private static Log log = LogFactory.getLog(MetaDatenServer.class);
	// werden nach 30 min alle eintraege des Cache geloescht?
	// wie Timeout fuer einen Eintrag neu starten?
	private static TimedCache<RpcClientChannel, ClientID> idList = new TimedCache<RpcClientChannel, ClientID>();
	private static TimedCache<RpcClientChannel, Subject> authList = new TimedCache<RpcClientChannel, Subject>();
	public static List<Node> knotenliste = new ArrayList<Node>();

	public static void main(String[] args) {

		ConfigData config = loadConfig("config.xml");
		log.info("Startup Server!");
		CleanRepository cleaner = new CleanRepository(config.getOveragetime());
		cleaner.timer.schedule(cleaner, config.getTimerdelay(), config.getTimerinterval());
		// setzen der server-Informationen
		PeerInfo serverInfo = new PeerInfo(config.getServerIP(), config.getPort());

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

		Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				"shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		
	}
	/**
	 * Load of  ConfigData needed for communication
	 * @param fileurl
	 * @return
	 */
	public static ConfigData loadConfig(String fileurl){
		ConfigData config = new ConfigData();
		Serializer serializer = new Persister();
		File source = new File(fileurl);

		 try {
			config = serializer.read(ConfigData.class, source);
//			serializer.read(ConfigData, source);
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
			ClientID id = new ClientID();

			// Abgleich der Userdaten
			log.info("Checking password");
			/* Shiro: */
			Subject currentUser = SecurityUtils.getSubject();

			if (!currentUser.isAuthenticated()) {
				UsernamePasswordToken token = new UsernamePasswordToken(
						request.getUsername(), request.getPassword());
				token.setRememberMe(true);
				try {

					currentUser.login(token);
					// eintragen der ClientID-Instanz zusammen mit den benutzten
					// Channel in eine Liste
					idList.put(channel, id);
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
			}
			/* Shiro END */

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
			log.info("Node with Id: " + node.getId() + "removed from repository");

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
			// ClientID id =
			// idList.get(ServerRpcController.getRpcChannel(controller));
			DatabaseToStore getfromDB = new DatabaseToStore();
			NodeHelper nhelper = new NodeHelper();
			List<Node> resultlist = new ArrayList<Node>();
			SearchResponse.Builder responsebuilder = SearchResponse
					.newBuilder();
			if (!(request.getQueryMs() == null)) {
				Node node = nhelper.changeToNode(request.getQueryMs());
				resultlist = getfromDB.getNodes(node);
			}
			if (!(request.getQueryString() == null)) {
				// TODO
				log.info("Operation: search by QueryString - Not supported yet");
			}
			for (Node result : resultlist) {
				responsebuilder.addResponse(nhelper.changetoNODE(result));
			}
			log.info("Finales mopped erstellen");
			SearchResponse response = responsebuilder.build();
			log.info("Send searchResponse to client: "
					+ ServerRpcController.getRpcChannel(controller)
							.getPeerInfo().getHostName());
			done.run(response);
		}
	}

}