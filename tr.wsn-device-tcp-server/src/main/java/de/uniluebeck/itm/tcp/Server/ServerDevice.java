package de.uniluebeck.itm.tcp.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.uniluebeck.itm.devicedriver.ConnectionListener;
import de.uniluebeck.itm.devicedriver.Device;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;
import de.uniluebeck.itm.devicedriver.async.OperationQueue;
import de.uniluebeck.itm.devicedriver.async.OperationQueueListener;
import de.uniluebeck.itm.devicedriver.async.QueuedDeviceAsync;
//import de.uniluebeck.itm.devicedriver.jennic.JennicDevice;
import de.uniluebeck.itm.devicedriver.operation.Operation;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection;
import de.uniluebeck.itm.devicedriver.serialport.SerialPortConnection.SerialPortMode;

public class ServerDevice {

	ServerDevice(){
		
	}
	
//	private Device createDevice(){
//
//		return new JennicDevice(new SerialPortConnection(){
//	
//			@Override
//			public void flush() {
//				// TODO Auto-generated method stub
//				
//			}
//	
//			@Override
//			public SerialPort getSerialPort() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//	
//			@Override
//			public void setSerialPortMode(SerialPortMode mode) {
//				// TODO Auto-generated method stub
//				
//			}
//	
//			@Override
//			public void addListener(ConnectionListener listener) {
//				// TODO Auto-generated method stub
//				
//			}
//	
//			@Override
//			public void connect(String uri) {
//				// TODO Auto-generated method stub
//				
//			}
//	
//			@Override
//			public InputStream getInputStream() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//	
//			@Override
//			public OutputStream getOutputStream() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//	
//			@Override
//			public boolean isConnected() {
//				// TODO Auto-generated method stub
//				return false;
//			}
//	
//			@Override
//			public void removeListener(ConnectionListener listener) {
//				// TODO Auto-generated method stub
//				
//			}
//	
//			@Override
//			public void shutdown(boolean force) {
//				// TODO Auto-generated method stub
//				
//			}});
//	}
//	
//	private QueuedDeviceAsync createQueuedDevice(Device device){
//		
//		return new QueuedDeviceAsync(new OperationQueue(){
//
//			@Override
//			public void addListener(OperationQueueListener listener) {
//				// TODO Auto-generated method stub
//				
//				
//			}
//
//			@Override
//			public <T> OperationHandle<T> addOperation(
//					Operation<T> operation, long timeout,
//					AsyncCallback<T> callback) {
//					
//				return new OperationHandle<T>(){
//
//					@Override
//					public void cancel() {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public T get() {
//						
//						return null;
//					}
//
//					@Override
//					public State getState() {
//						// TODO Auto-generated method stub
//						return null;
//					}};
//			}
//
//			@Override
//			public List<Operation<?>> getOperations() {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public void removeListener(OperationQueueListener listener) {
//				// TODO Auto-generated method stub
//				
//			}},device);
//	}
}
