package server;

import model.Capabilities;
import model.CapabilityConfiguration;
import model.Configuration;
import model.DescriptionEntity;
import model.Edge;
import model.EdgeAttribute;
import model.EdgeConfiguration;
import model.EdgeEntity;
import model.EdgeattributeEntity;
import model.Node;
import model.NodeCapability;
import model.NodeEntity;
import model.NodecapabilityEntity;
import model.sweF10DataRecord;
import model.sweF10QuantityRange;
import model.sweF10field;
import model.sweF10uom;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.metadata.ClassMetadata;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;
import org.apache.log4j.Logger;
import viewer.CreateXML;
import viewer.ParseXML;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.err;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


@WebService()
public class FindNeighbour extends ParseXML {

    /**
     * Create a sessionFactory for Hibernate queries.
     */
    private static SessionFactory ourSessionFactory;

    /**
     * The parameters of the class.
     */
    private static String[] argv;

    /**
     * Apache Log4J logger.
     */
    protected static final Logger LOGGER = Logger.getLogger("server");

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
     * This non-Web function creates a Hibernate Session.
     *
     * @return
     * @throws org.hibernate.HibernateException
     */
    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    /**
     * This function is used to tokenize a given url,
     * and extract information from it.
     *
     * @param url
     * @return tokens of the given url.
     */
    public final String[] tokenizeString(final String url) {
        String[] tokens = new String[6];
        int tokensNum = 0;
        final StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(url, ":");
        for (int i = 0; i < 6; i++) {
            tokens[i] = tokenizer.nextToken(":");
            tokensNum++;
        }
        if (tokensNum > 6) {
            LOGGER.fatal("ERROR: wrong url");
            System.exit(0);
        }

        return tokens;
    }

