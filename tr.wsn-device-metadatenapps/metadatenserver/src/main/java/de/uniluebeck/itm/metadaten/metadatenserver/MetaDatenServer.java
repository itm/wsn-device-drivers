package de.uniluebeck.itm.metadaten.metadatenserver;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

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
import de.uniluebeck.itm.metadaten.entities.NodeId;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Identification;
import de.uniluebeck.itm.metadaten.files.MetaDataService.NODE;
import de.uniluebeck.itm.metadaten.files.MetaDataService.Operations;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchRequest;
import de.uniluebeck.itm.metadaten.files.MetaDataService.SearchResponse;
import de.uniluebeck.itm.metadaten.files.MetaDataService.ServerIP;
import de.uniluebeck.itm.metadaten.files.MetaDataService.VOID;
import de.uniluebeck.itm.metadaten.server.config.ConfigData;
import de.uniluebeck.itm.metadaten.server.exception.NodeInDBException;
import de.uniluebeck.itm.metadaten.server.helper.ConfigReader;
import de.uniluebeck.itm.metadaten.server.helper.NodeHelper;
import de.uniluebeck.itm.persistence.DatabaseToStore;
import de.uniluebeck.itm.persistence.StoreToDatabase;
import de.uniluebeck.itm.tr.util.TimedCache;

/**
 * Server Class for the MetaDataRepository It provides all necessary function to
 * manipulate the Repository
 * 
 * @author babel
 * 
 */
public class MetaDatenServer {

	/**
	 * Logger for the server
	 */
	private static Log log = LogFactory.getLog(MetaDatenServer.class);
	// werden nach 30 min alle eintraege des Cache geloescht?
	// wie Timeout fuer einen Eintrag neu starten?
	// private static TimedCache<RpcClientChannel, ClientID> idList = new
	// TimedCache<RpcClientChannel, ClientID>();
	private static TimedCache<RpcClientChannel, Subject> authList = new TimedCache<RpcClientChannel, Subject>(
			30, TimeUnit.SECONDS);

	// private static Map<RpcClientChannel, ClientID> idList = new
	// HashMap<RpcClientChannel, ClientID>();
	/**
	 * List with authenticated CLients
	 */
	// private static Map<RpcClientChannel, Subject> authList = new
	// HashMap<RpcClientChannel, Subject>();

	/**
	 * Main method for the servers
	 * 
	 * @param args
	 *            1st argument for the path to the config file
	 * @throws URISyntaxException
	 */
	public static void main(final String[] args) throws URISyntaxException {
		final String file = (args.length < 1) ? "src/main/resources/config.xml"
				: args[0];
		ConfigData config = null;
		try {
			config = ConfigReader.readConfigFile(new File(file));
		} catch (final JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Startup Server!");
		final CleanRepository cleaner = new CleanRepository(config
				.getOveragetime().intValue());
		cleaner.timer.schedule(cleaner, config.getTimerdelay().longValue(),
				config.getTimerinterval().longValue());
		// setzen der server-Informationen
		final PeerInfo serverInfo = new PeerInfo(config.getServerIP(), config
				.getServerPort().intValue());

		// setzen des ThreadPools
		final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10,
				10);

		// setzen des bootstraps
		final DuplexTcpServerBootstrap bootstrap = new DuplexTcpServerBootstrap(
				serverInfo, new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()), executor);

		// setzen eines ConnectionLoggers
		final RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
		final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

			@Override
			public void connectionReestablished(
					final RpcClientChannel clientChannel) {
				log.info("connectionReestablished " + clientChannel);
			}

			@Override
			public void connectionOpened(final RpcClientChannel clientChannel) {
				log.info("connectionOpened " + clientChannel);
			}

			@Override
			public void connectionLost(final RpcClientChannel clientChannel) {
				log.info("connectionLost " + clientChannel);
			}

