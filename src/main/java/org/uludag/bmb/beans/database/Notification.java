package org.uludag.bmb.beans.database;

public class Notification {
    private String message;

    public Notification() {

    }

    public Notification(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
