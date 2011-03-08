package org.eventroaster.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {

    /**
     * Which {@link Event} to handle
     * @return
     */
    Class<?> event();

    /**
     * Indicated if this {@link EventHandler} should stop other handlers from processing
     * (invoked method must return boolean; must not include priority attribute)
     * @return
     */
    boolean blocker() default false;

    /**
     * Priority of this event handler (must be greater than zero)
     *
     * @return
     */
    int priority() default Integer.MAX_VALUE;
}
