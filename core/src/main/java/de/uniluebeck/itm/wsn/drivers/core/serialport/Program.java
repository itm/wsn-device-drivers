package de.uniluebeck.itm.wsn.drivers.core.serialport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Program annotation.
 * 
 * @author Malte Legenhausen
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface Program {
	
}
