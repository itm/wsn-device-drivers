package de.uniluebeck.itm.Datenlogger;

import model.*;
import viewer.CreateXML;
import viewer.StoreToDatabase;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class Datenlogger {
	String port;
	String server;
	String filters;
	String location;
	boolean gestartet = false;

	public Datenlogger(){
		
	}
	
	private String[] parseFilter(String filter){
		return new String[5];
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public void getloggers(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
	}	
	
	public void startlog(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Filter: " + filters);
		System.out.println("Location: " + location);
		gestartet = true;
		System.out.println("\nStarte das Loggen des Knotens....");
		writeToXmlFile();
	}	
	
	public void stoplog(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		gestartet = false;
		System.out.println("\nDas Loggen des Knotens wurde beendet.");
	}
	
	public void addfilter(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
	}
	
	private void writeToXmlFile(){
		//Read the xml file
        CreateXML create = new CreateXML();

        List<Node> nodeList = new ArrayList<Node>();
        List<Link> edgeList = new ArrayList<Link>();
        List<Capability> capList = new ArrayList<Capability>();

        // --- N1 ---
        //Add Node Capabilities

        Capability cap1 = new Capability("urn:wisebed:node:capability:temp", "integer", "lux", 2);
        Capability cap2 = new Capability("urn:wisebed:node:capability:time", "Float", "lux", 0);
        capList.add(cap1);
        capList.add(cap2);

        /**
         * Create all the required elemets for the setup section of a wiseml file.
         */
        Position position = new Position(1.23, 1.56, 1.77);
        Data data = new Data("urn:wisebed:node:capability:time");

        Node node1 = new Node("urn:wisebed:node:tud:M4FTR", position, "gw1", "blinkfast.tnode",
                "fast blinking node", "TNide v4", capList, data);
        nodeList.add(node1);

        Rssi rssi = new Rssi("decimal", "dBam", "-120");

        Link edge = new Link("urn:wisebed:node:tud:330006", "urn:wisebed:node:tud:330009",
                "true", "false", rssi, capList);
        edgeList.add(edge);

        Origin origin = new Origin(position, 5, 0);
        Timeinfo time = new Timeinfo("9/7/2009", "19/12/2010", "seconds");
        NodeDefaults ndefault = new NodeDefaults("node", node1);
        LinkDefaults ldefault = new LinkDefaults("link", edge);
        Setup setup = new Setup(origin, time, "cubic", "wiseml example", ndefault, ldefault, nodeList, edgeList);

        /**
         *  Create all the required elemets for the scenario section of a wiseml file.
         */

        EnableNode enable = new EnableNode("urn:wisebed:node:tud:M4FTR");
        EnableLink enablel = new EnableLink("urn:wisebed:node:tud:33000", "urn:wisebed:node:tud:330009");
        DisableNode disable = new DisableNode("urn:wisebed:node:tud:M4FTR");
        DisableLink disablel = new DisableLink("urn:wisebed:node:tud:33000", "urn:wisebed:node:tud:33000");
        Scenario scenario = new Scenario("scenario_1", "23/09/09", enable, disable, enablel, disablel, node1);
        Trace trace = new Trace("Trace_1","2/5/09", node1, edge);

        /**
         * Create the final wiseml file.
         */
        create.writeXML("test.xml", setup, scenario, trace);
	}
	
	private void writeToDatabase(){
		SessionFactory sessionFactory;
		try {
            sessionFactory = new AnnotationConfiguration().
                    configure("hibernate.cfg.xml").
                    buildSessionFactory();
        }
        catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
		
		StoreToDatabase storeDB = new StoreToDatabase();
        Node node = new Node();
        node.setID("urn:wisebed:node:cti:gw2:n4");
        storeDB.storeNode(node);
        final Session session = sessionFactory.openSession();
        Transaction tr = session.beginTransaction();
        tr.commit();
	}
}
