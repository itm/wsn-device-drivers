package thrift.prototype.operation;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;

import thrift.prototype.client.OperationKeys;
import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleCancel_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGetState_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.HandleGet_call;
import thrift.prototype.files.AsyncDevice.AsyncClient.program_call;

import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

public class programm extends Operation<Void> {

	public programm(String id, String OperationHandleKey, String uri, int port,
			TAsyncClientManager acm) {
		super(id, OperationHandleKey, uri, port, acm);
	}


	public OperationHandle<Void> operate(DeviceBinFile binaryImage,
			long timeout, final AsyncCallback<Void> callback) {
		
		/*
		binaryImage.resetBlockIterator();
		
		List<ByteBuffer> blocks = new ArrayList<ByteBuffer>();
		String description = "";
		
		/* aufbereiten der DeviceBinFile fuer den Transport *//*
		while(binaryImage.hasNextBlock()){
			BinFileDataBlock block = binaryImage.getNextBlock();
			blocks.add(ByteBuffer.wrap(block.data));
		}*/
		
		List<ByteBuffer> blocks = null;
		String description = "";
		
		try {
			stub.getClient().program(id, OperationHandleKey, blocks, description, timeout, new AsyncMethodCallback<AsyncDevice.AsyncClient.program_call>() {

				@Override
				public void onComplete(program_call response) {
					try {
						response.getResult();
						OperationKeys.getInstance().removeKey(OperationHandleKey);
						callback.onSuccess(null);
					} catch (TException e) {
						e.printStackTrace();
					}
					synchronized(o) {
						o.notifyAll();
			        }
				}
				@Override
				public void onError(Throwable throwable) {
					OperationKeys.getInstance().removeKey(OperationHandleKey);
					callback.onFailure(throwable);
				}
			});
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
								String StateName = response.getResult();
								// TODO aus StateName wieder ein State machen
								//state = new State(StateName);
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