    /**
     * This function reads and adds the configuration
     * information to the node entities.
     *
     * @param entity
     * @return
     */
    public final String addConfig(final String entity) {
        //Read the xml File with a simple xml serializer.

        final Serializer serializer = new Persister();
        final File source = new File("/home/kleopatra/Documents/"
                + "xml/Configuration.xml");
        final Configuration config;
        try {
            config = serializer.read(Configuration.class, source);

            return config.getConfig() + ":" + entity;
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * This function reads and adds the configuration
     * information to the capability entities.
     *
     * @return
     */
    public final String addCapabilityConfig(final String entity) {
        //Read the xml File with a simple xml serializer.

        final Serializer serializer = new Persister();
        final File source = new File("/home/kleopatra/Documents/xml/"
                + "CapabilityConfiguration.xml");
        final CapabilityConfiguration config;
        try {
            config = serializer.read(CapabilityConfiguration.class, source);

            return config.getConfig() + ":" + entity;
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }

    }


    /**
     * This function reads and adds the confi information to the edge entities.
     *
     * @param entity
     */
    public final String addEdgeConfig(final String entity) {
        //Read the xml File with a simple xml serializer.

        final Serializer serializer = new Persister();
        final File source = new File("/home/kleopatra/Documents/xml/"
                + "EdgeConfiguration.xml");
        final EdgeConfiguration config;
        try {
            config = serializer.read(EdgeConfiguration.class, source);

            return config.getConfig() + ":" + entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * This function returns a GraphML File with
     * the capability results.
     *
     * @param path
     * @param name
     * @param value
     */
    public final void writeCapability(final String path, final String name,
                                      final String value) {
        try {
            final FileWriter fwriter = new FileWriter(path, true);
            fwriter.write("<capability>" + "\n");
            fwriter.write("<name>");
            fwriter.write(name);
            fwriter.write("</name>" + "\n");
            fwriter.write("<value>");
            fwriter.write(value);
            fwriter.write("</value>" + "\n");
            fwriter.write("</capability>" + "\n");
            fwriter.close();
        }
        catch (IOException ioe) {
            err.println("IOException: " + ioe.getMessage());
        }

    }

    public final void writeEdge(final String path, final String edgeId,
                                final String source, final String target) {
        try {
            final FileWriter fwriter = new FileWriter(path, true);
            fwriter.write("<edge>" + "\n");
            fwriter.write("<edgeId>");
            fwriter.write(edgeId);
            fwriter.write("</edgeId>" + "\n");
            fwriter.write("<source>");
            fwriter.write(source);
            fwriter.write("</source>" + "\n");
            fwriter.write("<target>");
            fwriter.write(target);
            fwriter.write("</target>" + "\n");
            fwriter.write("</edge>" + "\n");
            fwriter.close();
        }
        catch (IOException ioe) {
            err.println("IOException: " + ioe.getMessage());
        }

    }


    /**
     * This functions returns a GraphML File with the node neighbourhood.
     *
     * @param path of the file.
     * @param node id.
     */
    public final void writeNodes(final String path, final String node) {

        try {
            boolean append;
            append = true;
            final FileWriter fwriter = new FileWriter(path, append);
            fwriter.write("<node> ");
            fwriter.write("<id>" + node + "</id> ");
            fwriter.write("</node>" + "\n");
            fwriter.close();
        }
        catch (IOException ioe) {
            LOGGER.error("IOException", ioe);
        }
    }

    /**
     * This function is called by the
     * writeNodes(String, String) function,
     * witch returns an xml file
     * with the nodes neibourhood results.
     *
     * @param path
     */
    public final void write(final String path) {

        try {
            boolean append;
            append = true;
            final FileWriter fwriter = new FileWriter(path, append);
            fwriter.write("</graphml>");
            fwriter.close();
        }
        catch (IOException ioe) {
            err.println("IOException: " + ioe.getMessage());
        }
    }

    /**
     * This funtion reads an xml file, and returns its contents as a String.
     *
     * @param path
     * @return string path
     */
    public final String readXml(final String path) {
        String text;
        text = "";
        try {

            final FileInputStream fstream = new FileInputStream(path);

            final DataInputStream inputStream;
            inputStream = new DataInputStream(fstream);
            final BufferedReader bufread;
            bufread = new BufferedReader(new InputStreamReader(inputStream));
            String strLine;

            //Read File Line By Line
            while ((strLine = bufread.readLine()) != null) {
                text = text + strLine + "\n";
            }
            inputStream.close();

        } catch (Exception e) { //Catch exception if any
            err.println("Error: " + e.getMessage());
        }
        return text;
    }

    /**
     * This function overwrites every occurence of certain substring,
     * with a new pattern in a file.
     *
     * @param fname
     * @param oldPattern
     * @param replPattern
     */
    public static void readReplace(final String fname, final String oldPattern,
                                   final String replPattern) {
        String line;
        final StringBuffer sbuf = new StringBuffer();
        try {
            final FileInputStream fis = new FileInputStream(fname);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            while ((line = reader.readLine()) != null) {
                line = line.replaceAll(oldPattern, replPattern);
                sbuf.append(line).append('\n');
            }
            reader.close();
            final BufferedWriter out;
            out = new BufferedWriter(new FileWriter(fname));
            out.write(sbuf.toString());
            out.close();
        }
        catch (Throwable e) {
            err.println("*** exception ***");
        }
    }

    public final sweF10field getField(final String code,
                                      final String value,
                                      final String definition,
                                      final String fieldName) {

        /**
         * Initiallize uom.
         */

        final sweF10uom uom = new sweF10uom();
        uom.setCode(code);
        uom.setValue(value);

        /**
         * Initiallize QuantityRange.
         */

        final sweF10QuantityRange range = new sweF10QuantityRange();
        range.setDefinition("urn:ogc:def:property:" + definition);
        range.setUom(uom);

        /**
         * Initiallize field.
         */

        final sweF10field field = new sweF10field();
        field.setName(fieldName);
        field.setRange(range);
        field.setRole("urn:orc:property:" + definition);


        return field;

    }

    /**
     * This function produces a file with SensorML content,
     * describing the capabilities of a certain node.
     *
     * @param path
     * @param fieldList
     * @param definition
     * @param description
     * @param capabilityName
     */
    public final void writeSensorML(final String path,
                                    final List<sweF10field> fieldList,
                                    final String definition,
                                    final String description,
                                    final String capabilityName) {

        final sweF10DataRecord data = new sweF10DataRecord();
        data.setDefinition("urn:orc:def:property:" + definition);
        data.setDescription(description);
        data.setFieldList(fieldList);


        final Serializer serializer = new Persister();
        final Capabilities example = new Capabilities(capabilityName, data);
        final File result = new File(path);
        try {
            serializer.write(example, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        readReplace(path, "F10", ":");
    }

    /**
     * This Web method returns the capabilities of
     * a given node in GraphML.
     *
     * @param node
     * @return string with capability results in GraphML.
     */

    @WebMethod
    public final String getCapabilities(final String node) {

        final Session session = getSession();
        session.beginTransaction();

        //tokenize the node id to extract the network id and find the
        // neighbourhood accordingly.
        final String[] tokens;
        tokens = tokenizeString(node);
        final String nodeId = tokens[4] + ":" + tokens[5];


        final List capabilityIds = session.createQuery(""
                + "select nodecapability.name "
                + "from NodecapabilityEntity nodecapability "
                + "where nodecapability.nodeid ='" + nodeId + "'").list();
        final List capabilityValues = session.createQuery(""
                + "select nodecapability.value "
                + "from NodecapabilityEntity nodecapability "
                + "where nodecapability.nodeid ='" + nodeId + "'").list();
        final List<String> capabilityResults = new ArrayList<String>();
        // writeGraphML("/home/kleopatra/Documents/xml/
        // Capability_Results.xml");
        System.out.println("\n"
                + "***** Finding Node Capabilities *****" + "\n");
        System.out.println("Client Sends To Server Node Id: "
                + node);
        //Print the neighbourhood of the selected node.

        for (int i = 0; i < capabilityIds.size(); i++) {

            // System.out.println("Node Capability Name :" +
            // addCapabilityConfig(capabilityIds.get(i).toString()));
            // System.out.println("Node Capability Value :" +
            // capabilityValues.get(i).toString());
            capabilityResults.add(addCapabilityConfig(capabilityIds.get(i).toString()));
            capabilityResults.add(capabilityValues.get(i).toString());
            writeCapability("/home/kleopatra/Documents/xml/"
                    + "Capability_Results.xml",
                    addCapabilityConfig(capabilityIds.get(i).toString()),
                    capabilityValues.get(i).toString());
        }
        session.close();

        //  write("/home/kleopatra/Documents/xml/Capability_Results.xml");
        final String result;
        result = readXml("/home/kleopatra/Documents/"
                + "xml/Capability_Results.xml");

        return result;


    }

    /**
     * This Web function returns all the edges
     * ( including their source and target node)  of the network.
     *
     * @return List of existing  edges.
     */

    @WebMethod
    public final String getEdges() {

        final Session session = getSession();
        session.beginTransaction();
        final List<Edge> edgeList;
        edgeList = new ArrayList<Edge>();
        final Query query = session.createQuery("from EdgeEntity  as edge");
        System.out.println("\n" + "***** Retrieving the Graph Edges *****"
                + "\n");
        //writeGraphML("/home/kleopatra/Documents/xml/Edge_Results.xml");
        final Iterator iterator;
        for (iterator = query.iterate(); iterator.hasNext();) {
            final EdgeEntity edge;
            edge = (EdgeEntity) iterator.next();
            // System.out.println("EDGE ID: " +
            // addEdgeConfig(edge.getId()));
            //  System.out.println("SOURCE NODE:" +
            // addEdgeConfig(edge.getSource()));
            // System.out.println("TARGET NODE: " +
            // addEdgeConfig(edge.getTarget()));
            writeEdge("/home/kleopatra/Documents/xml/Edge_Results.xml",
                    addEdgeConfig(edge.getId()), addConfig(edge.getSource()),
                    addConfig(edge.getTarget()));
        }

        session.close();
        // write("/home/kleopatra/Documents/xml/
        // Edge_Results.xml");
        final String result;
        result = readXml("/home/kleopatra/Documents/"
                + "xml/Edge_Results.xml");
        return result;
    }

    /**
     * This web service takes as input a
     * capability name from the client.
     * It returns the capability description
     * in sensorML.
     *
     * @param capability the capability name.
     * @return a SensorML description of the capability.
     */
    @WebMethod
    public final String getDescription(final String capability) {

        final Session session = getSession();
        session.beginTransaction();
        final List type = session.createQuery("select nodecapability.value "
                + "from NodecapabilityEntity nodecapability "
                + "where nodecapability.name='" + capability + "'  ").list();
        final Query query = session.createQuery(" from DescriptionEntity "
                + "as description "
                + "where description.sensorType='"
                + type.get(0).toString() + "'  ");
        DescriptionEntity description = null;
        final Iterator it;
        for (it = query.iterate(); it.hasNext();) {
            description = (DescriptionEntity) it.next();
        }

        final sweF10field field1;
        field1 = getField(description.getUom(),
                description.getResolution(), "Accuracy", "Accuracy");
        final sweF10field field2;
        field2 = getField(description.getUom(),
                description.getRange(), "Range", "Range");
        final List<sweF10field> fieldList;
        fieldList = new ArrayList<sweF10field>();
        fieldList.add(field1);
        fieldList.add(field2);
        writeSensorML("/home/kleopatra/Documents/xml/Capabilities.xml",
                fieldList, capability, description.getInfo(), capability);

        final String xmlString = readXml("/home/kleopatra/Documents/xml/Capabilities.xml");
        session.close();

        return xmlString;
    }


    /**
     * The Web method getNode takes
     * as input a node name from the client and returns
     * its neighbourhood.
     *
     * @param node name.
     * @return neighbourhood List.
     */

    @WebMethod
    public final String getNode(final String node) {

        final Session session = getSession();
        session.beginTransaction();

        /**
         * tokenize the node id to extract the network id
         * and find the neighbourhood accordingly.
         */
        final String[] tokens = tokenizeString(node);
        final String nodeId = tokens[4] + ":" + tokens[5];
        final String gateway = tokens[4];


        final List nodeIds = session.createQuery("select node.id "
                + "from NodeEntity node where node.gateway='" + gateway + "' "
                + "AND node.id != '" + nodeId + "' ").list();

        // System.out.println("\n" + "***** Finding Nodes Neighbourhood *****"
        // + "\n");
        // System.out.println("Client Sends To
        // Server Node Id: " + node);
        final List<String> neighbour = new ArrayList<String>();
        //writeGraphML("/home/kleopatra/Documents
        // /xml/Neighbourhood_Results.xml");

        //Print the neighbourhood of the selected node.

        for (int i = 0; i < nodeIds.size(); i++) {

            System.out.println("The Neighbour of the "
                    + "received node is :"
                    + addConfig(nodeIds.get(i).toString()));
            neighbour.add(addConfig(nodeIds.get(i).toString()));
            writeNodes("/home/kleopatra/Documents/xml/Neighbourhood_Results.xml",
                    addConfig(nodeIds.get(i).toString()));

        }
        session.close();
        // write("/home/kleopatra/Documents/xml/
        // Neighbourhood_Results.xml");


        final String result = readXml("/home/kleopatra/Documents/xml/Neighbourhood_Results.xml");
        return result;


    }


    /**
     * This Web Method returns All the existing records of our database.
     *
     * @return string
     */

    @WebMethod
    public final String getAll() {

        //use the class that creates GraphML Graph.
        final CreateXML create = new CreateXML();
        final List<NodeCapability> capabilityList = new ArrayList<NodeCapability>();
        final List<EdgeAttribute> attrList = new ArrayList<EdgeAttribute>();
        final List<Node> nodeList = new ArrayList<Node>();
        final List<Edge> edgeList = new ArrayList<Edge>();
        final Session session = getSession();
        session.beginTransaction();

        System.out.println("\n" + "***** Retrieving All "
                + "Existing Records ***** " + "\n");
        final Map metadataMap = session.getSessionFactory()
                .getAllClassMetadata();
        for (Object key : metadataMap.keySet()) {
            final ClassMetadata classMetadata = (ClassMetadata) metadataMap.
                    get(key);
            final String entityName = classMetadata.getEntityName();
            final Query query;
            query = session.createQuery("from " + entityName);
            System.out.println("executing: " + query.getQueryString());


            if (query.getQueryString().equals("from model.NodecapabilityEntity")) {
                final Iterator it;
                for (it = query.iterate(); it.hasNext();) {
                    final NodecapabilityEntity capability = (NodecapabilityEntity) it.next();
                    /**System.out.println("CAPABILITY ID: " +
                     * capability.getId());
                     * System.out.println("CAPABILITY NAME: " +
                     * capability.getName());
                     * System.out.println("CAPABILITY NODE ID: " +
                     * addConfig(capability.getNodeid()));
                     * System.out.println("CAPABILITY VALUE: " +
                     * capability.getValue());
                     */
                    final NodeCapability nodeCapability;
                    nodeCapability = new NodeCapability(capability.getId(),
                            addConfig(capability.getNodeid()),
                            capability.getName(),
                            capability.getValue());
                    capabilityList.add(nodeCapability);
                }
            }
            if (query.getQueryString().equals("from model.EdgeattributeEntity")) {
                final Iterator it;
                for (it = query.iterate(); it.hasNext();) {
                    final EdgeattributeEntity attribute =
                            (EdgeattributeEntity) it.next();
                    // System.out.println("ATTRIBUTE ID: " +
                    // attribute.getId());
                    //  System.out.println("EDGE ID: " +
                    // addConfig(attribute.getEdgeid()));
                    // System.out.println("ATTRIBUTE NAME: " +
                    // attribute.getName());
                    //  System.out.println("ATTRIBUTE VALUE: " +
                    // attribute.getValue());
                    final EdgeAttribute ea1;
                    ea1 = new EdgeAttribute(attribute.getId(),
                            addConfig(attribute.getEdgeid()),
                            attribute.getName(),
                            attribute.getValue());
                    attrList.add(ea1);
                }
            }

            if (query.getQueryString().equals("from model.NodeEntity")) {
                final Iterator it;
                for (it = query.iterate(); it.hasNext();) {
                    final NodeEntity node;
                    node = (NodeEntity) it.next();
                    //System.out.println("NODE ID: " + addConfig(node.getId()));
                    //System.out.println("GATEWAY: " + node.getGateway());
                    //System.out.println("NODE NAME: " + node.getNodeName());
                   // final Node node1 = new Node(capabilityList,
                   //         addConfig(node.getId()));
                   // nodeList.add(node1);
                }
            }
            if (query.getQueryString().equals("from model.EdgeEntity")) {
                final Iterator it;
                for (it = query.iterate(); it.hasNext();) {
                    final EdgeEntity edge = (EdgeEntity) it.next();
                    //System.out.println("EDGE ID: " + addConfig(edge.getId()));
                    //System.out.println("SOURCE NODE:" +
                    // addConfig(edge.getSource()));
                    //System.out.println("TARGET NODE: " +
                    // addConfig(edge.getTarget()));
                    final Edge edge1 = new Edge(addConfig(edge.getId()),
                            addConfig(edge.getSource()),
                            addConfig(edge.getTarget()),
                            attrList);
                    edgeList.add(edge1);
                }
            }

        }
        session.close();

        //create.writeXML("/home/kleopatra/Documents/xml/All_Records_Results.xml",
         //       nodeList, edgeList);
        final String result = readXml("/home/kleopatra/Documents"
                + "/xml/All_Records_Results.xml");
        return result;


    }

    /**
     * This Function combines the results of
     * one or more of the above webservices.
     *
     * @param urn1 : web service name.
     * @param arg1 : web service argumnt.
     * @param urn2 : web service name.
     * @param arg2 : web service argument.
     * @return String with multiple results.
     */

    @WebMethod
    public final String getMultiRecords(final String urn1, final String arg1,
                                        final String urn2, final String arg2) {

        String result1 = null;
        String result2 = null;
        String multiResult;

        if (urn1.equals("getNode")) {
            result1 = getNode(arg1);
        } else if (urn1.equals("getCapabilities")) {

            result1 = getCapabilities(arg1);
        } else if (urn1.equals("getDescription")) {

            result1 = getDescription(arg1);
        } else if (urn1.equals("getEdges")) {
            result1 = getEdges();
        }


        if (urn2.equals("getNode")) {
            result2 = getNode(arg2);
        } else if (urn2.equals("getCapabilities")) {

            result2 = getCapabilities(arg2);
        } else if (urn2.equals("getDescription")) {

            result2 = getDescription(arg2);
        } else if (urn2.equals("getEdges")) {
            result2 = getEdges();
        } else {
            System.out.println(" The URN names inserted are not valid!");
        }

        multiResult = (result1 + result2);
        System.out.println(multiResult);

        return multiResult;
    }


    /**
     * The Main function generates the web service.
     *
     * @param argv
     */
    public static void main(final String[] argv) {
        FindNeighbour.argv = argv;
        final FindNeighbour implementor = new FindNeighbour();
        final String address = "http://localhost:9000/FindNeighbour";
        Endpoint.publish(address, implementor);
    }

}