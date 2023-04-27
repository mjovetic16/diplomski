package com.example.fxform.model;

public class SubField {

    String tag;
    String data;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SubField{" +
                "tag='" + tag + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
