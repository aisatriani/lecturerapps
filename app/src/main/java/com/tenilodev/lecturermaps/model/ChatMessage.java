package com.tenilodev.lecturermaps.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by azisa on 2/21/2017.
 */

public class ChatMessage implements Serializable {
    private String messageText;
    private String messageUser;
    private String pengirimID;
    private String penerimaID;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String pengirimID, String penerimaID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.pengirimID = pengirimID;
        this.penerimaID = penerimaID;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getPengirimID() {
        return pengirimID;
    }

    public void setPengirimID(String pengirimID) {
        this.pengirimID = pengirimID;
    }

    public String getPenerimaID() {
        return penerimaID;
    }

    public void setPenerimaID(String penerimaID) {
        this.penerimaID = penerimaID;
    }
}
