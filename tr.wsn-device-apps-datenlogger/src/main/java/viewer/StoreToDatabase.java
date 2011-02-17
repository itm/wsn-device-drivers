package viewer;


import java.io.File;

import model.Node;
import model.NodeEntity;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;



/**
 * This is a class that retrieves information
 * from the "Store" Hashmap and stores it
 * to the database.
 */
public class StoreToDatabase {
	static File datafile = new File("C:\\uni hl\\workspace\\fallstudie2010\\sources\\tr.wsn-device-metadatenapps\\metadatenserver\\hibernate.cfg.xml"); 
	 
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            ourSessionFactory = new AnnotationConfiguration().
                    configure(datafile).
                    buildSessionFactory();
        }
        catch (Throwable ex) {
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
     * @param parentnode
     * @return
     
    public boolean nodeinDB (Node node)
    {
    	boolean inDB = false;
    	DatabaseToStore db = new DatabaseToStore();
    	if (db.getNode(node)!= null)
    		{
    			inDB=true;
    		}
    	return inDB;
    }
    
    */

    /**
     * This function stores o parentnode Entity to the database.
     *
     * @param parentnode
     */
    public final void storeNode(final Node node) {
    	//TODO tokenizer wieder nutzen falls wir WiseML die keys generieren lassen
    	
//        try {

//            myNode.setId(tokens[4] + ":" + tokens[5]);
            final Session session = getSession();
            final Transaction transaction = session.beginTransaction();
            session.save(node);
//            for (Capability cap : parentnode.getCapabilityList())
//            {
//            	session.save(cap);
//            }

            transaction.commit();
            session.close();
//        }
//        catch (Exception e) {
//            System.err.println("Fehler in StoreNode" +e.getStackTrace());
//        }

    }

   
}
