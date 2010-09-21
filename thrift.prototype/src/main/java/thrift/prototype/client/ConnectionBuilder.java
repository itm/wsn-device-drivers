package thrift.prototype.client;

import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;

import thrift.prototype.files.AsyncDevice;

/**
 * Stellt eine asynchrone Thrift-Verbindung her
 * @author Andreas Maier
 *
 */
public class ConnectionBuilder {

	/* Prinzipiel nicht notwendig, da man dies auch in Operation machen kann,
	 * aber wegen der Moeglichkeit die Verbindung spaeter tauschen zu koennen,
	 * bleibt diese Klasse erstmal erhalten.
	 */
	
	private AsyncDevice.AsyncClient client;
	String uri;
	int port;
	
	/**
	 * Konstruktor fuer Verbindungen mit anonymen Transaktionsmanager
	 * @param uri
	 * @param port
	 * @throws Exception
	 */
	public ConnectionBuilder (String uri, int port) throws Exception{
	        this.uri = uri;
	        this.port = port;
	        client = new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(), new TAsyncClientManager(), new TNonblockingSocket(uri, port));
	    }
	    
	/**
	 * Konstruktor fuer Verbindung durch einen einheitlichen Transaktionsmanager
	 * @param uri
	 * @param port
	 * @param acm_
	 * @throws Exception
	 */
	public ConnectionBuilder (String uri, int port, TAsyncClientManager acm_) throws Exception{
        this.uri = uri;
        this.port = port;
        client = new AsyncDevice.AsyncClient(new TBinaryProtocol.Factory(), acm_, new TNonblockingSocket(uri, port));
	}
	
	/**
	 * Gibt die hergestellte Verbindung zurueck
	 * @return
	 */
	public AsyncDevice.AsyncClient getClient(){
		return client;
	}
	
}
