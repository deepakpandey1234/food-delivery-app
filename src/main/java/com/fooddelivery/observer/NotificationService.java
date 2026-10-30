package com.fooddelivery.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Stand-in for SMS / push / email. In production: publish to Kafka, consumers send. */
public class NotificationService {
    private final List<String> sent = new CopyOnWriteArrayList<>();

    public void send(String recipient, String message) {
        String line = "[" + recipient + "] " + message;
        sent.add(line);
        System.out.println(line);
    }

    public List<String> getSent() { return List.copyOf(sent); }
}