			@Override
			public void connectionChanged(final RpcClientChannel clientChannel) {
				log.info("connectionChanged " + clientChannel);
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
		log.info("Serving started: " + bootstrap);

		/* Initialiesieren von Shiro */
		// URI fileuri = null;
		// try {
		// fileuri =
		// ClassLoader.getSystemResource("shiro.ini").getPath().toURI();
		// } catch (URISyntaxException e) {
		// log.error(e.getMessage());
		// }
		// File source = new File(fileuri);

		final Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				ClassLoader.getSystemResource("shiro.ini").toURI().getPath());
		final SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

	}

	/**
	 * Class implements the operations used of client and server
	 * 
	 * @author babel
	 * 
	 */
	static class OperationsImpl implements Operations.Interface {

		// Methode zum verbinden auf den MetaDatenServer
		// hier sollte die Authentifikation stattfinden

		@Override
		public void connect(final RpcController controller,
				final Identification request, final RpcCallback<VOID> done) {

			// eine Moeglichkeit den benutzten channel zu identifizieren
			final RpcClientChannel channel = ServerRpcController
					.getRpcChannel(controller);
			// erzeugen einer channel bezogenen User Instanz
			// ClientID id = new ClientID();

			// Abgleich der Userdaten
			log.info("Checking authentication");
			/* Shiro: */
			Subject currentUser;
			if (authList.get(channel) == null) {
				log.info("user not in authenticationlist - new authentication process started");
				currentUser = SecurityUtils.getSubject();
			} else {
				log.info("User in authenticationlist - getting user information");
				currentUser = authList.get(channel);
			}
			if (!currentUser.isAuthenticated()) {
				log.info("checking user and password");
				final UsernamePasswordToken token = new UsernamePasswordToken(
						request.getUsername(), request.getPassword());
				token.setRememberMe(true);
				try {

					currentUser.login(token);
					// eintragen der ClientID-Instanz zusammen mit den benutzten
					// Channel in eine Liste
					// idList.put(channel, id);
					authList.put(channel, currentUser);
					// ausfuehren des Callback
					done.run(VOID.newBuilder().build());

				} catch (final UnknownAccountException uae) {
					controller.setFailed("There is no user with username of "
							+ token.getPrincipal());
					done.run(null);
					return;
				} catch (final IncorrectCredentialsException ice) {
					controller.setFailed("Password for account "
							+ token.getPrincipal() + " was incorrect!");
					done.run(null);
					return;
				} catch (final LockedAccountException lae) {
					controller
							.setFailed("The account for username "
									+ token.getPrincipal()
									+ " is locked.  "
									+ "Please contact your administrator to unlock it.");
					done.run(null);
					return;
				} catch (final AuthenticationException ae) {
					controller.setFailed(ae.getMessage());
					done.run(null);
					return;
				}
			} else {
				done.run(VOID.newBuilder().build());
			}
			/* Shiro END */
			if ((currentUser.isAuthenticated())
					&& (!(authList.get(channel) == null))) {
				log.info("Authentication successfull");
			} else {
				log.error(("Authentication not successful "
						+ currentUser.isAuthenticated() + "User in authlist" + authList
						.containsKey(channel)));
			}
		}

		@Override
		public void add(final RpcController controller, final NODE request,
				final RpcCallback<VOID> done) {
			final NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Authentification failed!");
				done.run(null);

				return;
			}
			if (request.getKnotenid() == null) {
				controller.setFailed("Node without Id!");
				done.run(null);

				return;
			}
			node = nhelper.changeToNode(request);
			node.setTimestamp(new Date());
			final StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.storeNode(node);
			} catch (final NodeInDBException e) {
				log.error(e.getStackTrace());
				controller.setFailed("Error saving Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Node with Id: " + node.getId() + "added to repository");

			done.run(VOID.newBuilder().build());

		}

		@Override
		public void remove(final RpcController controller, final NODE request,
				final RpcCallback<VOID> done) {
			final NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			node = nhelper.changeToNode(request);
			final StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.deleteNode(node);
			} catch (final Exception e) {
				e.printStackTrace();
				controller.setFailed("Error deleting Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Node with Id: " + node.getId()
					+ "removed from repository");

			done.run(VOID.newBuilder().build());

		}

		@Override
		public void refresh(final RpcController controller, final NODE request,
				final RpcCallback<VOID> done) {
			final NodeHelper nhelper = new NodeHelper();
			Node node = new Node();
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			log.debug("Method: refresh: User: " + user == null
					+ "user authenticated? " + user.isAuthenticated());
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			if (request.getKnotenid() == null) {
				controller.setFailed("Node without Id cannot be updated!");
				done.run(null);

				return;
			}
			// ClientID id =
			// idList.get(ServerRpcController.getRpcChannel(controller));
			node = nhelper.changeToNode(request);
			node.setTimestamp(new Date());
			final StoreToDatabase storeDB = new StoreToDatabase();
			try {
				storeDB.updateNode(node);
			} catch (final Exception e) {
				e.printStackTrace();
				controller.setFailed("Error updating Node: " + e.getMessage());
				done.run(VOID.newBuilder().build());
			}
			log.info("Updated Node with Id: " + node.getId());

			done.run(VOID.newBuilder().build());
		}

		@Override
		public void search(final RpcController controller,
				final SearchRequest request,
				final RpcCallback<SearchResponse> done) {
			log.info("Searchquery by Client: "
					+ ServerRpcController.getRpcChannel(controller)
							.getPeerInfo().getHostName() + " started");
			final DatabaseToStore getfromDB = new DatabaseToStore();
			final NodeHelper nhelper = new NodeHelper();
			List<Node> resultlist = new ArrayList<Node>();
			final SearchResponse.Builder responsebuilder = SearchResponse
					.newBuilder();
			if (!(request.getQueryMs() == null)) {
				Node node = nhelper.changeToNode(request.getQueryMs());
				node = nhelper.removeEmptyStrings(node);
				log.info("search started");
				log.info("NodeID vom CLient" + node.getId().getId()
						+ "Rest des Requests" + node.getMicrocontroller());
				// log.info("true? " + (node.getMicrocontroller().matches("")));
				resultlist = getfromDB.getNodes(node, false);
				log.info("search ended and delivered " + resultlist.size()
						+ " results ");
			}
			if (!(request.getQueryString() == null)) {
				// TODO
				log.info("Operation: search by QueryString - Not supported yet");
			}
			log.info("Result Groesse!!!" + resultlist.size());
			for (Node result : resultlist) {
				log.info("Result" + result.getId());
				responsebuilder.addResponse(nhelper.changetoNODE(result));
			}

			log.info("Finales mopped erstellen");
			final SearchResponse response = responsebuilder.build();
			log.info("Send searchResponse to client: "
					+ ServerRpcController.getRpcChannel(controller)
							.getPeerInfo().getHostName());
			done.run(response);
		}

		@Override
		public void disconnect(final RpcController controller,
				final Identification request, final RpcCallback<VOID> done) {
			// eine Moeglichkeit den benutzten channel zu identifizieren
			final RpcClientChannel channel = ServerRpcController
					.getRpcChannel(controller);
			final Subject currentUser = authList.get(ServerRpcController
					.getRpcChannel(controller));
			log.info("Disconnect Client" + channel + "User" + currentUser);
			log.debug("Size of authList before removal of channel in dsiconnect()"
					+ authList.size());
			authList.remove(channel);
			log.debug("Size of authList after removal of channel in dsiconnect()"
					+ authList.size());
			// idList.remove(ServerRpcController.getRpcChannel(controller));
			// authList.remove(ServerRpcController.getRpcChannel(controller));
			try{
				currentUser.logout();
			}catch(final NullPointerException e){
				log.error("Error while logging out user." );
				log.error(e.getStackTrace());
			}
			log.info("Und user noch in der Liste"
					+ authList.get(ServerRpcController
							.getRpcChannel(controller)));
			done.run(VOID.newBuilder().build());
		}

		@Override
		public void removeallServerNodes(final RpcController controller,
				final ServerIP request, final RpcCallback<VOID> done) {
			final Node node = new Node();
			final NodeId id = new NodeId();
			final DatabaseToStore fromDB = new DatabaseToStore();
			final StoreToDatabase storeDB = new StoreToDatabase();
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("You are not authenticated!");
				done.run(null);

				return;
			}
			id.setIpAdress(request.getIP());
			node.setId(id);
			final List<Node> nodelist = fromDB.getNodes(node, true);
			for (Node nodeex : nodelist) {
				try {
					storeDB.deleteNode(nodeex);
				} catch (final Exception e) {
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
