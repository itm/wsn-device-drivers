package de.uniluebeck.itm.tcp.server;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
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

import de.uniluebeck.itm.devicedriver.ChipType;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.server.operations.eraseOperation;
import de.uniluebeck.itm.tcp.server.operations.getChipTypeOperation;
import de.uniluebeck.itm.tcp.server.operations.programOperation;
import de.uniluebeck.itm.tcp.server.operations.readFlashOperation;
import de.uniluebeck.itm.tcp.server.operations.readMacOperation;
import de.uniluebeck.itm.tcp.server.operations.resetOperation;
import de.uniluebeck.itm.tcp.server.operations.sendOperation;
import de.uniluebeck.itm.tcp.server.operations.writeFlashOperation;
import de.uniluebeck.itm.tcp.server.operations.writeMacOperation;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.RemoteMessagePacketListener;
import de.uniluebeck.itm.tcp.server.utils.RemoteMessagePlainTextListener;
import de.uniluebeck.itm.tcp.server.utils.ServerDevice;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ByteData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.GetHandleAnswers;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Identification;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketService;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.PacketTypeData;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.STRING;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.server.utils.MessageServiceFiles.sendData;
import de.uniluebeck.itm.tr.util.TimedCache;

/**
 * TCP-Server
 * @author Andreas Maier
 * @author Bjoern Schuett
 *
 */
public class Server {

	/**
	 * logger.
	 */
	private static Logger log = LoggerFactory.getLogger(Server.class);

	// werden nach 30 min alle eintraege des Cache geloescht?
	// wie Timeout fuer einen Eintrag neu starten?
	/**
	 * stores a clientID for every open channel.
	 */
	private static TimedCache<RpcClientChannel, ClientID> idList = new TimedCache<RpcClientChannel, ClientID>(
			30, TimeUnit.MINUTES);
	/**
	 * stores a Shiro subject for every open channel.
	 */
	private static TimedCache<RpcClientChannel, Subject> authList = new TimedCache<RpcClientChannel, Subject>(
			30, TimeUnit.MINUTES);
	// private static HashMap<RpcClientChannel,Subject> authList = new
	// HashMap<RpcClientChannel,Subject>();
	// private static HashMap <String,MessagePacketListener> packetListenerList
	// = new HashMap<String,MessagePacketListener>();
	// private static HashMap <String,MessagePlainTextListener>
	// plainTextListenerList = new HashMap<String,MessagePlainTextListener>();

	private static HashMap<RpcClientChannel, HashMap<String, MessagePacketListener>> packetListenerList = new HashMap<RpcClientChannel, HashMap<String, MessagePacketListener>>();
	private static HashMap<RpcClientChannel, HashMap<String, MessagePlainTextListener>> plainTextListenerList = new HashMap<RpcClientChannel, HashMap<String, MessagePlainTextListener>>();

	/**
	 * contains the objects representing the devices connected to the host.
	 */
	private static ServerDevice serverDevices;
	
	/**
	 * IP of the host the server is running on.
	 */
	private final String host;
	/**
	 * the port ther server is listening on.
	 */
	private final int port;

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            IP of the host.
	 * @param port
	 *            the port the server is listening on.
	 */
	public Server(final String host, final int port) {
		this(host, port, false);
	}

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            IP of the host.
	 * @param port
	 *            the port the server is listening on.
	 * @param metaDaten
	 *            activate MetaDaten-Collector
	 */
	public Server(final String host, final int port, final boolean metaDaten) {
		this.host = host;
		this.port = port;
		serverDevices = new ServerDevice(metaDaten);
	}

	/**
	 * starts the whole Server.
	 */
	public void start() {

		serverDevices.createServerDevices();

		// setzen der server-Informationen
		final PeerInfo serverInfo = new PeerInfo(host, port);

		// setzen des ThreadPools
		final RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10,
				10);

