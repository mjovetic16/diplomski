package com.example.fxform.model;

public class Leader extends MarcLine{

    String data;

    public Leader() {
        super(FieldType.LEADER);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Leader{" +
                "data='" + data + '\'' +
                '}';
    }
}
