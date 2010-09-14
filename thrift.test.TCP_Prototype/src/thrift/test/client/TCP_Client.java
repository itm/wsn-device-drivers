package thrift.test.client;

import org.apache.thrift.async.TAsyncClientManager;

import de.uniluebeck.itm.devicedriver.async.AsyncCallback;

public class TCP_Client {

	public static void main(String[] args) throws Exception {
		
		TAsyncClientManager acm = new TAsyncClientManager();
		
		/* Gemeinsamer ClientManager */
		TCP_Stub stub1 = new TCP_Stub("localhost", 50000, acm);
		TCP_Stub stub2 = new TCP_Stub("localhost", 50000, acm);
		
		/* jeweils eigene Clients */
		//TCP_Stub stub1 = new TCP_Stub("localhost", 50000);
		//TCP_Stub stub2 = new TCP_Stub("localhost", 50000);
		
		int i=0;
		int j=0;
		
		while(true){
			System.out.println("in: ");
			int input = System.in.read();
			
			switch (input){
			// 1 druecken
			case 49:
				stub1.setMessage("Dies ist Nachricht Nr.: "+i+" von Client1");
				System.out.println(stub1.getMessage());
				i++;
				break;
			// 2 druecken
			case 50:
				stub2.setMessage("Dies ist Nachricht Nr.: "+j+" von Client2");
				System.out.println(stub2.getMessage());
				j++;
				break;
			case 51:
				// konnte ich nicht sinnvoll testen, da ich keine DeviceBinFile erstellen konnte
				// vlt ware es sinnvoll hier ein Pfad zu uebergeben, anstatt des DeviceBinFile
				// sollte aber theoretisch gehen
				stub1.program(null, 0L, new AsyncCallback<Void>(){

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onFailure(Throwable throwable) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void result) {
						System.out.println("geht");
						
					}

					@Override
					public void onProgressChange(float fraction) {
						// TODO Auto-generated method stub
						
					}});
				break;
			}
		}

	}

}
