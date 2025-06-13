package ro.mpp2024.network;

import java.io.Serializable;

public class Message implements Serializable {
    private String type; // Tipul mesajului (ex. LOGIN, SEARCH_TRIP)
    private Object payload; // Datele asociate mesajului

    public Message(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}