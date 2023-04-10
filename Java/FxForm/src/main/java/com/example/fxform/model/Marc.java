package com.example.fxform.model;

import java.util.ArrayList;
import java.util.List;

public class Marc {

    String leader;

    List<ControlField> controlFieldList;

    List<DataField> dataFieldList;



    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
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
}
