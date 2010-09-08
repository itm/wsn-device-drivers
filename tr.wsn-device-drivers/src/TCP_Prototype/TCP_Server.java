
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;


public class TCP_Server {
	
	public static void main(String[] args) throws TTransportException{
		
		try{
			final TNonblockingServerSocket socket = new TNonblockingServerSocket(50000);
			
			final TProtocolFactory protocolFactory = new TCompactProtocol.Factory();
			final TestService.Processor processor = new TestService.Processor(new Handler());
			final TServer server = new THsHaServer(processor, socket,
                    new TFramedTransport.Factory(), protocolFactory);
			server.serve();
			
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class Handler implements TestService.Iface{
		
		@Override
		public void getString() throws TException {
			System.out.println("Hello World");
		}
		
	}
}

