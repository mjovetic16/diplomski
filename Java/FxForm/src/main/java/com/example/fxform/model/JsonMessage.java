package com.example.fxform.model;

public class JsonMessage {

    String name;
    String mbody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMbody() {
        return mbody;
    }

    public void setMbody(String mbody) {
        this.mbody = mbody;
    }

    @Override
    public String toString() {
        return "JsonMessageTest{" +
                "name='" + name + '\'' +
                ", mbody='" + mbody + '\'' +
                '}';
    }
}
