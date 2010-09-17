package de.uniluebeck.itm.overlaynet;



import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.Serializer;

public class WriteXML {

	public static void main (String[] args) {

		MetaDatenList example = new MetaDatenList();
		Metadata data1 = new Metadata();
		Metadata data2 = new Metadata();
		List <Metadata> liste = new ArrayList <Metadata> ();
		data1.setId(54321);
		data1.setFabricate("Jennec");
//		data1.setHost("ITM");
		data1.setIpadress("192.168.8.2");
		data1.setOsversion("1.0");
		liste.add(data1);
		data2.setId(9876);
		data2.setFabricate("Telios");
//		data2.setHost("Spanien");
		data2.setIpadress("101.168.8.2");
		data2.setOsversion("1.2");
		liste.add(data2);
		example.setList(liste);
		new WriteXML().writeXMLtoFile("example.xml", example);
	}
	
	public int writeXMLtoFile (String filename, MetaDatenList example)
	{
		File result = new File(filename);
		Serializer serial=new Persister();
		try {
			serial.write(example, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
