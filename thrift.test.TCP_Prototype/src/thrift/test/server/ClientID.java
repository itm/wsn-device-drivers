package thrift.test.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.uniluebeck.itm.devicedriver.BinFileDataBlock;
import de.uniluebeck.itm.devicedriver.DeviceBinFile;

/**
 * Client-repraesantition im Server
 * @author Andreas Maier
 *
 */
public class ClientID {
	private String Message = "init";
	private AtomicBoolean blocked = new AtomicBoolean();
	private static List<BinFileDataBlock> blocklist = new  ArrayList<BinFileDataBlock>();

	ClientID(){
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
	
	public void saveBinFile(List<Integer> addresses, List<ByteBuffer> values){
		
		Iterator<ByteBuffer> valuesIterator = values.iterator();
		Iterator<Integer> addressesIterator = addresses.iterator();
		
		while(valuesIterator.hasNext()){
			BinFileDataBlock block = new BinFileDataBlock(addressesIterator.next(), valuesIterator.next().array());
			blocklist.add(block);
		}
		
		// TODO DeviceBinFile aus Rohdaten erstellen und auf Festplatte zwischenspeichern
	}
	
	public DeviceBinFile getBinFile(){
		
		// TODO DeviceBinFile von Festplatte auslesen und zurueckgeben
		
		return null;
	}

	public void setBlocked(AtomicBoolean blocked) {
		this.blocked = blocked;
	}

	public AtomicBoolean getBlocked() {
		return blocked;
	}
	
}
