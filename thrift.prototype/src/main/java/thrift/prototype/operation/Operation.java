package thrift.prototype.operation;

import org.apache.thrift.async.TAsyncClientManager;

import de.uniluebeck.itm.devicedriver.State;

import thrift.prototype.client.ConnectionBuilder;
import thrift.prototype.files.AsyncDevice;

public abstract class Operation<T> {

	String id;
	String OperationHandleKey;
	AsyncDevice.AsyncClient client;
	
	State state;
	Object o = new Object();
	
	public Operation(String id, String OperationHandleKey, String uri,int port,TAsyncClientManager acm){

		this.id = id;
		this.OperationHandleKey = OperationHandleKey;
		
		try {
			this.client = new ConnectionBuilder(uri, port, acm).getClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// TODO Absract Factory einbauen
	//public abstract OperationHandle<Void> operate(String setMessage, final AsyncCallback<T> callback);
	//public abstract OperationHandle<Void> operate(final AsyncCallback<T> callback);
	
}
