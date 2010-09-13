package thrift.test.sync.server;


import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import tserver.gen.TimeServer;


public class TCP_Server {
	
	public static void main(String[] args) throws TTransportException{
		
		// oeffnen eines nicht blockierenden ServerSockets
		final TNonblockingServerSocket socket = new TNonblockingServerSocket(50000);
		// erstellen eines Service Prozessor durch uebergabe eines konkreten Handler
        final TimeServer.Processor processor = new TimeServer.Processor(new Handler());
        // festlegen des Seriealisierungs-Formats
        final TProtocolFactory protocolFactory = new TCompactProtocol.Factory();

        // erstellen des Servers
        final TServer server = new THsHaServer(processor, socket, new TFramedTransport.Factory(), protocolFactory);

        System.out.println("Ich bin der Server!");
        // starten des Servers
        server.serve();

	}
	
	/*	Implementierung der im Thrift-IDL festgelegten Services,
		TimeServer.Iface ist das dazugehoerige Interface	*/
	private static class Handler implements TimeServer.Iface{

		// konkrete Implementierung der time()-Methode
		@Override
		public long time() throws TException {
			return System.currentTimeMillis();
		}
	}
}

