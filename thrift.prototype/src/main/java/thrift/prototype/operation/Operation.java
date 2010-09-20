package thrift.prototype.operation;

import org.apache.thrift.async.TAsyncClientManager;
import thrift.prototype.client.TCP_Stub;
import de.uniluebeck.itm.devicedriver.State;

public abstract class Operation<T> {

	String id;
	String OperationHandleKey;
	TCP_Stub stub;
	
	Object o = new Object();
	State state = null;
	
	public Operation(String id, String OperationHandleKey, String uri,int port,TAsyncClientManager acm){

		this.id = id;
		this.OperationHandleKey = OperationHandleKey;
		
		try {
			this.stub = new TCP_Stub(uri, port, acm, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	// TODO Absract Factory einbauen
	//public abstract OperationHandle<Void> operate(String setMessage, final AsyncCallback<T> callback);
	//public abstract OperationHandle<Void> operate(final AsyncCallback<T> callback);
	
}
