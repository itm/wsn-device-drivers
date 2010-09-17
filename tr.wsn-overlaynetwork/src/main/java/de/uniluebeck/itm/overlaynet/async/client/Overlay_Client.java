package de.uniluebeck.itm.overlaynet.async.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TTransportException;

import de.uniluebeck.itm.overlaynet.WriteXML;
import de.uniluebeck.itm.overlaynet.MetaDatenList;
import de.uniluebeck.itm.overlaynet.Metadata;
import de.uniluebeck.itm.overlaynet.OverlayServer;

import de.uniluebeck.itm.overlaynet.OverlayServer.AsyncClient.add_call;
import de.uniluebeck.itm.overlaynet.OverlayServer.AsyncClient.remove_call;
import de.uniluebeck.itm.overlaynet.OverlayServer.AsyncClient.search_call;


//import tserver.gen.TimeServer;
//import tserver.gen.TimeServer.AsyncClient.time_call;

public class Overlay_Client {

	
	public static void main (String[] args) {
		
		// Client in einer Schleife laufen lassen.
		while(true) {
			String line = "";
			System.out.println("Ich bin der Client!");
			System.out.println("add oder search+id eingeben: ");
			//tastatur einlesen
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			try {
				line = in.readLine();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if (line.matches("add"))
			{
				
				// Erstellen des Clients-Sockets mit IP und port
				TNonblockingSocket socket;
				try {
					socket = new TNonblockingSocket("localhost", 7911);
					
					// Erstellen eines Client-Manager
					final TAsyncClientManager acm = new TAsyncClientManager();
					
					// Instantieren und Initieren des Clients
					final OverlayServer.AsyncClient client = new OverlayServer.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
					
					// Synchro-Objekt
					final Object o = new Object();
					//Metadatenobjekt erstellen
					Metadata md=new Metadata ();
					md.setId(25);
					md.setFabricate("Jennec");
					md.setIpadress("192.168.2.1");
					md.setOsversion("1.2");
					try {						
						// Ausfuehren der add-Methode
						// Uebergabe der hinzuzufügenden Metadaten und des Callback-Objekts
						// für die Rückmeldung über den Erfolg der Aktion
						client.add(md, new AsyncMethodCallback<OverlayServer.AsyncClient.add_call >() {
							// Bei erfolgreichem Ausfueren wird diese Methode
							// des Callback Objektes ausgefuehrt
							@Override
							public void onComplete(add_call response) {
								try {
									long status = response.getResult();
									System.out.println("Die Operation wurde " +
											"mit Status " + status + "abgeschlossen");
									
								} catch (TException e) {
									e.printStackTrace();
								}
								/* benachrichtigen des synchro-objekts
								 */
//								System.out.println("Kurz vor Sync");
//								synchronized(o) {
//									o.notifyAll();
//								}
//								System.out.println("und danach");
							}
							
							// Bei Callback Fehlern wird diese Methode ausgefuehrt
							@Override
							public void onError(Throwable arg0) {
							}
							
						});
						
						/* Thread ein wenig warten lassen, damit server die Moeglichkeit hat 
				   seine Arbeit zu tun, bevor die naechste Methode des Clients aufgerufen wird*/
//						synchronized(o) {
//							try {
//								o.wait(100000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
						
					} catch (TTransportException e) {
						e.printStackTrace();
					} catch (TException e) {
						e.printStackTrace();
					}
				} catch (TTransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (line.matches("^search [0-9]+"))
			{
				// Ein wenig Kommunikation
				System.out.println("Suche gestartet:");
				// Erstellen des Clients-Sockets mit IP und port
				TNonblockingSocket socket;
				try {
					socket = new TNonblockingSocket("localhost", 7911);
					
					// Erstellen eines Client-Manager
					final TAsyncClientManager acm = new TAsyncClientManager();
					
					// Instantieren und Initieren des Clients
					final OverlayServer.AsyncClient client = new OverlayServer.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);
					
					// Synchro-Objekt
//					final Object o = new Object();
					
				
					try {
						
						
						// Ausfuehren der search-Methode
						// Uebergabe der zu suchenden ID und des Callback-Objekts
						client.search(25, new AsyncMethodCallback<OverlayServer.AsyncClient.search_call>() {
							// Bei erfolgreichem Ausfueren wird diese Methode
							// des Callback Objektes ausgefuehrt
							@Override
							public void onComplete(search_call response) {
								try {
									List<Metadata> rlist = new ArrayList <Metadata> ();
									rlist=response.getResult();
									System.out.println("Folgende MetaDaten stehen zur Verfügung");
									for (Metadata p : rlist) {
										System.out.println("Id: " +p.getId());
										System.out.println("Fabrikat: " + p.getFabricate());
										System.out.println("OS: " +p.getOsversion());
										System.out.println("IP-Adresse: " +p.getIpadress());
									}
									MetaDatenList example = new MetaDatenList();
									example.setList(rlist);
									new WriteXML().writeXMLtoFile("result.xml", example);
									System.out.println("Ergebnis in result.xml geschrieben");
								} catch (TException e) {
									e.printStackTrace();
								}
								/* benachrichtigen des synchro-objekts
								 */
//								System.out.println("Kurz vor Sync");
//								synchronized(o) {
//									o.notifyAll();
//								}
//								System.out.println("und danach");
							}
							
							// Bei Callback Fehlern wird diese Methode ausgefuehrt
							@Override
							public void onError(Throwable arg0) {
							}
	
						});
					} catch (TTransportException e) {
						e.printStackTrace();
					} catch (TException e) {
						e.printStackTrace();
					}
				} catch (TTransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (line.matches("^remove [0-9]+"))
			{
				
				// Erstellen des Clients-Sockets mit IP und port
				TNonblockingSocket socket;
				try {
					socket = new TNonblockingSocket("localhost", 7911);
					
					// Erstellen eines Client-Manager
					final TAsyncClientManager acm = new TAsyncClientManager();
					
					// Instantieren und Initieren des Clients
					final OverlayServer.AsyncClient client = new OverlayServer.AsyncClient(new TBinaryProtocol.Factory(),acm,socket);

					try {
						
						// Ausfuehren der add-Methode
						// Uebergabe der hinzuzufügenden Metadaten und des Callback-Objekts
						// für die Rückmeldung über den Erfolg der Aktion
						client.remove(25, new AsyncMethodCallback<OverlayServer.AsyncClient.remove_call >() {
							// Bei erfolgreichem Ausfueren wird diese Methode
							// des Callback Objektes ausgefuehrt
							@Override
							public void onComplete(remove_call response) {
								try {
									long status = response.getResult();
									System.out.println("Die Operation wurde " +
											"mit Status " + status + " abgeschlossen");
									
								} catch (TException e) {
									e.printStackTrace();
								}

							}
							
							// Bei Callback Fehlern wird diese Methode ausgefuehrt
							@Override
							public void onError(Throwable arg0) {
								System.err.println("Fehler beim Entfernen der Metadaten");
							}
							
						});
						
					
					} catch (TTransportException e) {
						e.printStackTrace();
					} catch (TException e) {
						e.printStackTrace();
					}
				} catch (TTransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
	}
}
