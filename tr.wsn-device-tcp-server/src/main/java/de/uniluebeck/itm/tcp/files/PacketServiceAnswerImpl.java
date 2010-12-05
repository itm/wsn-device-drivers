package de.uniluebeck.itm.tcp.files;

import java.util.HashMap;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.MessagePlainText;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.PacketServiceAnswer;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.VOID;
import de.uniluebeck.itm.tcp.files.MessageServiceFiles.sendData;


// Implementierung der Methoden fuer das ReverseRPC
public class PacketServiceAnswerImpl implements PacketServiceAnswer.Interface{

	private HashMap<String,MessagePacketListener> listenerList = new HashMap<String,MessagePacketListener>();
	
	public PacketServiceAnswerImpl() {
	}

	@Override
	public void sendReverseMessage(RpcController controller, sendData request,
			RpcCallback<VOID> done) {

		if(request.hasType()){
			MessagePacket message = new MessagePacket(request.getType(),request.toByteArray());
			listenerList.get(request.getOperationKey()).onMessagePacketReceived(message);
		}
		else {
			MessagePlainText message = new MessagePlainText(request.toByteArray());
			listenerList.get(request.getOperationKey()).onMessagePlainTextReceived(message);
		}
		
		done.run(VOID.newBuilder().build());
		
	}
	
	public void setListener(String key, MessagePacketListener listener){
		listenerList.put(key, listener);
	}
	
	public void removeListener(String key){
		listenerList.remove(key);
	}
		
}
