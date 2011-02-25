package de.uniluebeck.itm.persistence;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;

/**
 * This is a class that retrieves information from the "Store" Hashmap and
 * stores it to the database.
 */
public class StoreToDatabase {
	static File datafile = new File(
			"C:\\uni hl\\workspace\\fallstudie2010\\sources\\tr.wsn-device-metadatenapps\\metadatenserver\\hibernate.cfg.xml");

	 private static final SessionFactory ourSessionFactory;
	    private static final URL url = ClassLoader.getSystemResource("hibernate.cfg.xml");

	    static {
	        try {
	            ourSessionFactory = new AnnotationConfiguration().configure(url).
	                    buildSessionFactory();
	        }
	        catch (Exception ex) {
	            throw new ExceptionInInitializerError(ex);
	        }
	    }
	/**
	 * This functions creates a Hibernate Session.
	 * 
	 * @return ourSessionFactory.openSession()
	 * @throws HibernateException
	 */
	public static Session getSession() throws HibernateException {
		return ourSessionFactory.openSession();
	}

	/**
	 * Ueberprueft ob ein Knoten bereits in der Datenbank gespeichert ist
	 * 
	 * @param parentnode
	 * @return
	 */
	public boolean nodeinDB(Node node) {
		boolean inDB = false;
		DatabaseToStore db = new DatabaseToStore();
		String dBNodeId = db.getNode(node).getId().getId();
		String dBNodeIP = db.getNode(node).getId().getIpAdress();
		if(!(dBNodeId == null) && (dBNodeIP == null)){
			if (dBNodeId.equals((node.getId().getId())) && dBNodeIP.equals((node.getId().getIpAdress()))) {
				inDB = true;
			}
		}
		return inDB;
	}

	/**
	 * This function stores o parentnode Entity to the database.
	 * 
	 * @param parentnode
	 * @throws Exception
	 */
	public final void storeNode(final Node node) throws Exception {
		// TODO tokenizer wieder nutzen falls wir WiseML die keys generieren
		// lassen
		if (!nodeinDB(node)) {
			final Session session = getSession();
			final Transaction transaction = session.beginTransaction();
			System.out.println("StoretoDataBase.storeNode: Saving Node");
			session.save(node);
			transaction.commit();
			System.out.println("StoretoDataBase.storeNode: Saved Node");
			session.close();
		} else {
			//TODO wenn bereits in DB, KNoten updaten oder Anfrage ignorieren?
			System.out.println("Node already in DB, data will be updated");
			updateNode(node);
			throw new Exception(
					"Knoten bereits in DB vorhanden. Duplicate Entry for this Node. Please use Refresh.");
		}
	}

	/**
	 * This function updates the current entry of a node.
	 * 
	 * @param parentnode
	 * @throws Exception
	 */
	public final void updateNode(final Node node) {
		// TODO tokenizer wieder nutzen falls wir WiseML die keys generieren
		// lassen
		if ((nodeinDB(node))){
			System.out.println("!!! im refreshif, updaten des Knoten");
			deleteCapability(node);
			final Session session = getSession();
			final Transaction transaction = session.beginTransaction();
			// for (Capability cap : node.getCapabilityList()) {
			// System.out.println("CAPPPPPPP HINZU!!!");
			// // session.update("capability", cap);
			// session.delete("capability", cap);
			// }
			session.update(node);
			transaction.commit();
			session.close();
		}else {
			try {
				System.out.println("!!! in der refreshElse, zufuegen des Knotens");
				storeNode(node);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Deletes theCapabilities of the given Node
	 */
	public void deleteCapability(Node node) {
		DatabaseToStore fromDB = new DatabaseToStore();
		Node capnode = fromDB.getNode(node);
		System.out.println("SIZE: " + capnode.getCapabilityList().size());
		final Session session = getSession();
		final Transaction transaction = session.beginTransaction();
		for (Capability cap : capnode.getCapabilityList()) {
			System.out.println("CAPPPPPPP Deleted!!!");
			// session.update("capability", cap);
			session.delete("capability", cap);
		}
		transaction.commit();
		session.close();
	}

	/**
	 * Deletes the given Node and it's capabilities
	 * 
	 * @param node
	 */
	public void deleteNode(Node node) {
//		deleteCapability(node);
		final Session session = getSession();
		final Transaction transaction = session.beginTransaction();
		session.delete(node);
		transaction.commit();
		session.close();
	}

	/**
	 * Deletes all node entries, that are older than given Timestamp
	 * 
	 * @param timestamp - maximum age of a node
	 */
	public void deleteoldNodes(Date timestamp) {
		DatabaseToStore fromDB = new DatabaseToStore();
		List<Node> deletelist = new ArrayList<Node>();
		deletelist = fromDB.getoldNodes(timestamp);
		for (Node nod : deletelist) {
			deleteNode(nod);
		}
	}
}
