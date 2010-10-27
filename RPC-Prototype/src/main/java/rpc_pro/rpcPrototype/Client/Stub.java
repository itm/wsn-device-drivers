package rpc_pro.rpcPrototype.Client;

import java.io.IOException;
import java.util.concurrent.Executors;


//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.log4j.BasicConfigurator;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import rpc_pro.rpcPrototype.files.PacketServiceImpl;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Identification;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.STRING;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Operations;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.PacketService;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.ProgramPacket;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.TestOperations;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Operations.BlockingInterface;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientBootstrap;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;

import de.uniluebeck.itm.devicedriver.DeviceBinData;
import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.DeviceAsync;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;


public class Stub implements DeviceAsync{

	//private static Log log = LogFactory.getLog(Client.class);
	
	PeerInfo server = null;
	PeerInfo client = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
	TestOperations.Interface testService = null;
	State state = null;
	
	Stub (String userName, String passWord, String uri, int port) throws Exception{
		this(userName,passWord,uri,port,1234);
	}

	Stub (String userName, String passWord, String uri, int port, int clientPort) throws Exception{
		
		// setzen der Server-Infos
		server = new PeerInfo(uri,port);
		// setzen der Client-Infos fuer Reverse RPC
		client = new PeerInfo(userName+"client",clientPort);
		
		// aufruf des initialen Connects
		connect(userName, passWord, new AsyncCallback<String>() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable throwable) {
				System.out.println(throwable.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				System.out.println(result);
			}

			@Override
			public void onProgressChange(float fraction) {
			}
		});
	}
	
	// initialer Connect
	private void connect(String userName, String passWord, final AsyncCallback<String> callback){
		
		// setzen des Thread-Pools
		executor = new ThreadPoolCallExecutor(3, 10);
		//setzen des bootstraps
		bootstrap = new DuplexTcpClientBootstrap(
                client, 
                new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()),
        executor);

		// registrieren der Reverse-RPC Services
		bootstrap.getRpcServiceRegistry().registerService(PacketService.newReflectiveService(new PacketServiceImpl()));
		
		// setzen der Verbindungs-Optionen, siehe Netty
		bootstrap.setOption("connectTimeoutMillis",10000);
		bootstrap.setOption("connectResponseTimeoutMillis",10000);
		bootstrap.setOption("receiveBufferSize", 1048576);
		bootstrap.setOption("tcpNoDelay", false);
		
		try {
			// herstellen der Verbindung zum Server
			channel = bootstrap.peerWith(server);
		} catch (IOException e) {
			callback.onFailure(e);
		}
		// erzeugen eines Controlles fuer diese Operation
		final RpcController controller = channel.newRpcController();
		
		// erzeugen eines async RPC-Objekts fuer die TestOperationen
		testService = TestOperations.newStub(channel);
		// erzeugen eines async RPC-Objekts fuer die Operationen
		operationService = Operations.newStub(channel);
		
		// aufbauen eines Identification-Packets
		Identification id = Identification.newBuilder().setUsername(userName).setPassword(passWord).build();
		//durchfuehren eines RPC-calls (das connect sollte vlt blockierend sein)
		operationService.connect(controller, id, new RpcCallback<VOID>(){

			// callback aufruf des Servers
			@Override
			public void run(VOID arg0) {
				if(!controller.failed()){
					callback.onSuccess("Die Authentifikation war erfolgreich");
				}
				else{
					callback.onFailure(new Throwable(controller.errorText()));
				}
			}});
	}

	// test Methode zum setzen einer Nachricht auf dem Server
	public OperationHandle<Void> setMessage(String setMessage, final AsyncCallback<Void> callback) {

		// erzeugen eines Controllers fuer diese Operation
		final RpcController controller = channel.newRpcController();
		// erzeugen einer Nachricht, der OperationKey wird aus der controllerID erzeugt
		STRING request = STRING.newBuilder().setQuery(setMessage).setOperationKey(controller.toString()).build();
		//ausfuehren des async RPCs
		testService.setMessage(controller, request, new RpcCallback<VOID>(){

			// callback aufruf des Servers
			@Override
			public void run(VOID arg0) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(null);
				}
			}});
		
		// Nach erfolgreicher Ausfuerung des Calls den OperationHandle loeschen?
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				// theoretisch soll das so gehen,
				// ist aber noch nicht getestet
				controller.startCancel();
				
				//alternativer async RPC aufruf zum abbrechen
