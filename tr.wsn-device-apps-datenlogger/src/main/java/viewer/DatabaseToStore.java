package viewer;

import controller.NodeCapabilityStore;
import controller.NodeStore;
import model.Node;
import model.NodeCapability;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.Iterator;
import java.util.List;

/**
 * In this class, information is extracted from the Database and Stored to
 * proper Hashmaps.
 */

public class DatabaseToStore {
    private static final SessionFactory ourSessionFactory;


    static {
        try {
            ourSessionFactory = new AnnotationConfiguration().
                    configure("hibernate.cfg.xml").
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
     * @throws org.hibernate.HibernateException
     *
     */
    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    /**
     * This function retrieves a Node from the DataBase and stores it to NodeStore.
     *
     * @param nodeId
     */

    public final void getNodeToStore(final String nodeId) {

        final Session session = getSession();
        session.beginTransaction();
        final List nodeIds = session.createQuery("select node.id from NodeEntity node ").list();
        for (int i = 0; i < nodeIds.size(); i++) {
            if (nodeIds.get(i).toString().equals(nodeId))
            // System.out.println(nodeIds.get(i).toString());
            {
                final Node mynode;
                mynode = new Node();
                mynode.setID(nodeIds.get(i).toString());
                NodeStore.getInstance().add(mynode);
            }
        }
    }


    /**
     * This function retrieves a NodeCapability from the DataBase and stores it to NodeStore.
     */
    public final void getNodeCapability(final int nodeCapId) {

        try {
            final Session session = getSession();
            session.beginTransaction();
            final Query query;
            query = session.createQuery("select nodecapability.id," + " nodecapability.name, " + " nodecapability.nodeid, " + " nodecapability.value from NodecapabilityEntity nodecapability  where nodecapability.id=" + nodeCapId + " ");

            for (final Iterator it = query.iterate(); it.
                    hasNext();) {
                final Object[] row = (Object[]) it.next();
                System.out.println("ID: " + row[0].toString());
                System.out.println("Name: " + row[1].toString());
                System.out.println("NodeId: " + row[2].toString());
                System.out.println("Value: " + row[3].toString());
                final NodeCapability mynodeCap;
                mynodeCap = new NodeCapability();
                mynodeCap.setID(Integer.parseInt(row[0].toString()));
                mynodeCap.setName(row[1].toString());
                mynodeCap.setNodeID(row[2].toString());
                mynodeCap.setValue(row[3].toString());
                NodeCapabilityStore.getInstance().add(mynodeCap);
            }

            session.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
