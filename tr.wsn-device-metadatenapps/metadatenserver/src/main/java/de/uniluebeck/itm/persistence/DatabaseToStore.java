package de.uniluebeck.itm.persistence;


import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Example;

import de.uniluebeck.itm.metadaten.entities.Node;
import de.uniluebeck.itm.metadaten.entities.NodeCapability;

/**
 * In this class, information is extracted from the Database and Stored to
 * proper Hashmaps.
 */

public class DatabaseToStore {
//	static File datafile = new File("C:\\uni hl\\workspace\\fallstudie2010\\sources\\tr.wsn-device-metadatenapps\\metadatenserver\\hibernate.cfg.xml");
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

    @SuppressWarnings("unchecked")
	public List <Node> getNodes(Node nodeexample) {
    	List <Node> resultlist = new ArrayList<Node>();
    	final Session session = getSession();
    	  Node getnode = new Node();
	   	getnode.setId(nodeexample.getDescription());
	   	getnode.setMicrocontroller(nodeexample.getMicrocontroller());
	   	getnode.setIpAddress(nodeexample.getMicrocontroller());
        Transaction transaction = session.beginTransaction();
//        resultlist = session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
        Criteria crit = session.createCriteria(Node.class);
        Example exampleNode = Example.create(nodeexample);
//        resultlist = session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
//        final List <NodeEntity> nodeIds = session.createQuery("from NodeEntity parentnode where id =" + parentnode.getId()).list();
        crit.add(exampleNode);
        resultlist = crit.list();      
        System.out.println("Example" + resultlist.size());
       	if (resultlist.size() > 0){
    		for (Node nod: resultlist){
//        		nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+ nod.getId()).list());
    			nod.getCapabilityList().size();
        	}
    		
    	}
        transaction.commit();
        session.close();
        return resultlist;
    }
    
    /**
     * This function retrieves a Node from the DataBase and stores it to NodeStore.
     *
     * @param nodeId
     */

    public Node getNode(Node node) {
    	 final Session session = getSession();
    	 
        Transaction transaction = session.beginTransaction();
        Criteria crit = session.createCriteria(Node.class);
        Example exampleNode = Example.create(node);
//        resultlist = session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
//        final List <NodeEntity> nodeIds = session.createQuery("from NodeEntity parentnode where id =" + parentnode.getId()).list();
        final List <Node> resultlist = session.createQuery("from Node snode where id =" + node.getId()).list();
//        crit.add(exampleNode);
//        =crit.list();
        Node returnnode = new Node();
        if (resultlist.size() > 0){
        	resultlist.get(0).getCapabilityList().size();
        	returnnode =resultlist.get(0);
        }
        transaction.commit();
        session.close();
        System.out.println("SIZE from DB" +  resultlist.size());
        return returnnode;
    }
    public List<Node> getoldNodes(Date timestamp){
    	final Session session = getSession();
    	DateFormat dfmt = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" ); 
    	
        Transaction transaction = session.beginTransaction();  
    	final List <Node> resultlist = session.createQuery("from Node snode where timestamp < '" + dfmt.format(timestamp)+"'").list();
//    	for (Node nod: resultlist){
//    		nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+ nod.getId()).list());
//    	}
    	if (resultlist.size() > 0){
    		for (Node nod: resultlist){
//        		nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+ nod.getId()).list());
    			resultlist.get(0).getCapabilityList().size();
        	}
    		
    	}
    	 transaction.commit();
         session.close();
         System.out.println("SIZE from DB" +  resultlist.size());
    	return resultlist;
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