//				operationService.cancelHandle(controller, VOID.newBuilder().build(), new RpcCallback<VOID>(){
//
//					@Override
//					public void run(VOID parameter) {
//						callback.onCancel();
//					}});
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, VOID.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  VOID.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}
	
	public OperationHandle<Void> getMessage(final AsyncCallback<String> callback) {
		
		final RpcController controller = channel.newRpcController();
		testService.getMessage(controller, VOID.newBuilder().setOperationKey(controller.toString()).build(), new RpcCallback<STRING>() {
			
			@Override
			public void run(STRING arg0) {	
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(arg0.getQuery());
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				// theoretisch soll das so gehen,
				// ist aber noch nicht getestet
				controller.startCancel();
				
				//alternativer async RPC aufruf zum abbrechen
//				operationService.cancelHandle(controller, VOID.newBuilder().build(), new RpcCallback<VOID>(){
//
//					@Override
//					public void run(VOID parameter) {
//						callback.onCancel();
//					}});
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, VOID.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  VOID.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}
	
	
	@Override
	public OperationHandle<Void> program(DeviceBinData binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		final RpcController controller = channel.newRpcController();
		
		// Hier muss noch die Umsetzung aus einem BinaerFile in ein BinaryPacket hin
		
		ProgramPacket packet = ProgramPacket.newBuilder().addAllBinaryPacket(null).setTimeout(timeout).setOperationKey(controller.toString()).build();
		
		operationService.program(controller, packet, new RpcCallback<VOID>() {
			@Override
			public void run(VOID parameter) {
				if(controller.failed()){
					callback.onFailure(new Throwable(controller.errorText()));
				}
				else{
					callback.onSuccess(null);
				}
			}
		});
		
		// Rueckgabe eines OperationHandle
		return new OperationHandle<Void>(){

			// aufruf bei cancel (User-seitig)
			@Override
			public void cancel() {
				// theoretisch soll das so gehen,
				// ist aber noch nicht getestet
				controller.startCancel();
				
				//alternativer async RPC aufruf zum abbrechen
//				operationService.cancelHandle(controller, VOID.newBuilder().build(), new RpcCallback<VOID>(){
//
//					@Override
//					public void run(VOID parameter) {
//						callback.onCancel();
//					}});
			}

			// aufruf bei get (User-seitig)
			@Override
			public Void get() {

				// erzeugen eines sync RPC-Objekts fuer die Operationen
		        BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
		        try {
		        	// sync RPC-Aufruf
					blockOperationService.getHandle(controller, VOID.newBuilder().setOperationKey(controller.toString()).build());
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				return null;
			}
			
			// aufruf bei getState (User-seitig)
			@Override
			public State getState() {
				
				// erzeugen eines State-Objekts
				State state = null;

				// Blockierender Aufruf fuer getState in Ordnung?
				
				// erzeugen eines sync RPC-Objekts fuer die Operationen
				BlockingInterface blockOperationService =  Operations.newBlockingStub(channel);
				try {
					// sync RPC-Aufruf
					STRING result = blockOperationService.getState(controller,  VOID.newBuilder().setOperationKey(controller.toString()).build());
					// erzeugen eines State aus dem result-String
					state = State.fromName(result.getQuery());
				} catch (ServiceException e) {
					e.printStackTrace();
				}

				//alternativer async RPC aufruf
//				operationService.getState(controller, VOID.newBuilder().setHandleKey(controller.toString()).build(), new RpcCallback<STRING>(){
//					@Override
//					public void run(STRING parameter) {
//						System.out.println(parameter.getQuery());
//						state = State.fromName(parameter.getQuery());
//					}});
				
				return state;
			}};
	}
	
	
	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			PacketType... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			int... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length,
			long timeout, AsyncCallback<byte[]> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout,
			AsyncCallback<MacAddress> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> reset(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data,
			int length, long timeout, AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}
}
	
