package de.uniluebeck.itm.wsn.drivers.core.serialport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to which a method in programming mode.
 * When the method is entered the programming mode can be entered via the <code>AbstractProgramInterceptor</code>
 * When the method is finished the programming mode will be automatically left.
 * 
 * @author Malte Legenhausen
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface ProgrammingMode {
	
}
