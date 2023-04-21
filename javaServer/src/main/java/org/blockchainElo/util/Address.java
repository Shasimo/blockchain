package org.blockchainElo.util;

import com.google.gson.Gson;

import java.io.Serializable;

public class Address extends Pair<String, Integer> implements Serializable {
    // this class is functionally useless it is only used for making json more readable
    String ip;
    int port;
    public Address(String ip, int port) {
        super(ip, port);
        this.ip = getFirst();
        this.port = getSecond();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
