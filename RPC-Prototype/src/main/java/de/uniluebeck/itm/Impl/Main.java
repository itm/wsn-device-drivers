package de.uniluebeck.itm.Impl;

import de.uniluebeck.itm.devicedriver.MacAddress;
import de.uniluebeck.itm.devicedriver.MessagePacket;
import de.uniluebeck.itm.devicedriver.MessagePacketListener;
import de.uniluebeck.itm.devicedriver.PacketType;
import de.uniluebeck.itm.devicedriver.State;
import de.uniluebeck.itm.devicedriver.async.*;

public class Main implements DeviceAsync{

	
	public OperationHandle<Void> setMessage() {
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				System.out.println("cancel ausgefuehrt");
				
			}

			@Override
			public Void get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				
				return State.DONE;
			}};
		
	}
	
	
	public OperationHandle<Void> getMessage() {
		
		return new OperationHandle<Void>(){

			@Override
			public void cancel() {
				System.out.println("cancel ausgefuehrt");
				
			}

			@Override
			public Void get() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public State getState() {
				
				return State.RUNNING;
			}};
		
	}
	
	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			PacketType... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMessagePacketListener(MessagePacketListener listener,
			int... types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> eraseFlash(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<byte[]> readFlash(int address, int length,
			long timeout, AsyncCallback<byte[]> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<MacAddress> readMac(long timeout,
			AsyncCallback<MacAddress> callback) {
		
		
		
		return null;
	}

	@Override
	public void removeMessagePacketListener(MessagePacketListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OperationHandle<Void> reset(long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> send(MessagePacket packet, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeFlash(int address, byte[] data,
			int length, long timeout, AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> writeMac(MacAddress macAddress, long timeout,
			AsyncCallback<Void> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OperationHandle<Void> program(byte[] bytes,
			long timeout, AsyncCallback<Void> callback) {
		
		callback.onSuccess(null);
		try {
			System.out.println("jup es geht Main");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
