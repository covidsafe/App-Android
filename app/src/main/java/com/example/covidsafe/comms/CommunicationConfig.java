package com.example.covidsafe.comms;

public class CommunicationConfig {
    private String host;
    private int port;
    private String clientId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public CommunicationConfig(String host, int port, String clientId) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
    }
}
