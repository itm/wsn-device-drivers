package de.uniluebeck.itm.overlaynet;



import java.io.File;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.Serializer;

public class XMLLaden {

	public static void main (String[] args) {

		MetaDatenList example = new MetaDatenList();
		Serializer serializer = new Persister();
		File source = new File("example.xml");

		 try {
			example = serializer.read(MetaDatenList.class, source);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Ausgabeder Liste: " +example.getList().get(0).getIpadress());
		
	}

}
