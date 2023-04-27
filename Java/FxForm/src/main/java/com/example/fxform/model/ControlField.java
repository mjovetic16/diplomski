package com.example.fxform.model;

import static com.example.fxform.model.FieldType.CONTROL_FIELD;

public class ControlField extends MarcLine{

    String tag;
    String data;

    public ControlField() {
        super(CONTROL_FIELD);
    }

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
        return "ControlField{" +
                "tag='" + tag + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
