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
import org.apache.shiro.config.ConfigurationException;
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
import de.uniluebeck.itm.tcp.server.operations.EraseFlashOperation;
import de.uniluebeck.itm.tcp.server.operations.GetChipTypeOperation;
import de.uniluebeck.itm.tcp.server.operations.ProgramOperation;
import de.uniluebeck.itm.tcp.server.operations.ReadFlashOperation;
import de.uniluebeck.itm.tcp.server.operations.ReadMacOperation;
import de.uniluebeck.itm.tcp.server.operations.ResetOperation;
import de.uniluebeck.itm.tcp.server.operations.SendOperation;
import de.uniluebeck.itm.tcp.server.operations.WriteFlashOperation;
import de.uniluebeck.itm.tcp.server.operations.WriteMacOperation;
import de.uniluebeck.itm.tcp.server.utils.ClientID;
import de.uniluebeck.itm.tcp.server.utils.RemoteMessageListener;
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
 * 
 * @author Andreas Maier
 * @author Bjoern Schuett
 * 
 */
public class Server {

	/**
	 * logger.
	 */
	private static Logger log = LoggerFactory.getLogger(Server.class);

	/**
	 * default authentication time
	 */
	private final static int TIMEOUT = 30;

	/**
	 * stores a clientID for every open channel.
	 */
	private static TimedCache<RpcClientChannel, ClientID> idList = new TimedCache<RpcClientChannel, ClientID>(
			TIMEOUT, TimeUnit.MINUTES);
	/**
	 * stores a Shiro subject for every open channel.
	 */
	private static TimedCache<RpcClientChannel, Subject> authList = new TimedCache<RpcClientChannel, Subject>(
			TIMEOUT, TimeUnit.MINUTES);

	/**
	 * packetListenerList
	 */
	private static HashMap<RpcClientChannel, HashMap<String, MessagePacketListener>> packetListenerList = new HashMap<RpcClientChannel, HashMap<String, MessagePacketListener>>();
	/**
	 * plainTextListenerList
	 */
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
	 * Path of the shiro-config File
	 */
	private String shiroConfig = "";

	/**
	 * Constructor.
	 * 
	 * @param host
	 *            IP of the host.
	 * @param port
	 *            the port the server is listening on.
	 * @param devicesPath
	 *            the path of the config-file (devices.xml)
	 * @param shiroConfigPath
	 *            the path of the config-file (shiro.ini)
	 * @param configPath
	 *            the path of the config-file (config.xml)
	 * @param sensorsPath
	 *            the path of the config-file (sensors.xml)
	 * @param metaDaten
	 *            activate MetaDaten-Collector
	 */
	public Server(final String host, final int port, final String devicesPath,
			final String shiroConfigPath, final String configPath,
			final String sensorsPath, final boolean metaDaten) {
		this.host = host;
		this.port = port;
		this.shiroConfig = shiroConfigPath;
		serverDevices = new ServerDevice(devicesPath, configPath, sensorsPath,
				metaDaten);
	}

	/**
	 * starts the whole Server.
	 */
	public void start() {

		// erzeugen der Connection zu den Devices und starten der
		// MetaDataCollection
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
				// suaberes beenden der Verbindung und entfernen aller
				// Ressourcen
				if (!idList.isEmpty() && null != idList.get(clientChannel)) {
					final DeviceAsync device = idList.get(clientChannel)
							.getDevice();
					if (!packetListenerList.isEmpty()
							&& !packetListenerList.get(clientChannel).isEmpty()) {
						for (String key : packetListenerList.get(clientChannel)
								.keySet()) {
							device.removeListener(packetListenerList.get(
									clientChannel).get(key));
						}
						packetListenerList.remove(clientChannel);
					}
					if (!plainTextListenerList.isEmpty()
							&& !plainTextListenerList.get(clientChannel)
									.isEmpty()) {
						for (String key : plainTextListenerList.get(
								clientChannel).keySet()) {
							device.removeListener(plainTextListenerList.get(
									clientChannel).get(key));
						}
						plainTextListenerList.remove(clientChannel);
					}
					// entfernen des Clients aus der authentifiziert-Liste
					authList.remove(clientChannel);
					// entfernen des ClientID-Objektes
					idList.remove(clientChannel);
				}
				clientChannel.close();
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
		Factory<SecurityManager> factory = null;
		try {
			factory = new IniSecurityManagerFactory(shiroConfig);
		} catch (final ConfigurationException e) {
			log.error(e.getMessage());
			System.exit(-1);
		}
		final SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

	}

