package de.uniluebeck.itm.tcp.Server;


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

import de.uniluebeck.itm.Impl.Main;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.devicedriver.MessagePlainTextListener;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.event.MessageEvent;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ChipType;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ByteData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.FlashData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Identification;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.OpKey;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Operations;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketTypeData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.ProgramPacket;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.STRING;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketService;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.Timeout;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.sendData;
import de.uniluebeck.itm.tr.util.TimedCache;

public class Server {

	private static Logger log = LoggerFactory.getLogger(Server.class);
	
	// werden nach 30 min alle eintraege des Cache geloescht?
	// wie Timeout fuer einen Eintrag neu starten?
	private static TimedCache<RpcClientChannel,ClientID> idList = new TimedCache<RpcClientChannel,ClientID>(30,TimeUnit.MINUTES);
	private static TimedCache<RpcClientChannel,Subject> authList = new TimedCache<RpcClientChannel,Subject>(30,TimeUnit.MINUTES);
	//private static HashMap<RpcClientChannel,Subject> authList = new HashMap<RpcClientChannel,Subject>();
	private static HashMap <String,MessagePacketListener> packetListenerList = new HashMap<String,MessagePacketListener>();
	private static HashMap <String,MessagePlainTextListener> plainTextListenerList = new HashMap<String,MessagePlainTextListener>();
	
	private String host;
	private int port;
	private static ServerDevice serverDevices;
	
	Server(String host, int port){
		this.host = host;
		this.port = port;
		serverDevices = new ServerDevice();
	}
	
	public void start (){
		
		serverDevices.createServerDevices();
		
		// setzen der server-Informationen
		PeerInfo serverInfo = new PeerInfo(host, port);
		
		// setzen des ThreadPools
		 RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10, 10);
	        
