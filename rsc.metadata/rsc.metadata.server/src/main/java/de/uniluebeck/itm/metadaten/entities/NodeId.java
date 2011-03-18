package de.uniluebeck.itm.metadaten.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class NodeId implements Serializable{
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = -7922093913435426044L;
	
	String id;
	String ipAdress;
	public String getId() {
		return id;
	}
	public void setId(final String id) {
		this.id = id;
	}
	public String getIpAdress() {
		return ipAdress;
	}
	public void setIpAdress(final String ipAdress) {
		this.ipAdress = ipAdress;
	}

	public boolean equals(NodeId id){
		boolean result = false;
		if((this.id.equals(id.getId()) && (this.ipAdress.equals(id.getIpAdress())))){
			result = true;
		}		
		return result;
	}
	
	public String toString (){
		return this.id +"|" +this.ipAdress;
	}

}
