package com.example.fxform.model;

import java.util.List;

import static com.example.fxform.model.FieldType.DATA_FIELD;

public class DataField extends MarcLine{

 String tag;

 List<SubField> subFieldList;

 String index1;

 String index2;

 public DataField() {
  super(FieldType.DATA_FIELD);
 }

 public String getTag() {
  return tag;
 }

 public void setTag(String tag) {
  this.tag = tag;
 }

 public List<SubField> getSubFieldList() {
  return subFieldList;
 }

 public void setSubFieldList(List<SubField> subFieldList) {
  this.subFieldList = subFieldList;
 }

 public String getIndex1() {
  return index1;
 }

 public void setIndex1(String index1) {
  this.index1 = index1;
 }

 public String getIndex2() {
  return index2;
 }

 public void setIndex2(String index2) {
  this.index2 = index2;
 }


 @Override
 public String toString() {
  return "DataField{" +
          "tag='" + tag + '\'' +
          ", subFieldList=" + subFieldList +
          ", index1='" + index1 + '\'' +
          ", index2='" + index2 + '\'' +
          '}';
 }
}
