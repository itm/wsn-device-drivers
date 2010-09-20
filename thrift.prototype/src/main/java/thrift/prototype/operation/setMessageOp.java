package thrift.prototype.operation;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.transport.TTransportException;

import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleCancel_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGetState_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGet_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.setMessage_call;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class setMessageOp extends Operation<Void>{
	
	public setMessageOp(String id, String OperationHandleKey, String uri, int port,
			TAsyncClientManager acm) {
		super(id, OperationHandleKey, uri, port, acm);
	}

	public OperationHandle<Void> operate(String setMessage, final AsyncCallback<Void> callback) {
		
		try {
			// Entfernter Methodenaufruf
			stub.getClient().setMessage(id, OperationHandleKey, setMessage, new AsyncMethodCallback<AsyncDevice.AsyncClient.setMessage_call>() {
	            
				// Bei erfolgreicher Uebertragung
				@Override
				public void onComplete(setMessage_call response) {
					try {
						response.getResult();
						callback.onSuccess(null);
						OperationKeys.getInstance().removeKey(OperationHandleKey);
					} catch (TException e) {
						e.printStackTrace();
					}// benachrichtigen des synchro-objekts
					synchronized(o) {
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
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				try {
					stub.getClient().HandleCancel(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleCancel_call>(){

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
			public Void get() {
				
				try {
					stub.getClient().HandleGet(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleGet_call>(){

						@Override
						public void onComplete(HandleGet_call response) {
							try {
								response.getResult();
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
					stub.getClient().HandleGetState(id, OperationHandleKey, new AsyncMethodCallback<AsyncDevice.AsyncClient.HandleGetState_call>(){

						@Override
						public void onComplete(HandleGetState_call response) {
							try {
								String stateName = response.getResult();
								for(State zwState : State.values()){
									if(zwState.getName().equalsIgnoreCase(stateName)){
										state = zwState;
									}
								}
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
