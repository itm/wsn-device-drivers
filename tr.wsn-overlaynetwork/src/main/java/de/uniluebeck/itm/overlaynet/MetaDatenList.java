package de.uniluebeck.itm.overlaynet;



import java.util.List;

import org.simpleframework.xml.*;

public class MetaDatenList {
		
	   @ElementList
	   private List<Metadata> list;

	public List<Metadata> getList() {
		return list;
	}

	public void setList(List<Metadata> list) {
		this.list = list;
	}



}
