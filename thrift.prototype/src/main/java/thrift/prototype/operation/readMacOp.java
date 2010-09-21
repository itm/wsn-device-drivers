package thrift.prototype.operation;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.transport.TTransportException;

import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleCancel_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGetState_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGetReadMac_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.readMac_call;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class readMacOp extends Operation<MacAddress> {

	MacAddress macaddress;
	
	public readMacOp(String id, String OperationHandleKey, String uri, int port,
			TAsyncClientManager acm) {
		super(id, OperationHandleKey, uri, port, acm);
	}

	public OperationHandle<MacAddress> operate(long timeout, final AsyncCallback<MacAddress> callback) {
			
		try {
				// Entfernter Methodenaufruf
				client.readMac(id, OperationHandleKey, timeout, new AsyncMethodCallback<AsyncDevice.AsyncClient.readMac_call>() {
		            
					// Bei erfolgreicher Uebertragung
					@Override
					public void onComplete(readMac_call response) {
						try {
							macaddress = new MacAddress(response.getResult().array());
							callback.onSuccess(macaddress);
						} catch (TException e) {
							e.printStackTrace();
						}// benachrichtigen des synchro-objekts
						synchronized(o) {
							OperationKeys.getInstance().removeKey(OperationHandleKey);
							o.notifyAll();
				        }
		            }

					// Bei fehlerhafter Uebertragung
					@Override
					public void onError(Throwable throwable) {
						synchronized(o) {
							o.notifyAll();
							callback.onFailure(throwable);
							OperationKeys.getInstance().removeKey(OperationHandleKey);
						}
					}
				});
			} catch (TTransportException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new OperationHandle<MacAddress>(){

				@Override
				public void cancel() {
					try {
						client.HandleCancel(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleCancel_call>(){

							@Override
							public void onComplete(HandleCancel_call response) {
							}

							@Override
							public void onError(Throwable throwable) {
							}
							
						});
					} catch (TException e) {
						e.printStackTrace();
					}
					
					OperationKeys.getInstance().removeKey(OperationHandleKey);
				}
				@Override
				public MacAddress get() {
					
					try {
						client.HandleGetReadMac(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleGetReadMac_call>(){

							@Override
							public void onComplete(HandleGetReadMac_call response) {
								try {
									response.getResult();
									// einmalige Antwort oder wiederholender Aufruf?
								} catch (TException e) {
									e.printStackTrace();
								}
								synchronized(o) {
									o.notifyAll();
						        }
								
							}

							@Override
							public void onError(Throwable throwable) {
								synchronized(o) {
									o.notifyAll();
						        }
								
							}
							
						});
						synchronized(o) {
					        o.wait();
					        OperationKeys.getInstance().removeKey(OperationHandleKey);
					      }	
					} catch (TException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    
				return null;
				}
				@Override
				public State getState() {
					
					try {
						client.HandleGetState(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleGetState_call>(){

							@Override
							public void onComplete(HandleGetState_call response) {
								try {
									String stateName = response.getResult();
									state = State.fromName(stateName);
								} catch (TException e) {
									e.printStackTrace();
								}
							}

							@Override
							public void onError(Throwable throwable) {
							}});
					} catch (TException e) {
						e.printStackTrace();
					}
					
					return state;
				}};
		}
}