		 // setzen des bootstraps
		 DuplexTcpServerBootstrap bootstrap = new DuplexTcpServerBootstrap(
				 serverInfo,
				 new NioServerSocketChannelFactory(
						 Executors.newCachedThreadPool(),
						 Executors.newCachedThreadPool()),
				 executor);
	        
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
					log.info("connectionLost " + clientChannel);
				}
				
				@Override
				public void connectionChanged(RpcClientChannel clientChannel) {
					log.info("connectionChanged " + clientChannel);
				}
			};
			rpcEventNotifier.setEventListener(listener);
	    	bootstrap.registerConnectionEventListener(rpcEventNotifier);
	        
	        
	    	// registrieren der benutzten Services
	    	bootstrap.getRpcServiceRegistry().registerService(Operations.newReflectiveService(new OperationsImpl()));
	    	bootstrap.getRpcServiceRegistry().registerService(PacketService.newReflectiveService(new PacketServiceImpl()));

	    	// starten des Servers
	    	bootstrap.bind();
	    	
	    	// ein wenig Kommunikation
	    	log.info("Serving " + bootstrap);
	    	
	    	/* Initialiesieren von Shiro */
	    	
	    	Factory<SecurityManager> factory = new IniSecurityManagerFactory("src/main/resources/shiro.ini");
	        SecurityManager securityManager = factory.getInstance();
	        SecurityUtils.setSecurityManager(securityManager);	        
			
	}
	
	// eigentliche Operationen, die spaeter verwendet werden sollen
	static class OperationsImpl implements Operations.Interface {
		
		// Methode zum verbinden auf den Server
		// hier sollte die Authentifikation stattfinden
		@Override
		public void connect(RpcController controller, Identification request,
				RpcCallback<EmptyAnswer> done) {
						
			// eine Moeglichkeit den benutzten channel zu identifizieren
			RpcClientChannel channel = ServerRpcController.getRpcChannel(controller);
			
			// erzeugen einer channel bezogenen User Instanz
			ClientID id = new ClientID(serverDevices.getDeviceList().get(request.getDeviceID()));
			
			// Abgleich der Userdaten
			
			/*Shiro:*/
			Subject currentUser = SecurityUtils.getSubject();
			
	        if (!currentUser.isAuthenticated()) {
	            UsernamePasswordToken token = new UsernamePasswordToken(request.getUsername(), request.getPassword());
	            token.setRememberMe(true);
	            try {
	            	
	                currentUser.login(token);
	                // eintragen der ClientID-Instanz zusammen mit den benutzten Channel in eine Liste
					idList.put(channel, id);
					authList.put(channel, currentUser);
			        // ausfuehren des Callback
			        done.run(EmptyAnswer.newBuilder().build());
					
	            } catch (UnknownAccountException uae) {
	            	controller.setFailed("There is no user with username of " + token.getPrincipal());
	            	done.run(null);
	            	return;
	            } catch (IncorrectCredentialsException ice) {
	            	controller.setFailed("Password for account " + token.getPrincipal() + " was incorrect!");
	            	done.run(null);
	            	return;
	            } catch (LockedAccountException lae) {
	            	controller.setFailed("The account for username " + token.getPrincipal() + " is locked.  " +
	                        "Please contact your administrator to unlock it.");
	            	done.run(null);
	            	return;
	            } catch (AuthenticationException ae) {
	            	controller.setFailed(ae.getMessage());
	            	done.run(null);
	            	return;
	            }
	        }
			/*Shiro END*/

		}

		// Methode um Device zu Programmieren
		@Override
		public void program(RpcController controller, ProgramPacket request,
				RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			
			if (!user.isPermitted("write:program")) {
				controller.setFailed("Sie haben nicht die noetigen Rechte!");
				done.run(null);
				return;
			}
			
			// identifizieren des Users mit dem Channel
			final ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			// erstellen einer Klasse zum Testen der OperationHandle
			DeviceAsync deviceAsync = id.getDevice();

			// erzeugen eines OperationHandle zur der Operation
			OperationHandle <Void> handle = deviceAsync.program(request.getBinaryPacketList().get(0).toByteArray(), request.getTimeout(), new AsyncCallback<Void>(){

				@Override
				public void onCancel() {
					System.out.println("Abbruch im TCP-Server");
				}

				@Override
				public void onFailure(Throwable throwable) {
					System.out.println("Fehler im TCP-Server");
				}

				@Override
				public void onProgressChange(float fraction) {
					System.out.println("change im TCP-Server");
				}

				@Override
				public void onSuccess(Void result) {
					System.out.println("jup es geht im TCP-Server");
				}

				@Override
				public void onExecute() {
					// TODO Auto-generated method stub
					
				}});
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			id.setHandleVoidList(request.getOperationKey(), handle);
			
			// ausfuehren des Callbacks
			done.run(EmptyAnswer.newBuilder().build());
		}

		// reagieren auf ein getState-Aufruf
		@Override
		public void getState(RpcController controller, OpKey request,
				RpcCallback<STRING> done) {
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			OperationHandle<Void> handle = id.getHandleVoidList(request.getOperationKey());			
			done.run(STRING.newBuilder().setQuery(handle.getState().getName()).build());
		}

		// reagieren auf ein cancel-Aufruf
		@Override
		public void cancelHandle(RpcController controller, OpKey request,
				RpcCallback<EmptyAnswer> done) {
	
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			OperationHandle<Void> handle = id.getHandleVoidList(request.getOperationKey());
			System.out.println("canceled: "+controller.isCanceled());
			if(controller.isCanceled()){
				handle.cancel();
				done.run(EmptyAnswer.newBuilder().build());
			}
		}

		// reagieren auf ein get-Aufruf
		@Override
		public void getHandle(RpcController controller, OpKey request,
				RpcCallback<EmptyAnswer> done) {
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			OperationHandle<Void> handle = id.getHandleVoidList(request.getOperationKey());
			handle.get();
			done.run(EmptyAnswer.newBuilder().build());
		}

		@Override
		public void writeMac(RpcController controller, MacData request,
				RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			
			if (!user.isPermitted("write:mac")) {
				controller.setFailed("Sie haben nicht die noetigen Rechte!");
				done.run(null);
				return;
			}
			
			// erstellen einer Klasse zum Testen der OperationHandle
			Main test = new Main();
			
			// identifizieren des Users mit dem Channel
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			// erzeugen eines OperationHandle zur der Operation
			OperationHandle <Void> handle = test.writeMac(new MacAddress(request.toByteArray()), request.getTimeout(), new AsyncCallback<Void>(){

				@Override
				public void onCancel() {
					System.out.println("Abbruch im TCP-Server");
				}

				@Override
				public void onFailure(Throwable throwable) {
					System.out.println("Fehler im TCP-Server");
				}

				@Override
				public void onProgressChange(float fraction) {
					System.out.println("change im TCP-Server");
				}

				@Override
				public void onSuccess(Void result) {
					System.out.println("jup es geht im TCP-Server");
				}

				@Override
				public void onExecute() {
					// TODO Auto-generated method stub
					
				}});
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			id.setHandleVoidList(request.getOperationKey(), handle);
			
			// ausfuehren des Callbacks
			done.run(EmptyAnswer.newBuilder().build());
			
		}

		@Override
		public void writeFlash(RpcController controller, FlashData request,
				RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}			

			if (!user.isPermitted("write:flash")) {
				controller.setFailed("Sie haben nicht die noetigen Rechte!");
				done.run(null);
				return;
			}
			
			// erstellen einer Klasse zum Testen der OperationHandle
			Main test = new Main();
			
			// identifizieren des Users mit dem Channel
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			// erzeugen eines OperationHandle zur der Operation
			OperationHandle <Void> handle = test.writeFlash(request.getAddress(),request.toByteArray(),request.getLength(),request.getTimeout(),new AsyncCallback<Void>(){

				@Override
				public void onCancel() {
					System.out.println("Abbruch im TCP-Server");
				}

				@Override
				public void onFailure(Throwable throwable) {
					System.out.println("Fehler im TCP-Server");
				}

				@Override
				public void onProgressChange(float fraction) {
					System.out.println("change im TCP-Server");
				}

				@Override
				public void onSuccess(Void result) {
					System.out.println("jup es geht im TCP-Server");
				}

				@Override
				public void onExecute() {
					// TODO Auto-generated method stub
					
				}});
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			id.setHandleVoidList(request.getOperationKey(), handle);
			
			// ausfuehren des Callbacks
			done.run(EmptyAnswer.newBuilder().build());
			
		}

		@Override
		public void eraseFlash(RpcController controller, Timeout request,
				RpcCallback<EmptyAnswer> done) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void readFlash(RpcController controller, FlashData request,
				RpcCallback<ByteData> done) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void readMac(final RpcController controller, final Timeout request,
				final RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			
			if (!user.isPermitted("write:program")) {
				controller.setFailed("Sie haben nicht die noetigen Rechte!");
				done.run(null);
				return;
			}
			
			// identifizieren des Users mit dem Channel
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			// erstellen einer Klasse zum Testen der OperationHandle
			DeviceAsync deviceAsync = id.getDevice();

			// erzeugen eines OperationHandle zur der Operation
			OperationHandle <MacAddress> handle = deviceAsync.readMac(request.getTimeout(), new AsyncCallback<MacAddress>(){

				@Override
				public void onCancel() {
					controller.setFailed("readMac wurde vom Device abgebrochen");
					done.run(null);
				}

				@Override
				public void onFailure(Throwable throwable) {
					controller.setFailed(throwable.getMessage());
					done.run(null);
				}

				@Override
				public void onProgressChange(float fraction) {
					ReverseMessage message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
					message.sendReverseMessage(String.valueOf(fraction));
				}

				@Override
				public void onSuccess(MacAddress result) {
					// ausfuehren des Callbacks
					ReverseMessage message = new ReverseMessage(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
					message.sendReverseMac(result);
				}

				//TODO wozu onExecute und wo wird es abgefangen
				@Override
				public void onExecute() {
					
				}});
			
			// ein channel-einzigartiger OperationKey wird vom Client zu jeder Operation mitgeschickt
			id.setHandleMacList(request.getOperationKey(), handle);
			
			done.run(EmptyAnswer.newBuilder().build());
			
		}

		@Override
		public void reset(RpcController controller, Timeout request,
				RpcCallback<EmptyAnswer> done) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void send(RpcController controller, sendData request,
				RpcCallback<EmptyAnswer> done) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getChipType(RpcController controller, Timeout request,
				RpcCallback<ChipType> done) {
			// TODO Auto-generated method stub
			
		}
	}
	

	static class PacketServiceImpl implements PacketService.Interface {

		@Override
		public void addMessagePacketListener(final RpcController controller,
				final PacketTypeData request, RpcCallback<EmptyAnswer> done) {

			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			
			int[] types = new int[request.getTypeCount()];
			for (int i=0;i<request.getTypeCount();i++){
				types[i] = request.getType(i);
			}
	
			MessagePacketListener listener = new MessagePacketListener() {
				
				@Override
				public void onMessagePacketReceived(MessageEvent<MessagePacket> event) {
					RemoteMessagePacketListener remoteListener = new RemoteMessagePacketListener(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
					remoteListener.onMessagePacketReceived(event);
				}
			};
			
			// erstellen einer Klasse zum Testen der OperationHandle
			DeviceAsync deviceAsync = idList.get(ServerRpcController.getRpcChannel(controller)).getDevice();
			deviceAsync.addListener(listener, types);
			
			packetListenerList.put(request.getOperationKey(), listener);
			
			done.run(EmptyAnswer.newBuilder().build());
			
		}

		@Override
		public void addMessagePlainTextListener(final RpcController controller,
				final PacketTypeData request, RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			

			MessagePlainTextListener listener = new MessagePlainTextListener() {

				@Override
				public void onMessagePlainTextReceived(
						MessageEvent<MessagePlainText> message) {
					
					RemoteMessagePlainTextListener remoteListener = new RemoteMessagePlainTextListener(request.getOperationKey(),ServerRpcController.getRpcChannel(controller));
					remoteListener.onMessagePlainTextReceived(message);
				}
			};
			
			// erstellen einer Klasse zum Testen der OperationHandle
			DeviceAsync deviceAsync = idList.get(ServerRpcController.getRpcChannel(controller)).getDevice();
			deviceAsync.addListener(listener);
			
			plainTextListenerList.put(request.getOperationKey(), listener);
			
			done.run(EmptyAnswer.newBuilder().build());
			
			
		}

		@Override
		public void removeMessagePacketListener(RpcController controller,
				OpKey request, RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}
			
			DeviceAsync deviceAsync = idList.get(ServerRpcController.getRpcChannel(controller)).getDevice();

			deviceAsync.removeListener(packetListenerList.get(request.getOperationKey()));
			packetListenerList.remove(request.getOperationKey());
			done.run(EmptyAnswer.newBuilder().build());
			
		}
		
		@Override
		public void removeMessagePlainTextListener(RpcController controller,
				OpKey request, RpcCallback<EmptyAnswer> done) {
			
			Subject user = authList.get(ServerRpcController.getRpcChannel(controller));
			if(user==null || !user.isAuthenticated()){
				controller.setFailed("Sie sind nicht authentifiziert!");
				done.run(null);
				return;
			}

			DeviceAsync deviceAsync = idList.get(ServerRpcController.getRpcChannel(controller)).getDevice();
			deviceAsync.removeListener(plainTextListenerList.get(request.getOperationKey()));
			plainTextListenerList.remove(request.getOperationKey());
			done.run(EmptyAnswer.newBuilder().build());
			
		}
	}

}
