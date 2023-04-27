package com.example.fxform.model;

import java.util.ArrayList;
import java.util.List;

public class Marc {

    Leader leader;

    List<ControlField> controlFieldList;

    List<DataField> dataFieldList;


    public Leader getLeader() {
        return leader;
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    public List<ControlField> getControlFieldList() {
        return controlFieldList;
    }

    public void setControlFieldList(List<ControlField> controlFieldList) {
        this.controlFieldList = controlFieldList;
    }

    public List<DataField> getDataFieldList() {
        return dataFieldList;
    }

    public void setDataFieldList(List<DataField> dataFieldList) {
        this.dataFieldList = dataFieldList;
    }


    @Override
    public String toString() {
        return "Marc{" +
                "leader='" + leader + '\'' +
                ", controlFieldList=" + controlFieldList +
                ", dataFieldList=" + dataFieldList +
                '}';
    }
}
