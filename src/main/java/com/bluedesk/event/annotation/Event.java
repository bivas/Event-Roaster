package com.bluedesk.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    /**
     * Indicate if only a single event of this type should be handled
     * @return
     */
    boolean unique() default false;

}
