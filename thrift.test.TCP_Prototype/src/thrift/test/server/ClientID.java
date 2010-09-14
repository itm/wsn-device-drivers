package thrift.test.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uniluebeck.itm.devicedriver.BinFileDataBlock;
import de.uniluebeck.itm.devicedriver.DeviceBinFile;

/**
 * Client-repraesantition im Server
 * @author Andreas Maier
 *
 */
public class ClientID {
	private String Message;
	private static List<BinFileDataBlock> blocklist = new  ArrayList<BinFileDataBlock>();

	ClientID(){
	}
	
	public void setMessage(String message){
		Message = message;
	}
	
	public String getMessage() {
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
	
}
