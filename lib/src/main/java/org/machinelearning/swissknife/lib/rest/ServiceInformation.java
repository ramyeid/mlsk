package org.machinelearning.swissknife.lib.rest;

public class ServiceInformation {
    private final String host;
    private final String port;

    public ServiceInformation(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public String getUrl() {
        return "http://" + host + ":" + port + "/";
    }
}
