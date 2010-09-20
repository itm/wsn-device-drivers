package thrift.prototype.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.thrift.TException;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.Impl.Main;
import de.uniluebeck.itm.devicedriver.DeviceBinFile;
import de.uniluebeck.itm.devicedriver.async.AsyncCallback;
import de.uniluebeck.itm.devicedriver.async.OperationHandle;

import thrift.prototype.files.AsyncDevice;
import thrift.prototype.files.LoginFailed;

//TODO entweder Rohdaten direkt per program (list aus binaerdaten) uebertragen oder 
//TODO die Rohbloecke einzeln per transfer

public class TCP_Server {

	public static void main(String[] args) throws TTransportException {
	
		   // put up a server
	    final TNonblockingServer s = new TNonblockingServer(new AsyncDevice.Processor(new Handler()), new TNonblockingServerSocket(50001));
	    new Thread(new Runnable() {
	      @Override
	      public void run() {
	    	// Ein wenig Kommunikation
			System.out.println("Ich bin der Server!");
	        s.serve();
	      }
	    }).start();
	}
	
	//Implementierung der im Thrift-IDL festgelegten Services
	//TestService.Iface ist das dazugehoerige Interface
	private static class Handler implements AsyncDevice.Iface{

		private static Set<String> keys= new HashSet<String>();
		private static HashMap<String,ClientID> clientIDList = new  HashMap<String, ClientID>();
		private static HashMap<String,String> UserList = new  HashMap<String, String>();
		
		@Override
		public String connect(String userName, String passWord) throws LoginFailed, TException {
			
			/*
			 * Authentifizierung des Clients:
			 */
			
			LoginContext lc = null;
			try {
			    lc = new LoginContext("Sample", new AuthenticationCallbackHandler(userName, passWord));
			    lc.login();
			    System.out.println("User authentifiziert.");
			} catch (LoginException le) {
			    throw new LoginFailed(le.getMessage());			    
			} 	
			
			
			/*
			 * dem Client einen Key zuweisen:
			 */
			
			
			boolean doublekey = false;
			String key;
			
			// reconnect
			if(UserList.containsKey(userName)){
				key = UserList.get(userName);
			}
			// connect
			else {			
				/* erstellen eines zufaelligen key, mit dem ein Client identifiziert werden kann */
				do{
					double zwkey = Math.random();
					key = String.valueOf(zwkey);
					doublekey = keys.contains(key);
				}while(true == doublekey);
				
				/* erstellen einer Client-Repraesantition und speichern in einer Liste */
				ClientID clientId = new ClientID();
				keys.add(key);
				clientIDList.put(key, clientId);
				UserList.put(userName, key);
			}
			
			System.out.println("User verbunden.");
			
			return key;
		}
		
		@Override
		public void disconnect(String key) throws TException {
			keys.remove(key);
			UserList.remove(key);
			clientIDList.remove(key);
		}
		
		@Override
		public void setMessage(String key, String HandleKey, String message) throws TException {
			
			ClientID clientID = clientIDList.get(key);
			OperationHandle<Void> handle = null;
			clientID.setHandleList(HandleKey, handle);
			clientID.setMessage(message);
		}

		@Override
		public String getMessage(String key, String HandleKey) throws TException {

			ClientID clientID = clientIDList.get(key);
			OperationHandle<Void> handle = null;
			clientID.setHandleList(HandleKey, handle);
			return clientID.getMessage();
		}
		
