package de.uniluebeck.itm.wsn.drivers.core.concurrent;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Binding that is mainly used for objects that are involved in the idle task.
 * 
 * @author Malte Legenhausen
 */
@BindingAnnotation 
@Target({ FIELD, PARAMETER, METHOD }) 
@Retention(RUNTIME)
public @interface Idle {

}
