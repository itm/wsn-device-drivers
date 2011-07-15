package de.uniluebeck.itm.wsn.drivers.core.operation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Annotation for setting the maximum length of an message send via the OutputStream.
 * 
 * @author Malte Legenhausen
 */
@BindingAnnotation 
@Target({ FIELD, PARAMETER, METHOD }) 
@Retention(RUNTIME)
public @interface MaxMessageLength {

}
