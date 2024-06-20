package com.example.xchat;

public class Message {
    // true is from user
    private boolean type;
    // Message content
    private String message;

    public Message(boolean type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean getType() {
        return this.type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public boolean isFromUser() {
        return type;
    }
}
