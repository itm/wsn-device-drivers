package viewer;

import controller.EdgeAttrStore;
import controller.EdgeStore;
import controller.NodeCapabilityStore;
import controller.NodeStore;
import model.Edge;
import model.EdgeAttribute;
import model.EdgeEntity;
import model.EdgeattributeEntity;
import model.Node;
import model.NodeCapability;
import model.NodeEntity;
import model.NodecapabilityEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import java.util.StringTokenizer;


/**
 * This is a class that retrieves information
 * from the "Store" Hashmap and stores it
 * to the database.
 */
public class StoreToDatabase {

    private static final SessionFactory ourSessionFactory;

    static {
        try {
            ourSessionFactory = new AnnotationConfiguration().
                    configure("hibernate.cfg.xml").
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
     * This function retrieves an Edge Entity from the "store" hashmap
     * and stores it to the database.
     *
     * @param key of the Edge Entity.
     */
    public final void retrieveAndStoreEdge(final String key) {
      //  final Edge edge = EdgeStore.getInstance().get(key);
      //  storeEdge(edge);
    }

    /**
     * This function retrieves an EdgeAttribute Entity from the "store" hashmap
     * and stores it to the database.
     *
     * @param key of the EdgeAttribute Entity.
     */
    public final void retrieveAndStoreEdgeAttribute(final int key) {
        final EdgeAttribute edgeAttr = EdgeAttrStore.getInstance().get(key);
        storeEdgeAttribute(edgeAttr);
    }

    /**
     * This  function retrives a Node Entity form the "store" hashmap.
     * and stores it to the database
     *
     * @param key of the Node Entity
     */
    public final void retrieveAndStoreNode(final String key) {
        final Node node = NodeStore.getInstance().get(key);
        storeNode(node);
    }

    /**
     * This  function retrives a NodeCapability Entity form the "store" hashmap.
     * and stores it to the database
     *
     * @param key of the NodeCapability Entity
     */
    public final void retrieveAndStoreNodeCapability(final int key) {
        final NodeCapability myCapability = NodeCapabilityStore.getInstance().get(key);
        storeNodeCapability(myCapability);
    }

    /**
     * This function is used to tokenize a given url, and extract information from it.
     *
     * @param url
     * @return tokens of the given url.
     */
    public final String[] tokenizeString(final String url) {
        String tokens[] = new String[6];
        int tokensNum = 0;
        final StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(url, ":");
        for (int i = 0; i < 6; i++) {
            tokens[i] = tokenizer.nextToken(":");
            tokensNum++;
        }
        if (tokensNum > 6) {
            System.out.println("wrong url");
            System.exit(0);
        }

        return tokens;
    }


    /**
     * This function stores o node Entity to the database.
     *
     * @param node
     */
    public final void storeNode(final Node node) {
        try {
            final NodeEntity myNode = new NodeEntity();

            final String key = node.getID();
            final String[] tokens;
            tokens = tokenizeString(key);
            myNode.setId(tokens[4] + ":" + tokens[5]);
            myNode.setGateway(tokens[4]);
            final Session session = getSession();
            final Transaction transaction = session.beginTransaction();
            session.save(myNode);

            transaction.commit();
            session.close();
        }
        catch (Exception e) {
            System.out.println(e.getStackTrace().toString());
        }

    }

    /**
     * Stores an Edge entity to the database.
     *
     * @param edge entity.
     */
    public final void storeEdge(final Edge edge) {
        final EdgeEntity myEdge;
        myEdge = new EdgeEntity();
        final String[] sourceTokens;
        sourceTokens = tokenizeString(edge.getSource());
        final String[] targetTokens;
        targetTokens = tokenizeString(edge.getTarget());
        myEdge.setId(edge.getID());
        myEdge.setSource(sourceTokens[4] + ":" + sourceTokens[5]);
        myEdge.setTarget(targetTokens[4] + ":" + targetTokens[5]);

        final Session session = getSession();
        final Transaction transaction = session.beginTransaction();
        session.save(myEdge);
        transaction.commit();
        session.close();

    }

    /**
     * Stores an EdgeAttribute entity to the database.
     *
     * @param edgeAttr entity.
     */
    public final void storeEdgeAttribute(final EdgeAttribute edgeAttr) {
        final EdgeattributeEntity edgeAtrEntity = new EdgeattributeEntity();
        edgeAtrEntity.setId(edgeAttr.getID());
        edgeAtrEntity.setEdgeid(edgeAttr.getEdgeID());
        edgeAtrEntity.setName(edgeAttr.getName());
        edgeAtrEntity.setValue(edgeAttr.getValue());
        final Session session = getSession();
        final Transaction transaction = session.beginTransaction();
        session.save(edgeAtrEntity);
        transaction.commit();
        session.close();

    }

    /**
     * Stores a NodeCapablity entity to the database.
     *
     * @param nodeCap entity.
     */
    public final void storeNodeCapability(final NodeCapability nodeCap) {
        final NodecapabilityEntity nodecapEntity = new NodecapabilityEntity();
        final String[] tokens;
        tokens = tokenizeString(nodeCap.getNodeID());
        nodecapEntity.setId(nodeCap.getID());
        nodecapEntity.setNodeid(tokens[4] + ":" + tokens[5]);
        nodecapEntity.setName(nodeCap.getName());
        nodecapEntity.setValue(nodeCap.getValue());

        final Session session = getSession();
        final Transaction transaction = session.beginTransaction();
        session.save(nodecapEntity);
        transaction.commit();
        session.close();

    }


}
