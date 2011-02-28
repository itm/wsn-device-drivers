package de.uniluebeck.itm.persistence;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.metadatenserver.MetaDatenServer;
import de.uniluebeck.itm.metadaten.server.exception.NodeInDBException;

/**
 * This is a class that retrieves information from the "Store" Hashmap and
 * stores it to the database.
 * @author babel
 */
public class StoreToDatabase {
	/**
	 * Logger for saving operations
	 */
	private static Log log = LogFactory.getLog(StoreToDatabase.class);
	/** */
	private static final SessionFactory ourSessionFactory;
	/**Url to Hibernate Config*/    
	private static final URL url = ClassLoader.getSystemResource("hibernate.cfg.xml");

	    static {
	        try {
	            ourSessionFactory = new AnnotationConfiguration().configure(url).
	                    buildSessionFactory();
	        }
	        catch (final Exception ex) {
	            throw new ExceptionInInitializerError(ex);
	        }
	    }
	/**
	 * This functions creates a Hibernate Session.
	 * 
	 * @return ourSessionFactory.openSession()
	 * @throws HibernateException - Exception with Hibernate
	 */
	public static Session getSession() throws HibernateException {
		return ourSessionFactory.openSession();
	}

	/**
	 * Ueberprueft ob ein Knoten bereits in der Datenbank gespeichert ist
	 * 
	 * @param parentnode
	 * @return boolean true if node already in DB
	 */
	public boolean nodeinDB(final Node node) {
		boolean inDB = false;
		final DatabaseToStore db = new DatabaseToStore();
		try{
			if(db.getNode(node).getId( ).equals(node.getId())){
				inDB = true;
			}
		}catch(final NullPointerException e){
			System.err.println("Nullpointer in NodeinDB");
		}
		return inDB;
	}

	/**
	 * This function stores a node Entity to the database.
	 * 
	 * @param Node node that should be written to the database
	 * @throws Exception - if node is already in DB
	 */
	public final void storeNode(final Node node) throws NodeInDBException {
		// TODO tokenizer wieder nutzen falls wir WiseML die keys generieren
		// lassen
		if (!nodeinDB(node)) {
			final Session session = getSession();
			final Transaction transaction = session.beginTransaction();
			log.info("StoretoDataBase.storeNode: Saving Node");
			session.save(node);
			log.info("StoretoDataBase.storeNode: Saved Node");
			transaction.commit();
			session.close();
		} else {
			//TODO wenn bereits in DB, KNoten updaten oder Anfrage ignorieren?
			log.info("Node with id "+node.getId().toString() + " already in DB, data will be updated");
			updateNode(node);
			throw new NodeInDBException(
					"Node with id "+node.getId().toString() + " already in DB, data will be updated. Duplicate Entry for this Node. Please use Refresh.");
		}
	}

	/**
	 * This function updates a node Entity to the database.
	 * 
	 * @param Node node that should be updatetes in database
	 * @throws NodeInDBException - if node is already in DB
	 */
	public final void updateNode(final Node node) {
		if (nodeinDB(node)){
			log.info("Update Node with ID" + node.getId().toString());
			deleteCapability(node);
			final Session session = getSession();
			final Transaction transaction = session.beginTransaction();
			// for (Capability cap : node.getCapabilityList()) {
			// System.out.println("CAPPPPPPP HINZU!!!");
			// // session.update("capability", cap);
//			 session.delete("capability", cap);
			// }
//			for (Capability cap : node.getCapabilityList()){
//				session.update(cap);
//			}
			session.update(node);
			transaction.commit();
			session.close();
		}else {
			try {
				storeNode(node);
			} catch (final NodeInDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Deletes theCapabilities of the given Node
	 * @param node - Node which capabilities should be removed
	 */
	public void deleteCapability(final Node node) {
		final DatabaseToStore fromDB = new DatabaseToStore();
		final Node capnode = fromDB.getNode(node);
		System.out.println("SIZE: " + capnode.getCapabilityList().size());
		final Session session = getSession();
		final Transaction transaction = session.beginTransaction();
		log.info("Deleting Capabilities of node " + node.getId().toString());
		for (Capability cap : capnode.getCapabilityList()) {
			session.delete("capability", cap);
		}
		transaction.commit();
		session.close();
	}

	/**
	 * Deletes the given Node and it's capabilities
	 * 
	 * @param node that should be deleted from repository
	 */
	public void deleteNode(final Node node) {
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
	public void deleteoldNodes(final Date timestamp) {
		final DatabaseToStore fromDB = new DatabaseToStore();
		List<Node> deletelist = new ArrayList<Node>();
		deletelist = fromDB.getoldNodes(timestamp);
		for (Node nod : deletelist) {
			deleteNode(nod);
		}
	}
}
