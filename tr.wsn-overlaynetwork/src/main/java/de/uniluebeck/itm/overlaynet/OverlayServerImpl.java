package de.uniluebeck.itm.overlaynet;

import java.util.ArrayList;
import java.util.List;
import org.apache.thrift.TException;
import org.apache.thrift.server.TNonblockingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.overlaynet.async.server.Overlay_Server;



public class OverlayServerImpl implements OverlayServer.Iface {

	public List<Metadata> list = new ArrayList<Metadata> ();
	private static final Logger LOGGER =
	    LoggerFactory.getLogger(Overlay_Server.class.getName());
	public List<Metadata> getList() {
		return list;
	}

	public void setList(List<Metadata> list) {
		this.list = list;
	}


	@Override
	public long add(Metadata data) throws TException {
		// TODO Auto-generated method stub
		//Daten zur Liste der Metadaten am Server hinzufügen
		LOGGER.info("Client fügt Metadaten hinzu");
		list.add(data);
		for (Metadata p : list) {
			System.out.println("Metadaten:");
			System.out.println(p.getFabricate());
			System.out.println(p.getId());
			System.out.println(p.getOsversion());
			System.out.println(p.getIpadress());	
		}
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Status 0, wenn es beim Hinzufügen keinen Fehler gegeben hat
		return 0;
	}

	@Override
	public List<Metadata> search(long ID) throws TException {
		// TODO Auto-generated method stub
		
		List<Metadata> rlist = new ArrayList <Metadata> ();
		for (Metadata p : list) {
			if (p.getId()==ID)
			{
				rlist.add(p);
			}
		}
		return rlist;
	}

	@Override
	public long remove(long id) throws TException {
		// TODO Auto-generated method stub
		List<Metadata> rlist = new ArrayList <Metadata> ();
		System.out.println("größe vorher: " +list.size());
		for (Metadata p : list) {
			if (p.getId()==id)
			{
				list.remove(p);
			}
		}
		System.out.println("größe nachher: " +list.size());
		return 0;
	}

}