	// eigentliche Operationen, die spaeter verwendet werden sollen
	/**
	 * Implements the Operations from the Operations.Interface
	 * 
	 * @author Andreas Maier
	 */
	static class OperationsImpl implements Operations.Interface {

		// Methode zum verbinden auf den Server
		// hier sollte die Authentifikation stattfinden

		/**
		 * establish a Connection from a client
		 * 
		 * @param controller
		 *            RpcController
		 * @param request
		 *            the UserData
		 * @param done
		 *            RpcCallback<EmptyAnswer>
		 */
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
				final UsernamePasswordToken token = new UsernamePasswordToken(
						request.getUsername(), request.getPassword());
				token.setRememberMe(true);
				try {
					currentUser.login(token);
					// eintragen der ClientID-Instanz mit den benutzten
					// Channel in eine Liste
					idList.put(channel, id);
					authList.put(channel, currentUser);
					// ausfuehren des erfolgreichen Callbacks
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
			} else { // Wenn ein Benutzer bereits authentifiziert war, wird er
				// hier erneut eingetragen
				idList.put(channel, id);
				authList.put(channel, currentUser);
				done.run(EmptyAnswer.newBuilder().build());
			}
			/* Shiro END */

		}

		// reagieren auf ein getState-Aufruf
		/**
		 * react to a GetState-Call from the Client
		 * @param controller the RpcController for this operation
		 * @param request the OpKey-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void getState(final RpcController controller,
				final OpKey request, final RpcCallback<STRING> done) {

			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));
			if (id != null) {
				// finden des richtigen OperationHandle fuer den aktuellen Call
				final OperationHandle<?> handle = id.getHandleElement(request
						.getOperationKey());
				/*
				 * ausfuehren der getState-Anfrage auf dem physikalischen Device
				 * und senden der Antwort an den Antwort an den Client
				 */
				done.run(STRING.newBuilder().setQuery(
						handle.getState().getName()).build());
			} else { // Fehlerfall, sollte im normalen Betrieb nicht auftreten
				controller
						.setFailed("Internal Error, please reconnect and try it again! ");
				done.run(null);
			}
		}

		// reagieren auf ein cancel-Aufruf
		/**
		 * react to a Cancel-Call from the Client
		 * @param controller the RpcController for this operation
		 * @param request the OpKey-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void cancelHandle(final RpcController controller,
				final OpKey request, final RpcCallback<EmptyAnswer> done) {

			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));
			if (id != null) {
				/* finden des richtigen OperationHandle fuer den aktuellen Call */
				final OperationHandle<?> handle = id.getHandleElement(request
						.getOperationKey());
				/* ausfuehren der Cancel-Anfrage auf dem physikalischen Device */
				handle.cancel();
				/* loeschen des OperationHandle im ClientID-Objekt */
				id.deleteHandleElement(handle);
				/* senden der Bestaetigung an den Client */
				done.run(EmptyAnswer.newBuilder().build());
			} else {
				controller
						.setFailed("Internal Error, please reconnect and try it again! ");
				done.run(null);
			}
		}

		// reagieren auf ein get-Aufruf
		/**
		 * react to a Get-Call from the Client
		 * @param controller the RpcController for this operation
		 * @param request the OpKey-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void getHandle(final RpcController controller,
				final OpKey request, final RpcCallback<GetHandleAnswers> done) {

			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));
			/*
			 * setzen eines CalledGet-Flag, um eine onSuccess-Nachricht beim
			 * Client zu verhindern
			 */
			id.setCalledGet(request.getOperationKey());
			OperationHandle<?> handle = null;

			try {
				/* finden des richtigen OperationHandle fuer den aktuellen Call */
				handle = id.getHandleElement(request.getOperationKey());
				/* ausfuehren des get auf dem physikalischen Device */
				final Object a = handle.get();

				GetHandleAnswers response = null;

				/*
				 * herausfinden welche Art von Antwort vom get-Aufruf
				 * zurueckgegeben wurde und aufbauen einer dementsprechenden
				 * Antwort
				 */
				if (a == null) { // Antwort auf Void als Datentyp
					response = GetHandleAnswers.newBuilder().setEmptyAnswer(
							EmptyAnswer.newBuilder().build()).build();
				} else if (a.getClass().getName().contains("ChipType")) {
					/* Antwort auf ChipType als Datentyp */
					response = GetHandleAnswers.newBuilder().setChipData(
							STRING.newBuilder().setQuery(((ChipType) a).name())
									.build()).build();
				} else if (a.getClass().getName().contains("MacAddress")) {
					/* Antwort auf MacAddress als Datentyp */
					final MacData mac = MacData.newBuilder()
							.addMACADDRESS(
									ByteString.copyFrom(((MacAddress) a)
											.getMacBytes())).build();
					response = GetHandleAnswers.newBuilder().setMacAddress(mac)
							.build();
				} else if (a.getClass().getName().contains("[B")) {
					/* Antwort auf byte[] als Datentyp */
					final ByteData bytes = ByteData.newBuilder().addData(
							ByteString.copyFrom(((byte[]) a).clone())).build();
					response = GetHandleAnswers.newBuilder().setData(bytes)
							.build();
				}
				/* absenden der eigentlichen Antwort */
				done.run(response);
			} catch (final Exception e) {
				log.error("", e);
				controller.setFailed(e.getMessage());
				done.run(null);
			}
			/*
			 * entfernen des OperationHandle aus dem ClientID-Object, da
			 * Operation fertig ist
			 */
			id.deleteHandleElement(handle);
			/* reset des CalledGet-Flag, um normalen Betrieb wieder herzustellen */
			id.removeCalledGet(request.getOperationKey());
		}

		// Methode um Device zu Programmieren
		/**
		 * program a Device
		 * @param controller the RpcController for this operation
		 * @param request the ProgramPacket-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void program(final RpcController controller,
				final ProgramPacket request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer program-Operation auf einem
			 * physikalischen Device
			 */
			new ProgramOperation(controller, done, user, id, request).execute();
		}

		/**
		 * write the MacAddress on a Device
		 * @param controller the RpcController for this operation
		 * @param request the MacData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void writeMac(final RpcController controller,
				final MacData request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer writeMac-Operation auf einem
			 * physikalischen Device
			 */
			new WriteMacOperation(controller, done, user, id, request)
					.execute();

		}

		/**
		 * write the Flash on a Device
		 * @param controller the RpcController for this operation
		 * @param request the FlashData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void writeFlash(final RpcController controller,
				final FlashData request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer writeFlash-Operation auf einem
			 * physikalischen Device
			 */
			new WriteFlashOperation(controller, done, user, id, request)
					.execute();
		}

		/**
		 * erase the Flash on a Device
		 * @param controller the RpcController for this operation
		 * @param request the Timeout-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void eraseFlash(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer eraseFlash-Operation auf einem
			 * physikalischen Device
			 */
			new EraseFlashOperation(controller, done, user, id, request)
					.execute();
		}

		/**
		 * read the Flash on a Device
		 * @param controller the RpcController for this operation
		 * @param request the FlashData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void readFlash(final RpcController controller,
				final FlashData request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer readFlash-Operation auf einem
			 * physikalischen Device
			 */
			new ReadFlashOperation(controller, done, user, id, request)
					.execute();

		}

		/**
		 * read the MacAddress on a Device
		 * @param controller the RpcController for this operation
		 * @param request the Timeout-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void readMac(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer readMac-Operation auf einem
			 * physikalischen Device
			 */
			new ReadMacOperation(controller, done, user, id, request).execute();

		}

		/**
		 * reset a Device
		 * @param controller the RpcController for this operation
		 * @param request the Timeout-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void reset(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer reset-Operation auf einem
			 * physikalischen Device
			 */
			new ResetOperation(controller, done, user, id, request).execute();

		}

		/**
		 * send a Message to a Device
		 * @param controller the RpcController for this operation
		 * @param request the sendData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void send(final RpcController controller,
				final sendData request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer send-Operation auf einem
			 * physikalischen Device
			 */
			new SendOperation(controller, done, user, id, request).execute();

		}

		/**
		 * get the Chiptype from a Device
		 * @param controller the RpcController for this operation
		 * @param request the Timeout-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void getChipType(final RpcController controller,
				final Timeout request, final RpcCallback<EmptyAnswer> done) {

			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));

			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController
					.getRpcChannel(controller));

			/*
			 * erzeugen und ausfuehren einer getChipType-Operation auf einem
			 * physikalischen Device
			 */
			new GetChipTypeOperation(controller, done, user, id, request)
					.execute();
		}
	}

	/**
	 * Implements the Operations from the PacketService.Interface
	 * 
	 * @author Andreas Maier
	 * 
	 */
	static class PacketServiceImpl implements PacketService.Interface {

		/**
		 * add a MessagePacketListener to a Device
		 * @param controller the RpcController for this operation
		 * @param request the PacketTypeData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void addMessagePacketListener(final RpcController controller,
				final PacketTypeData request,
				final RpcCallback<EmptyAnswer> done) {

			/* kontorllieren der Berechtigung */
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			/* finden des richtigen Device */
			final DeviceAsync deviceAsync = idList.get(
					ServerRpcController.getRpcChannel(controller)).getDevice();

			/*
			 * wenn richtiges Device nicht gefunden wurde, sollte im normalen
			 * Betrieb nicht vorkommen
			 */
			if (deviceAsync == null) {
				controller.setFailed("Error while adding a Packet-Listener");
				done.run(null);
				return;
			}

			final int[] types = new int[request.getTypeCount()];
			for (int i = 0; i < request.getTypeCount(); i++) {
				types[i] = request.getType(i);
			}

			// TODO vlt MessagePacketListener und MessagePlainTextListener durch
			// eine Methode verarbeitens

			/* wiederherstellen eines listener-Objekt */
			final MessagePacketListener listener = new MessagePacketListener() {

				@Override
				public void onMessagePacketReceived(
						final MessageEvent<MessagePacket> event) {
					/*
					 * uebergeben eines RemoteMessageListener, der die Antworten
					 * des Devices an den Client weiterreichen wird
					 */
					final RemoteMessageListener remoteListener = new RemoteMessageListener(
							request.getOperationKey(), ServerRpcController
									.getRpcChannel(controller));
					remoteListener.onMessagePacketReceived(event);
				}
			};

			deviceAsync.addListener(listener, types);

			/*
			 * erzeugen einer temporaeren HashMap, um die Beziehung zwischen
			 * OpKey und Listener-Objekt zu erhalten
			 */
			final HashMap<String, MessagePacketListener> temp = new HashMap<String, MessagePacketListener>();
			temp.put(request.getOperationKey(), listener);

			/*
			 * hinzufuegen der temporaeren HashMap zur packetListenerList. Dies
			 * ist notwendig, da ein Client den gleichen Channel benutzt aber
			 * mehrere Operationen starten kann. Diese Beziehung muss zur
			 * Verwaltung der Listener erhalten bleiben.
			 */
			packetListenerList.put(ServerRpcController
					.getRpcChannel(controller), temp);

			done.run(EmptyAnswer.newBuilder().build());
		}

		/**
		 * add a MessagePlainTextListener to a Device
		 * @param controller the RpcController for this operation
		 * @param request the PacketTypeData-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void addMessagePlainTextListener(final RpcController controller,
				final PacketTypeData request,
				final RpcCallback<EmptyAnswer> done) {

			/* Kommentare analog zu addMessagePacketListener */
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			final DeviceAsync deviceAsync = idList.get(
					ServerRpcController.getRpcChannel(controller)).getDevice();

			if (deviceAsync == null) {
				controller.setFailed("Error while adding a Plaintext-Listener");
				done.run(null);
				return;
			}

			final MessagePlainTextListener listener = new MessagePlainTextListener() {

				@Override
				public void onMessagePlainTextReceived(
						final MessageEvent<MessagePlainText> message) {

					final RemoteMessageListener remoteListener = new RemoteMessageListener(
							request.getOperationKey(), ServerRpcController
									.getRpcChannel(controller));
					remoteListener.onMessagePlainTextReceived(message);
				}
			};

			deviceAsync.addListener(listener);

			final HashMap<String, MessagePlainTextListener> temp = new HashMap<String, MessagePlainTextListener>();
			temp.put(request.getOperationKey(), listener);

			plainTextListenerList.put(ServerRpcController
					.getRpcChannel(controller), temp);

			done.run(EmptyAnswer.newBuilder().build());

		}

		/**
		 * remove a MessagePacketListener from a Device
		 * @param controller the RpcController for this operation
		 * @param request the OpKey-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void removeMessagePacketListener(final RpcController controller,
				final OpKey request, final RpcCallback<EmptyAnswer> done) {

			/* kontorllieren der Berechtigung */
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			/* finden des richtigen Device */
			final DeviceAsync deviceAsync = idList.get(
					ServerRpcController.getRpcChannel(controller)).getDevice();
			/* finden der richtigen ListenerList fuer den aktuellen Client */
			final HashMap<String, MessagePacketListener> temp = packetListenerList
					.get(ServerRpcController.getRpcChannel(controller));

			/* Fehlerfall, sollte im normalen Betrieb nicht vorkommen */
			if (deviceAsync == null
					|| (!packetListenerList.containsKey(ServerRpcController
							.getRpcChannel(controller)) && temp
							.containsKey(request.getOperationKey()))) {
				controller.setFailed("Error while removing a Packet-Listener");
				done.run(null);
				return;
			}

			/* entfernen des Listener vom physikalischen Device */
			deviceAsync.removeListener(temp.get(request.getOperationKey()));
			/* entfernen des Listener aus der ListenerList des Clients */
			temp.remove(request.getOperationKey());
			/* ueberschreiben der alten ListenerList */
			packetListenerList.put(ServerRpcController
					.getRpcChannel(controller), temp);

			done.run(EmptyAnswer.newBuilder().build());
		}

		/**
		 * remove a MessagePlainTextListener from a Device
		 * @param controller the RpcController for this operation
		 * @param request the OpKey-request from the Client
		 * @param done RpcCallback
		 */
		@Override
		public void removeMessagePlainTextListener(
				final RpcController controller, final OpKey request,
				final RpcCallback<EmptyAnswer> done) {

			// Kommentare analog zu removeMessagePacketListener
			final Subject user = authList.get(ServerRpcController
					.getRpcChannel(controller));
			if (user == null || !user.isAuthenticated()) {
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			final DeviceAsync deviceAsync = idList.get(
					ServerRpcController.getRpcChannel(controller)).getDevice();
			final HashMap<String, MessagePlainTextListener> a = plainTextListenerList
					.get(ServerRpcController.getRpcChannel(controller));

			if (deviceAsync == null
					|| (!plainTextListenerList.containsKey(ServerRpcController
							.getRpcChannel(controller)) && a
							.containsKey(request.getOperationKey()))) {
				controller
						.setFailed("Error while removing a Plaintext-Listener");
				done.run(null);
				return;
			}

			deviceAsync.removeListener(a.get(request.getOperationKey()));
			a.remove(request.getOperationKey());
			plainTextListenerList.put(ServerRpcController
					.getRpcChannel(controller), a);
			done.run(EmptyAnswer.newBuilder().build());
		}
	}

}
