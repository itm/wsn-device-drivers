package rpc_pro.rpcPrototype.Server;

import java.util.HashMap;
import java.util.concurrent.Executors;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.log4j.BasicConfigurator;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import rpc_pro.rpcPrototype.files.MessageServiceFiles.Identification;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Message;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Operations;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.ProgramPacket;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.TestOperations;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID;

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
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class Server {

	//private static Log log = LogFactory.getLog(Server.class);
	private static HashMap<RpcClientChannel,ClientID> idList = new HashMap<RpcClientChannel,ClientID>();
	
	
	public static void main (String[] args){
		
		//BasicConfigurator.configure();
		
		PeerInfo serverInfo = new PeerInfo("localhost", 8080);
		
		 RpcServerCallExecutor executor = new ThreadPoolCallExecutor(10, 10);
	        
	        DuplexTcpServerBootstrap bootstrap = new DuplexTcpServerBootstrap(
	                        serverInfo,
	                new NioServerSocketChannelFactory(
	                        Executors.newCachedThreadPool(),
	                        Executors.newCachedThreadPool()),
	                executor);
	        
	        RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
	        RpcConnectionEventListener listener = new RpcConnectionEventListener() {
				
				@Override
				public void connectionReestablished(RpcClientChannel clientChannel) {
					System.out.println("connectionReestablished " + clientChannel);
				}
				
				@Override
				public void connectionOpened(RpcClientChannel clientChannel) {
					System.out.println("connectionOpened " + clientChannel);	
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
	        
	        
	    	bootstrap.getRpcServiceRegistry().registerService(TestOperations.newReflectiveService(new TestOperationsImpl()));
	    	bootstrap.getRpcServiceRegistry().registerService(Operations.newReflectiveService(new OperationsImpl()));

	    	bootstrap.bind();
	    	System.out.println("Serving " + bootstrap);
			
	}
	
	static class TestOperationsImpl implements TestOperations.Interface {

		@Override
		public void setMessage(RpcController controller, Message request,
				RpcCallback<VOID> done) {
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			OperationHandle<Void> handle = null;
			
			id.setHandleList(controller, handle);
			id.setMessage(request.getQuery());
			
			done.run(VOID.newBuilder().build());
			
			//System.out.println("Die Nachricht: "+request.getQuery()+" ist auf dem Server angekommen.");
		}

		@Override
		public void getMessage(RpcController controller, VOID request,
				RpcCallback<Message> done) {
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			done.run(Message.newBuilder().setQuery(id.getMessage()).build());
			
			
		}
	}
	
	static class OperationsImpl implements Operations.Interface {
		@Override
		public void connect(RpcController controller, Identification request,
				RpcCallback<Message> done) {
			
			RpcClientChannel channel = ServerRpcController.getRpcChannel(controller);
			
			ClientID id = new ClientID();
			
			if(request.getPassword().equals("testPassword") && (request.getUsername().equalsIgnoreCase("testUser") || request.getUsername().equalsIgnoreCase("testUser2") )){
				idList.put(channel, id);
				done.run(Message.newBuilder().setQuery("Die Authentifikation war erfolgreich!").build());
			}
			else{
				done.run(Message.newBuilder().setQuery("Die Authentifikation ist fehlgeschalgen!").build());
			}
		}

		@Override
		public void program(RpcController controller, ProgramPacket request,
				RpcCallback<VOID> done) {
			
			Main test = new Main();
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			
			OperationHandle <Void> handle = test.program(null, request.getTimeout(), new AsyncCallback<Void>(){

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
				}});
			
			id.setHandleList(controller, handle);
			
			done.run(VOID.newBuilder().build());
		}

		@Override
		public void getState(RpcController controller, VOID request,
				RpcCallback<Message> done) {
			
			ClientID id = idList.get(ServerRpcController.getRpcChannel(controller));
			OperationHandle<Void> handle = id.getHandleList(controller);
			
			done.run(Message.newBuilder().setQuery(handle.getState().getName()).build());
			
		}

//		@Override
//		public void setMessage(RpcController controller, Message request,
//				RpcCallback<Message> done) {
//			
//			ClientID id = ClientID.getClientID();
//			
//			id.setController(request.getQuery(), controller);
//			
//			new testServer().test();
//			
//		}
//
//		@Override
//		public void sendMessage(RpcController controller, Message request,
//				RpcCallback<Message> done) {
//			
//			RpcClientChannel channel = ServerRpcController.getRpcChannel(controller);
//			Interface clientService = HalloService.newStub(channel);
//			RpcController clientController = channel.newRpcController();
//			
//			clientService.sayHello(clientController, request, null);
//			
//		}
		
		
//		@Override
//		public void setMessage(RpcController controller, Message request,
//				RpcCallback<Message> done) {
//			
//			System.out.println("setMessage gestartet");
//			
//			RpcClientChannel channel = ServerRpcController.getRpcChannel(controller);
//			Interface clientService = HalloService.newStub(channel);
//			RpcController clientController = channel.newRpcController();
//			
//			
//			Message clientRequest = Message.newBuilder().setQuery("Hello World").build();
//			clientService.sayHello(clientController, clientRequest, null);
//			
//		}

	}
}
