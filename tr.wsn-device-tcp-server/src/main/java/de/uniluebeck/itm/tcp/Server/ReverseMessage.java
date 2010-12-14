package de.uniluebeck.itm.tcp.Server;

import com.google.protobuf.ByteString;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;


import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.EmptyAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.MacData;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.clientMessage;;

public class ReverseMessage {

	private RpcClientChannel channel = null;
	private PacketServiceAnswer.Interface answer;
	private String OperationKey;
	
	ReverseMessage(String OperationKey, RpcClientChannel channel){
		this.OperationKey = OperationKey;
		this.channel = channel;
		answer = PacketServiceAnswer.newStub(channel);
	}

	
	public void sendReverseMessage(String message){
		
		final RpcController controller = channel.newRpcController();
		clientMessage request = clientMessage.newBuilder().setOperationKey(OperationKey).setQuery(message).build();
		answer.sendReverseMessage(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
			}});
		
	}
	
	public void sendReverseMac(MacAddress result){
		
		final RpcController controller = channel.newRpcController();
		
		MacData request = MacData.newBuilder().setOperationKey(OperationKey).addMACADDRESS(ByteString.copyFrom(result.getMacBytes())).build();
		
		answer.reverseReadMac(controller, request, new RpcCallback<EmptyAnswer>(){

			@Override
			public void run(EmptyAnswer parameter) {
			}});
		
	}
}
