package org.eventroaster;

public interface EventService {

    public void fire(Object event);

    public void register(Object listener);

    public void unregister(Object listener);
}
