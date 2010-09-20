package thrift.prototype.operation;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;

import thrift.prototype.client.ConnectionBuilder;
import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.LoginFailed;
import thrift.prototype.files.AsyncDevice.AsyncClient.connect_call;

public class Connect {

	AsyncDevice.AsyncClient client;
	String userName;
	String passWord;
	
	Object o = new Object();
	
	public Connect(String userName, String passWord, String uri, int port){
		
		this.userName = userName;
		this.passWord = passWord;
		try {
			this.client = new ConnectionBuilder(uri,port).getClient();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void operate(final AsyncCallback<String> callback){
		
		try {
			// erstellt ein ClientID-Objekt mit id als Key, mit Hilfe dieses
			client.connect(userName, passWord, new AsyncMethodCallback<AsyncDevice.AsyncClient.connect_call>(){
				@Override
				public void onComplete(connect_call response) {
					try {
						callback.onSuccess(response.getResult());
					} catch (TException e) {
						System.out.println("Point 1");
						callback.onFailure(e);
					} catch (LoginFailed lf) {
						callback.onFailure(lf);
					}synchronized(o) {
						o.notifyAll();
			        }
				}
				@Override
				public void onError(Throwable throwable) {
					callback.onFailure(throwable);
				}			
			});
			synchronized(o) {
		        o.wait(10000);
		      }	
		} catch (TException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
