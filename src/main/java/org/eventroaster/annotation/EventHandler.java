package org.eventroaster.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    Class<?> event();

    boolean blocker() default false;

    /**
     * Priority of this event handler
     * @return
     */
    int priority() default Integer.MAX_VALUE;
}
