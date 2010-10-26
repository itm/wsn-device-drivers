package rpc_pro.rpcPrototype.Client;

import java.io.IOException;
import java.util.concurrent.Executors;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.log4j.BasicConfigurator;

import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import rpc_pro.rpcPrototype.files.PacketServiceImpl;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Identification;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Message;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.Operations;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.PacketService;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.ProgramPacket;
import rpc_pro.rpcPrototype.files.MessageServiceFiles.TestOperations;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
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
	
	PeerInfo client = null;
	PeerInfo server = null;
	ThreadPoolCallExecutor executor = null;
	DuplexTcpClientBootstrap bootstrap = null;
	RpcClientChannel channel = null;
	Operations.Interface operationService = null;
	TestOperations.Interface testService = null;
	
	Stub (String userName, String passWord, String uri, int port) throws Exception{
		
		server = new PeerInfo(uri,port);
		client = new PeerInfo(userName+"client",1234);
		
		connect(userName, passWord, new AsyncCallback<String>() {

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
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
	
	
	private void connect(String userName, String passWord, final AsyncCallback<String> callback){
		executor = new ThreadPoolCallExecutor(3, 10);
		bootstrap = new DuplexTcpClientBootstrap(
                client, 
                new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()),
        executor);

		bootstrap.getRpcServiceRegistry().registerService(PacketService.newReflectiveService(new PacketServiceImpl()));
		
		bootstrap.setOption("connectTimeoutMillis",10000);
		bootstrap.setOption("connectResponseTimeoutMillis",10000);
		bootstrap.setOption("receiveBufferSize", 1048576);
		bootstrap.setOption("tcpNoDelay", false);
		
		try {
			channel = bootstrap.peerWith(server);
		} catch (IOException e) {
			callback.onFailure(e);
		}
		RpcController controller = channel.newRpcController();
		
		testService = TestOperations.newStub(channel);
		
		operationService = Operations.newStub(channel);
		Identification id = Identification.newBuilder().setUsername(userName).setPassword(passWord).build();
		operationService.connect(controller, id, new RpcCallback<Message>(){

			@Override
			public void run(Message arg0) {
				callback.onSuccess(arg0.getQuery());
			}});
		
	}

	public OperationHandle<Void> setMessage(String setMessage, final AsyncCallback<Void> callback) {

		// ausfuehren des Thrift-Befehls und Rueckgabe des OperationHandle
		//return new setMessageOp(userID, OperationHandleKey, uri, port, acm).operate(setMessage, callback);
		
		final RpcController controller = channel.newRpcController();
		Message request = Message.newBuilder().setQuery(setMessage).build();
		//testService.setMessage(controller, request, null);
		
		testService.setMessage(controller, request, new RpcCallback<rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID>(){

			@Override
			public void run(
					rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID arg0) {
				callback.onSuccess(null);
			}});
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				controller.startCancel();
				callback.onCancel();
			}

			@Override
			public Void get() {
				
				try {
					controller.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return null;
			}

			@Override
			public State getState() {
				
				return null;
			}};
		
	}
	
	public OperationHandle<Void> getMessage(final AsyncCallback<String> callback) {
		
		RpcController controller = channel.newRpcController();
		testService.getMessage(controller, rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID.newBuilder().build(), new RpcCallback<Message>() {
			
			@Override
			public void run(Message arg0) {
				callback.onSuccess(arg0.getQuery());
			}
		});
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				
			}

			@Override
			public Void get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				// TODO Auto-generated method stub
				return null;
			}};
		
	}
	
	
	@Override
	public OperationHandle<Void> program(DeviceBinData binaryImage,
			long timeout, AsyncCallback<Void> callback) {
		
		RpcController controller = channel.newRpcController();
		//ProgramPacket packet = ProgramPacket.newBuilder().setBinaryPacket(null).setTimeout(timeout).build();
		ProgramPacket packet = ProgramPacket.newBuilder().addAllBinaryPacket(null).setTimeout(timeout).build();
		
		
		operationService.program(controller, packet, null);
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				
			}

			@Override
			public Void get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				// TODO Auto-generated method stub
				return null;
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
	
