package de.uniluebeck.itm.datenlogger;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Capability;
import model.Data;
import model.DisableLink;
import model.DisableNode;
import model.EnableLink;
import model.EnableNode;
import model.Link;
import model.LinkDefaults;
import model.Node;
import model.NodeDefaults;
import model.Origin;
import model.Position;
import model.Rssi;
import model.Scenario;
import model.Setup;
import model.Timeinfo;
import model.Trace;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

import com.google.common.base.Predicate;

import viewer.CreateXML;
import viewer.StoreToDatabase;

public class Datenlogger {
	
	String port;
	String server;
	String klammer_filter;
	String regex_filter;
	String location;
	boolean gestartet = false;

	public Datenlogger(){
		
	}
	
	public Predicate<CharSequence> parse_klammer_filter(String klammer_filter){
		Stack<Predicate<CharSequence>> ausdruecke = new Stack<Predicate<CharSequence>>();
		Stack<String> operatoren = new Stack<String>();
		String ausdruck = "";
		for(int i = 0; i < klammer_filter.length(); i++){
			String character = Character.toString(klammer_filter.charAt(i));
			if(character.equals("|")){
				operatoren.push("or");
			}else if(character.equals("&")){
				operatoren.push("and");
			}else if(character.equals("(")){
				//do nothing
			}else if(character.equals(")")){
				if(!ausdruck.equals("")){
					System.out.println(ausdruck);
					Predicate<CharSequence> predicate = new Klammer_Predicate(ausdruck);
					ausdruecke.push(predicate);
					ausdruck = "";
				}else{
					Predicate<CharSequence> erster_ausdruck = ausdruecke.pop();
					Predicate<CharSequence> zweiter_ausdruck = ausdruecke.pop();
					String operator = operatoren.pop();
					if(operator.equals("or")){
						Predicate<CharSequence> ergebnis = or(erster_ausdruck, zweiter_ausdruck);
						ausdruecke.push(ergebnis);
					}else if(operator.equals("and")){
						Predicate<CharSequence> ergebnis = and(erster_ausdruck, zweiter_ausdruck);
						ausdruecke.push(ergebnis);
					}
				}
			}else{
				ausdruck = ausdruck + character;
			}			
		}
		while(operatoren.size() != 0){
			Predicate<CharSequence> erster_ausdruck = ausdruecke.pop();
			Predicate<CharSequence> zweiter_ausdruck = ausdruecke.pop();
			String operator = operatoren.pop();
			if(operator.equals("or")){
				Predicate<CharSequence> ergebnis = or(erster_ausdruck, zweiter_ausdruck);
				ausdruecke.push(ergebnis);
			}else if(operator.equals("and")){
				Predicate<CharSequence> ergebnis = and(erster_ausdruck, zweiter_ausdruck);
				ausdruecke.push(ergebnis);
			}
		}	
		return ausdruecke.pop();
	}
	
	public void setPort(String port) {
		this.port = port;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean isGestartet() {
		return gestartet;
	}

	public void setGestartet(boolean gestartet) {
		this.gestartet = gestartet;
	}

	public void setKlammer_filter(String klammer_filter) {
		this.klammer_filter = klammer_filter;
	}

	public void setRegex_filter(String regex_filter) {
		this.regex_filter = regex_filter;
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
		new Listener(this).start();
		
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		System.out.println("Klammer-Filter: " + klammer_filter);
		System.out.println("Regex-Filter: " + regex_filter);
		System.out.println("Location: " + location);
		
		gestartet = true;
		System.out.println("\nStarte das Loggen des Knotens....");
		String erhaltene_Daten = "5";
		
		//Filtern
		boolean matches = false;
		
		//(Datentyp, Beginn, Wert)-Filter
		if(klammer_filter != null){
			matches = parse_klammer_filter(klammer_filter).apply(erhaltene_Daten);
		}
			
		//Reg-Ausdruck-Filter
		//"[+-]?[0-9]+"
		if(regex_filter != null){
			Pattern p = Pattern.compile(regex_filter);
			Matcher m = p.matcher(erhaltene_Daten);
			matches = m.matches();
		}	
		
		if(!matches){
			//logge Daten
			System.out.println("Daten werden geloggt.");
			//writeToDatabase();
			//writeToXmlFile();
		}
		else{
			System.out.println("Daten werden nicht geloggt.");
		}

		try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
	}	
	
	public void stoplog(){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		System.out.println("Server: " + server);
		gestartet = false;
		System.out.println("\nDas Loggen des Knotens wurde beendet.");
	}
	
	public void add_klammer_filter(String filter){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		klammer_filter = klammer_filter + filter;
		System.out.println("Klammer-Filter: " + klammer_filter);
		System.out.println("Filter hinzugefuegt");
	}
	
	public void add_regex_filter(String filter){
		System.out.println("Parameter:");
		System.out.println("Port: " + port);
		regex_filter = regex_filter + filter;
		System.out.println("Regex-Filter: " + regex_filter);
		System.out.println("Filter hinzugefuegt");
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
