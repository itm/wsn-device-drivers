package rpc_pro.rpcPrototype.Server;

import java.util.HashMap;
import java.util.List;

import rpc_pro.rpcPrototype.files.MessageServiceFiles.VOID;

import com.google.protobuf.RpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class ClientID {

	String Message = "init";
	private static HashMap<RpcController,OperationHandle<Void>> handleList = new HashMap<RpcController,OperationHandle<Void>>();
	
	public ClientID(){
		
	}
	
	public void setMessage(String message){
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		Message = message;
	}
	
	public String getMessage() {
		for(int i=0;i<1;i++){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return Message;
	}

	public void setHandleList(RpcController controller, OperationHandle<Void> handle) {
		handleList.put(controller, handle);
	}

	public OperationHandle<Void> getHandleList(RpcController controller) {
		return handleList.get(controller);
	}
	
	
	
	
	
//	
//	private static HashMap<RpcController, String> userList = null;
//	
//	private static ClientID instance = null;
//	
//	
//	private ClientID(){
//	}
//	
//	public static ClientID getClientID(){
//		if(instance == null){
//			instance = new ClientID();
//			userList = new HashMap<RpcController, String>();
//		}
//		return instance;
//	}
//	
//	
//	public void setController(RpcController controller, String username){
//		userList.put(controller, username);
//	}
//	
//	public boolean getAuthentifikation(RpcController controller){
//		
//		if(userList.containsKey(controller)){
//			return true;
//		}
//		else{
//			return false;
//		}
//	}
//	
//	
//	public String getUsername(RpcController controller){
//		
//		return userList.get(controller);
//	}
	
}
