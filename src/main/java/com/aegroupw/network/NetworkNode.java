package com.aegroupw.network;

import java.util.Objects;

public class NetworkNode extends Object {
    private final int number;
    private final NetworkNodeType type;

    public NetworkNode(int number, NetworkNodeType type) {
        this.number = number;
        this.type = type;
    }

    public int getNumber() {
        return number;
    }

    public NetworkNodeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NetworkNode)) return false;
        NetworkNode that = (NetworkNode) o;
        return number == that.number && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, type);
    }

    @Override
    public String toString() {
        return "NetworkNode{" + "number=" + number + ", type=" + type + '}';
    }
}
