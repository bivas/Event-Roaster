package org.eventroaster;

public interface EventService {

    void fire(Object event);

    void register(Object listener);

    void unregister(Object listener);
}
