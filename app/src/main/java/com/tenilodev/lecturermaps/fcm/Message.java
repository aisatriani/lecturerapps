package com.tenilodev.lecturermaps.fcm;

/**
 * Created by azisa on 2/24/2017.
 */

public class Message {
    String to;
    NotifyData data;

    public Message(String to, NotifyData notification) {
        this.to = to;
        this.data = notification;
    }
}