		// setzen des bootstraps
		final DuplexTcpServerBootstrap bootstrap = new DuplexTcpServerBootstrap(
				serverInfo,
				new NioServerSocketChannelFactory(Executors
						.newCachedThreadPool(), Executors.newCachedThreadPool()),
				executor);

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
				DeviceAsync device = idList.get(clientChannel).getDevice();
				for(String key : packetListenerList.get(clientChannel).keySet()){
					device.removeListener(packetListenerList.get(clientChannel).get(key));
				}
				for(String key : plainTextListenerList.get(clientChannel).keySet()){
					device.removeListener(plainTextListenerList.get(clientChannel).get(key));
				}
				authList.remove(clientChannel);
				idList.remove(clientChannel);
				clientChannel.close();
				packetListenerList.remove(clientChannel);
				plainTextListenerList.remove(clientChannel);
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
		bootstrap.getRpcServiceRegistry().registerService(
				Operations.newReflectiveService(new OperationsImpl()));
		bootstrap.getRpcServiceRegistry().registerService(
				PacketService.newReflectiveService(new PacketServiceImpl()));

		// starten des Servers
		bootstrap.bind();

		// ein wenig Kommunikation
		log.info("Serving " + bootstrap);

		/* Initialiesieren von Shiro */

		final Factory<SecurityManager> factory = new IniSecurityManagerFactory(
				"src/main/resources/shiro.ini");
		final SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

	}

	// eigentliche Operationen, die spaeter verwendet werden sollen
	static class OperationsImpl implements Operations.Interface {

		// Methode zum verbinden auf den Server
		// hier sollte die Authentifikation stattfinden
		@Override
		public void connect(final RpcController controller,
				final Identification request,
				final RpcCallback<EmptyAnswer> done) {

			// eine Moeglichkeit den benutzten channel zu identifizieren
			final RpcClientChannel channel = ServerRpcController
					.getRpcChannel(controller);

			// erzeugen einer channel bezogenen User Instanz
			final ClientID id = new ClientID(serverDevices.getDeviceList().get(
					request.getDeviceID()));

			// Abgleich der Userdaten

			/* Shiro: */
			final Subject currentUser = SecurityUtils.getSubject();

			if (!currentUser.isAuthenticated()) {
				final UsernamePasswordToken token = new UsernamePasswordToken(request
						.getUsername(), request.getPassword());
				token.setRememberMe(true);
				try {
					currentUser.login(token);
					// eintragen der ClientID-Instanz zusammen mit den benutzten
					// Channel in eine Liste
					idList.put(channel, id);
					authList.put(channel, currentUser);
					// ausfuehren des Callback
					done.run(EmptyAnswer.newBuilder().build());

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
			}
			/* Shiro END */

		}

		@Override
		public void shutdown(final RpcController controller, final EmptyAnswer request,
				final RpcCallback<EmptyAnswer> done) {
			
			DeviceAsync device = idList.get(ServerRpcController
					.getRpcChannel(controller)).getDevice();

			for(String key : packetListenerList.get(ServerRpcController.getRpcChannel(controller)).keySet()){
				device.removeListener(packetListenerList.get(ServerRpcController.getRpcChannel(controller)).get(key));
			}
			
			for(String key : plainTextListenerList.get(ServerRpcController.getRpcChannel(controller)).keySet()){
				device.removeListener(plainTextListenerList.get(ServerRpcController.getRpcChannel(controller)).get(key));
			}
			
			idList.remove(ServerRpcController.getRpcChannel(controller));
			authList.remove(ServerRpcController.getRpcChannel(controller));
			packetListenerList.remove(ServerRpcController
					.getRpcChannel(controller));
			plainTextListenerList.remove(ServerRpcController
					.getRpcChannel(controller));
			device = null;
			done.run(EmptyAnswer.newBuilder().build());
		}

		// Methode um Device zu Programmieren
		@Override
		public void program(final RpcController controller, final ProgramPacket request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new programOperation(controller, done, user, id, request).run();
		}

		// reagieren auf ein getState-Aufruf
		@Override
		public void getState(final RpcController controller,
				final OpKey request, final RpcCallback<STRING> done) {

			new Runnable() {
				@Override
				public void run() {
					final ClientID id = idList.get(ServerRpcController
							.getRpcChannel(controller));
					if (id != null) {
						final OperationHandle<?> handle = id.getHandleElement(request
								.getOperationKey());
						done.run(STRING.newBuilder().setQuery(
								handle.getState().getName()).build());
					} else {
						controller
								.setFailed("Internal Error, please reconnect and try it again! ");
						done.run(null);
					}
				}
			}.run();
		}

		// reagieren auf ein cancel-Aufruf
		@Override
		public void cancelHandle(final RpcController controller,
				final OpKey request, final RpcCallback<EmptyAnswer> done) {

			new Runnable() {

				@Override
				public void run() {
					final ClientID id = idList.get(ServerRpcController
							.getRpcChannel(controller));
					if (id != null) {
						final OperationHandle<?> handle = id.getHandleElement(request
								.getOperationKey());
						handle.cancel();
						id.deleteHandleElement(handle);
						done.run(EmptyAnswer.newBuilder().build());
					} else {
						controller
								.setFailed("Internal Error, please reconnect and try it again! ");
						done.run(null);
					}

				}
			}.run();
		}

		// reagieren auf ein get-Aufruf
		@Override
		public void getHandle(final RpcController controller,
				final OpKey request, final RpcCallback<GetHandleAnswers> done) {

			new Runnable() {

				@Override
				public void run() {

					final ClientID id = idList.get(ServerRpcController
							.getRpcChannel(controller));
					id.setCalledGet(request.getOperationKey());
					OperationHandle<?> handle = null;

					try {
						// TODO wenn der Aufruf zu schnell passiert, bleibt
						// handle null, deswegen muss man ein wenig warten
						// Thread.sleep(100);

						handle = id.getHandleElement(request.getOperationKey());
						final Object a = handle.get();

						GetHandleAnswers response = null;

						if (a == null) {
							response = GetHandleAnswers.newBuilder()
									.setEmptyAnswer(
											EmptyAnswer.newBuilder().build())
									.build();
						} else if (a.getClass().getName().contains("ChipType")) {

							response = GetHandleAnswers.newBuilder()
									.setChipData(
											STRING.newBuilder().setQuery(
													((ChipType) a).name())
													.build()).build();
						} else if (a.getClass().getName()
								.contains("MacAddress")) {
							final MacData mac = MacData.newBuilder().addMACADDRESS(
									ByteString.copyFrom(((MacAddress) a)
											.getMacBytes())).build();
							response = GetHandleAnswers.newBuilder()
									.setMacAddress(mac).build();
						} else if (a.getClass().getName().contains("[B")) {
							final ByteData bytes = ByteData.newBuilder().addData(
									ByteString.copyFrom(((byte[]) a).clone()))
									.build();
							response = GetHandleAnswers.newBuilder().setData(
									bytes).build();
						}
						done.run(response);
					} catch (final Exception e) {
						controller.setFailed("Error in get()-Operation");
						done.run(null);
					}
					id.deleteHandleElement(handle);
					id.removeCalledGet(request.getOperationKey());

				}
			}.run();

		}

		@Override
		public void writeMac(final RpcController controller,
				final MacData request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new writeMacOperation(controller, done, user, id, request).run();

		}

		@Override
		public void writeFlash(final RpcController controller, final FlashData request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new writeFlashOperation(controller, done, user, id, request).run();
		}

		@Override
		public void eraseFlash(final RpcController controller, final Timeout request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new eraseOperation(controller, done, user, id, request).run();
		}

		@Override
		public void readFlash(final RpcController controller, final FlashData request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new readFlashOperation(controller, done, user, id, request).run();

		}

		@Override
		public void readMac(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new readMacOperation(controller, done, user, id, request).run();

		}

		@Override
		public void reset(final RpcController controller, final Timeout request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new resetOperation(controller, done, user, id, request).run();

		}

		@Override
		public void send(final RpcController controller, final sendData request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new sendOperation(controller, done, user, id, request).run();

		}

		@Override
		public void getChipType(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			new getChipTypeOperation(controller, done, user, id, request).run();
		}
	}

	static class PacketServiceImpl implements PacketService.Interface {

		@Override
		public void addMessagePacketListener(final RpcController controller,
				final PacketTypeData request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			new Runnable() {

				@Override
				public void run() {

					final DeviceAsync deviceAsync = idList.get(
							ServerRpcController.getRpcChannel(controller))
							.getDevice();

					if (deviceAsync == null) {
						controller
								.setFailed("Error while adding a Packet-Listener");
						done.run(null);
						return;
					}

					final int[] types = new int[request.getTypeCount()];
					for (int i = 0; i < request.getTypeCount(); i++) {
						types[i] = request.getType(i);
					}

					final MessagePacketListener listener = new MessagePacketListener() {

						@Override
						public void onMessagePacketReceived(
								final MessageEvent<MessagePacket> event) {
							final RemoteMessagePacketListener remoteListener = new RemoteMessagePacketListener(
									request.getOperationKey(),
									ServerRpcController
											.getRpcChannel(controller));
							remoteListener.onMessagePacketReceived(event);
						}
					};

					deviceAsync.addListener(listener, types);

					// packetListenerList.put(request.getOperationKey(),
					// listener);

					final HashMap<String, MessagePacketListener> a = new HashMap<String, MessagePacketListener>();
					a.put(request.getOperationKey(), listener);

					packetListenerList.put(ServerRpcController
							.getRpcChannel(controller), a);

					done.run(EmptyAnswer.newBuilder().build());

				}
			}.run();
		}

		@Override
		public void addMessagePlainTextListener(final RpcController controller,
				final PacketTypeData request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			new Runnable() {

				@Override
				public void run() {

					final DeviceAsync deviceAsync = idList.get(
							ServerRpcController.getRpcChannel(controller))
							.getDevice();

					if (deviceAsync == null) {
						controller
								.setFailed("Error while adding a Plaintext-Listener");
						done.run(null);
						return;
					}

					final MessagePlainTextListener listener = new MessagePlainTextListener() {

						@Override
						public void onMessagePlainTextReceived(
								final MessageEvent<MessagePlainText> message) {

							final RemoteMessagePlainTextListener remoteListener = new RemoteMessagePlainTextListener(
									request.getOperationKey(),
									ServerRpcController
											.getRpcChannel(controller));
							remoteListener.onMessagePlainTextReceived(message);
						}
					};

					deviceAsync.addListener(listener);

					// plainTextListenerList.put(request.getOperationKey(),
					// listener);

					final HashMap<String, MessagePlainTextListener> a = new HashMap<String, MessagePlainTextListener>();
					a.put(request.getOperationKey(), listener);

					plainTextListenerList.put(ServerRpcController
							.getRpcChannel(controller), a);

					done.run(EmptyAnswer.newBuilder().build());

				}
			}.run();
		}

		@Override
		public void removeMessagePacketListener(final RpcController controller,
				final OpKey request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			new Runnable() {

				@Override
				public void run() {

					final DeviceAsync deviceAsync = idList.get(
							ServerRpcController.getRpcChannel(controller))
							.getDevice();
					final HashMap<String, MessagePacketListener> a = packetListenerList
							.get(ServerRpcController.getRpcChannel(controller));

					if (deviceAsync == null
							|| (!packetListenerList
									.containsKey(ServerRpcController
											.getRpcChannel(controller)) && a
									.containsKey(request.getOperationKey()))) {
						controller
								.setFailed("Error while removing a Packet-Listener");
						done.run(null);
						return;
					}

					deviceAsync
							.removeListener(a.get(request.getOperationKey()));
					// packetListenerList.remove(request.getOperationKey());
					a.remove(ServerRpcController.getRpcChannel(controller));
					packetListenerList.put(ServerRpcController
							.getRpcChannel(controller), a);

					done.run(EmptyAnswer.newBuilder().build());

				}
			}.run();
		}

		@Override
		public void removeMessagePlainTextListener(
				final RpcController controller, final OpKey request,
				final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			new Runnable() {

				@Override
				public void run() {
					final DeviceAsync deviceAsync = idList.get(
							ServerRpcController.getRpcChannel(controller))
							.getDevice();
					final HashMap<String, MessagePlainTextListener> a = plainTextListenerList
							.get(ServerRpcController.getRpcChannel(controller));

					if (deviceAsync == null
							|| (!plainTextListenerList
									.containsKey(ServerRpcController
											.getRpcChannel(controller)) && a
									.containsKey(request.getOperationKey()))) {
						controller
								.setFailed("Error while removing a Plaintext-Listener");
						done.run(null);
						return;
					}

					deviceAsync
							.removeListener(a.get(request.getOperationKey()));
					a.remove(request.getOperationKey());
					plainTextListenerList.put(ServerRpcController
							.getRpcChannel(controller), a);
					done.run(EmptyAnswer.newBuilder().build());
				}
			}.run();
		}
	}

}
