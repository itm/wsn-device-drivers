package de.uniluebeck.itm.persistence;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uniluebeck.itm.metadaten.entities.Capability;
import de.uniluebeck.itm.metadaten.entities.Node;

/**
 * In this class, information is extracted from the Database and Stored to
 * proper Hashmaps.
 * @author Toralf Babel
 */

@SuppressWarnings("deprecation")
public class DatabaseToStore {
	/**
	 * Logger for loading operations
	 */
	private static Logger log = LoggerFactory.getLogger(StoreToDatabase.class);
	/**SessionFactory delivers sessions for DB-actions*/
	private static final SessionFactory OUR_SESSION_FACTORY;
	/***/
	private static final URL URL = ClassLoader
			.getSystemResource("hibernate.cfg.xml");

	static {
		try {
			OUR_SESSION_FACTORY = new AnnotationConfiguration().configure(URL)
					.buildSessionFactory();
		} catch (final Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	/**
	 * This functions creates a Hibernate Session.
	 * 
	 * @return ourSessionFactory.openSession()
	 * @throws HibernateException thrown from Hibernate
	 * 
	 */
	public static Session getSession() throws HibernateException {
		return OUR_SESSION_FACTORY.openSession();
	}

	/**
	 * This function retrieves all nodes from the DataBase that are like the
	 * given example. The capability of the example node is checked too. The
	 * method regards only the first Capability of the nodeexample. If there is
	 * no capability only the properties of the node are considered
	 * 
	 * @param nodeexample
	 *            Examplenode, which properties are considered for search
	 * @param ignoreCapability
	 *            If is set, the Capabilities are ignored for searching, only
	 *            nodeproperties are considered for loading nodes
	 * @return List <Node> list of nodes that capabilities match to the given
	 *         capability
	 */

	@SuppressWarnings("unchecked")
	public List<Node> getNodes(final Node nodeexample,
			final boolean ignoreCapability) {
		List<Node> resultlist = new ArrayList<Node>();
		final Session session = getSession();
		final Transaction transaction = session.beginTransaction();
		// resultlist =
		// session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
		final Criteria crit = session.createCriteria(Node.class);
		final Example exampleNode = Example.create(nodeexample);
		// resultlist =
		// session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
		// final List <NodeEntity> nodeIds =
		// session.createQuery("from NodeEntity parentnode where id =" +
		// parentnode.getId()).list();
		crit.add(exampleNode);
		log.info("Searching for nodes that are like the examplenode");
		resultlist = crit.list();
		log.info("Result before checking ID: " + resultlist.size());
		if (!(nodeexample.getId().getId() == null)) {
			final List<Node> templist = new ArrayList<Node>();
			for (Node node : resultlist) {
				if (nodeexample.getId().getId().matches(node.getId().getId())) {
					templist.add(node);
				}
			}
			resultlist.clear();
			resultlist.addAll(templist);
		}
		if (!(nodeexample.getId().getIpAdress() == null)) {
			final List<Node> templist = new ArrayList<Node>();
			for (Node node : resultlist) {
				if (nodeexample.getId().getIpAdress()
						.matches(node.getId().getIpAdress())) {
					templist.add(node);
				}
			}
			resultlist.clear();
			resultlist.addAll(templist);
		}
		if (resultlist.size() > 0) {
			for (Node nod : resultlist) {
				// nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+
				// nod.getId()).list());
				nod.getCapabilityList().size();
			}
		}
		transaction.commit();
		session.close();

		log.info("Found (before using Capability-Properties)" + resultlist.size() + " nodes");
		if ((nodeexample.getCapabilityList().size() > 0)
				&& (ignoreCapability == false)) {
			List<Node> templist = new ArrayList<Node>();
			templist = searchforcapabilities(resultlist, nodeexample
					.getCapabilityList());
			resultlist.clear();
			resultlist.addAll(templist);
		}
		log.info("Found at end of getNodes " + resultlist.size() + " nodes");
		return resultlist;
	}

	/**
	 * 
	 * @param nodeexamples
	 *            List of Nodes to which the capabilities found by the example -
	 *            capability will be matched
	 * @param cap Capability as example to filter the delivered list of nodes
	 * @return List <Node> list of nodes that capabilities match to the given
	 *         capability
	 */
	private List<Node> searchforcapabilities(final List<Node> nodeexamples,
			final List <Capability> capList) {
		final List<Node> resultList = new ArrayList<Node>();
		final Session session = getSession();
		for(Capability cap : capList){
			
			final Transaction transaction = session.beginTransaction();
			final Criteria crit = session.createCriteria(Capability.class);
			final Example exampleCap = Example.create(cap);
			List<Capability> capresult = new ArrayList<Capability>();
			crit.add(exampleCap);
			capresult = crit.list();
			for (Node node : nodeexamples) {
				
				for (Capability captemp : capresult) {
					if (captemp.getNode().getId().equals(node.getId())) {
						if (!(resultList.contains(node))) {
							resultList.add(node);
						}
					} else {
						if (!(resultList.contains(node))) {
							resultList.remove(node);
							break;
						}
					}
				}
			}
			transaction.commit();
		}
		session.close();
		return resultList;
	}

	/**
	 * This function retrieves a Node from the DataBase and stores it to
	 * NodeStore.
	 * 
	 * @param node
	 *            The nodeexample that the node searched should fit to
	 * 
	 * @return Node retunrs Node found in the database
	 */

	public Node getNode(final Node node) {
		final Session session = getSession();
		Node returnnode = new Node();
		final Transaction transaction = session.beginTransaction();
		// resultlist =
		// session.createQuery("select  from Node parentnode where id="+parentnode.getId()).list();
		// final List <NodeEntity> nodeIds =
		// session.createQuery("from NodeEntity parentnode where id =" +
		// parentnode.getId()).list();
		// final List <Node> tresultlist =
		// session.createQuery("from Node snode where id = "+
		// node.getId()).list();
		returnnode = (Node) session.get(Node.class, node.getId());
		returnnode.getCapabilityList().size();
		transaction.commit();
		session.close();
		return returnnode;
	}

	/**
	 * Delivers all nodes with an older timestamp than the given one
	 * 
	 * @param timestamp
	 *            - time limit: Nodes with older timestamp will be returned
	 * @return List <Node> list of nodes that have an older timestamp than the
	 *         given one
	 */
	public List<Node> getoldNodes(final Date timestamp) {
		final Session session = getSession();
		final DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		final Transaction transaction = session.beginTransaction();
		final List<Node> resultlist = session.createQuery(
				"from Node snode where timestamp < '" + dfmt.format(timestamp)
						+ "'").list();
		// for (Node nod: resultlist){
		// nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+
		// nod.getId()).list());
		// }
		if (resultlist.size() > 0) {
			resultlist.get(0).getCapabilityList().size();
//			for (Node nod : resultlist) {
//				// nod.setCapabilityList(session.createQuery("from Capability where parentnode_id ="+
//				// nod.getId()).list());
//				resultlist.get(0).getCapabilityList().size();
//			}

		}
		transaction.commit();
		session.close();
		// System.out.println("SIZE from DB" + resultlist.size());
		return resultlist;
	}
}
