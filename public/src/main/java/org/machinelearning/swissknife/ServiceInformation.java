package org.machinelearning.swissknife;

import java.util.Objects;

public class ServiceInformation {
    private final String host;
    private final String port;
    private final String pid;

    public ServiceInformation(String host, String port) {
        this(host, port, null);
    }

    public ServiceInformation(String host, String port, String pid) {
        this.host = host;
        this.port = port;
        this.pid = pid;
    }

    public String getPid() {
        return pid;
    }

    public String getUrl() {
        return "http://" + host + ":" + port + "/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInformation that = (ServiceInformation) o;
        return Objects.equals(host, that.host) &&
                Objects.equals(port, that.port) &&
                Objects.equals(pid, that.pid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, pid);
    }
}
