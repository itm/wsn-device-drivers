package thrift.prototype.operation;

import java.util.HashSet;
import java.util.Set;


public class OperationKeys {

	private static OperationKeys instance = new OperationKeys();
	private Set<String> keys = new HashSet<String>();
	
	private OperationKeys(){
	}
	
	public static OperationKeys getInstance() {
		return instance;
	}
	
	public String getKey(){
		
		boolean doublekey = false;
		String key;
		
		/* erstellen eines zufaelligen key, mit dem ein Client identifiziert werden kann */
		do{
			double zwkey = Math.random();
			key = String.valueOf(zwkey);
			doublekey = keys.contains(key);
		}while(true == doublekey);
		
		keys.add(key);
		
		return key;
	}
	
	public void removeKey(String key){
		
		keys.remove(key);
		
	}
	
}
