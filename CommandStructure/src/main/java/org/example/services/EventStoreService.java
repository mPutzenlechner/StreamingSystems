package org.example.services;

import org.example.events.IEvent;

public class EventStoreService {
    public static EventStoreService instance = new EventStoreService();

    public static EventStoreService getInstance() {
        return instance;
    }

    public void raiseEvent(IEvent event) {

    }
}
