package com.example.ewtapp;

public class Alert {
    private String id;
    private String type;
    private String message;

    public Alert() {
        // Default constructor required for calls to DataSnapshot.getValue(Alert.class)
    }

    public Alert(String id, String type, String message) {
        this.id = id;
        this.type = type;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