		@Override
		public void program(String key, String HandleKey, List<ByteBuffer> BinFile,
				String description, long timeout) throws TException {
			Main test = new Main();
			
			/* erstmal auskommentiert da ich keine 
			   Moeglichkeit habe eine DeviceBinFile zu erstellen */
			ClientID clientID = clientIDList.get(key);
			
			OperationHandle<Void> handle = null;
			
			//clientID.saveBinFile(BinFile, description);
			
			//DeviceBinFile binaryImage=clientID.getBinFile();
			
			// Provisorisch null
			DeviceBinFile binaryImage = null;
			
			handle = test.program(binaryImage, timeout, new AsyncCallback<Void>(){

				@Override
				public void onCancel() {
					System.out.println("Abbruch im TCP-Server");
				}

				@Override
				public void onFailure(Throwable throwable) {
					System.out.println("Fehler im TCP-Server");
				}

				@Override
				public void onProgressChange(float fraction) {
					System.out.println("change im TCP-Server");
				}

				@Override
				public void onSuccess(Void result) {
					System.out.println("jup es geht im TCP-Server");
				}});
			
			clientID.setHandleList(HandleKey, handle);
			
		}

		@Override
		public void HandleCancel(String key, String OperationHandleKey)
				throws TException {
			
			ClientID clientID = clientIDList.get(key);
			OperationHandle<Void> handle = clientID.getHandle(OperationHandleKey);
			handle.cancel();
			clientID.removeHandle(OperationHandleKey);
			
		}

		@Override
		public void HandleGet(String key, String OperationHandleKey)
				throws TException {
			
			ClientID clientID = clientIDList.get(key);
			OperationHandle<Void> handle = clientID.getHandle(OperationHandleKey);
			handle.get();
			
			
		}

		@Override
		public String HandleGetState(String key, String OperationHandleKey)
				throws TException {
			
			ClientID clientID = clientIDList.get(key);
			OperationHandle<Void> handle = clientID.getHandle(OperationHandleKey);
			
			return handle.getState().getName();
		}
		
	}
}

/**
 * The application implements the CallbackHandler.
 *
 * <p> This application is text-based.  Therefore it displays information
 * to the user using the OutputStreams System.out and System.err,
 * and gathers input from the user using the InputStream System.in.
 */
class AuthenticationCallbackHandler implements CallbackHandler {

	String userName;
	char[] passWord;
	
	AuthenticationCallbackHandler(String userName, String PassWord){
		this.userName = userName;
		this.passWord = PassWord.toCharArray();
	}
	
    /**
     * Invoke an array of Callbacks.
     *
     * <p>
     *
     * @param callbacks an array of <code>Callback</code> objects which contain
     *			the information requested by an underlying security
     *			service to be retrieved or displayed.
     *
     * @exception java.io.IOException if an input or output error occurs. <p>
     *
     * @exception UnsupportedCallbackException if the implementation of this
     *			method does not support one or more of the Callbacks
     *			specified in the <code>callbacks</code> parameter.
     */
    public void handle(Callback[] callbacks)
    	throws IOException, UnsupportedCallbackException {
      
		for (int i = 0; i < callbacks.length; i++) {
		    if (callbacks[i] instanceof TextOutputCallback) {
	      
			// display the message according to the specified type
			TextOutputCallback toc = (TextOutputCallback)callbacks[i];
			switch (toc.getMessageType()) {
			case TextOutputCallback.INFORMATION:
	 		    System.out.println(toc.getMessage());
	 		    break;
	 		case TextOutputCallback.ERROR:
	 		    System.out.println("ERROR: " + toc.getMessage());
	 		    break;
	 		case TextOutputCallback.WARNING:
	 		    System.out.println("WARNING: " + toc.getMessage());
	 		    break;
	 		default:
	 		    throw new IOException("Unsupported message type: " +
	 					toc.getMessageType());
	 		}
	 
	 	    } else if (callbacks[i] instanceof NameCallback) {
	  
		 		NameCallback nc = (NameCallback)callbacks[i];
		 		nc.setName(this.userName);
	 
	 	    } else if (callbacks[i] instanceof PasswordCallback) {
	  
		 		PasswordCallback pc = (PasswordCallback)callbacks[i];
		 		pc.setPassword(this.passWord);
	  
	 	    } else {
	 	    	throw new UnsupportedCallbackException
	 			(callbacks[i], "Unrecognized Callback");
	 	    }
		}
    }
   

}

