package de.uniluebeck.itm.tcp.server.utils;

/**
 * Type of a Operation. 
 * @author Andreas Maier
 *
 */
public enum OperationType {

	/**
	 * write-Mode
	 */
	WRITEOPERATION("write",0),
	/**
	 * read-Mode
	 */
	READOPERATION("read",1);
	
	/**
	 * The name of the Operation
	 */
	private String name;
	/**
	 * The short representation of the Operation
	 */
	private int type;
	
	/**
	 * Constructor
	 * @param name The name of the Operation
	 * @param type The short representation of the Operation
	 */
	private OperationType(final String name, final int type){
		this.name = name;
		this.type = type;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getType(){
		return this.type;
	}
}
