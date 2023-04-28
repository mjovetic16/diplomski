package com.example.fxform;

import com.example.fxform.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import com.example.fxform.model.JsonMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HelloController {
    public Label textFieldLabel;
    public TextField formField;
    @FXML
    private Label sendFormBtnLabel;
    @FXML
    private Label sendFileFormBtnLabel;

    @FXML private TreeView<String> modelTreeView;

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize() {
        loadTreeItems("Empty Data");
    }

    // loads some strings into the tree in the application UI.
    public void loadTreeItems(String... rootItems) {
        TreeItem<String> root = new TreeItem<String>("Empty Root Node");
        root.setExpanded(true);

        for (String itemString: rootItems) {
            root.getChildren().add(new TreeItem<String>(itemString));
        }

        modelTreeView.setRoot(root);
    }

    @FXML
    protected void onSendButtonClick() {

        String txt = "Empty line";
        txt = formField.getText();
//        sendFormBtnLabel.setText(txt);


        HttpResponse entityResponse = sendJson(txt+"\\n","http://localhost:3000/parse/line");

        if (entityResponse.getEntity() != null) {
            String content = null;
            try {
                content = EntityUtils.toString(entityResponse.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("The response is:");
            System.out.println("PARSED:"+content);

            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            JsonMessage jsonMessage = gson.fromJson(content, JsonMessage.class);

            System.out.println(jsonMessage);




            sendFormBtnLabel.setText("PARSED:"+jsonMessage.getMbody());


            try {
                MarcLine newMarcLine = parseJsonLine(jsonMessage.getMbody());
                System.out.println("Parsed into marcline object");
                System.out.println(newMarcLine);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }




        }




    }

    public void onChooseButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        String txt = "";

        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();

        File file = fileChooser.showOpenDialog(thisStage);
        if(file!=null){
            Scanner sc = null;
            try {
                sc = new Scanner(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            while (sc.hasNextLine())
                txt+=sc.nextLine()+"\\n";
        }

        HttpResponse entityResponse = sendJson(txt,"http://localhost:3000/parse/file");
        //HttpResponse entityResponse = sendJson(txt,"https://enj6ipg92lgs9.x.pipedream.net/");

        if (entityResponse.getEntity() != null) {
            String content = null;
            try {
                content = EntityUtils.toString(entityResponse.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("The response is:");
            System.out.println(content);


            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            JsonMessage jsonMessage = gson.fromJson(content, JsonMessage.class);
            System.out.println(jsonMessage);
            sendFormBtnLabel.setText("PARSED");




            try {
               Marc newMarc = parseJson(jsonMessage.getMbody());
               System.out.println("Parsed into marc object");
               System.out.println(newMarc);

               loadMarcTree(newMarc);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // do something with the JSON object
        }


    }


    public HttpResponse sendJson(String txt, String path){
        String payload = "{\"name\":\"PARSE REQUEST LINE\",\"mbody\":\""+txt+"\"}";

        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);
        
        HttpResponse response = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(path);
            request.setEntity(entity);
            request.addHeader("Accept", "application/json");
            request.addHeader("Accept-Charset", "utf-8");


            System.out.println("SJ::Entity:"+entity);
            System.out.println("SJ::Request:"+request);
            System.out.println("SJ::Payload:"+payload);

            response = httpClient.execute(request);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    public Marc parseJson(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        Iterator<?> keys = json.keys();

        Marc marcObj = new Marc();

        while(keys.hasNext())
        {
            String key = (String)keys.next();

            switch(key) {
                case "leader":
                    Leader leader = new Leader();
                    leader.setData(json.getString("leader"));
                    marcObj.setLeader(leader);
                    break;
                case "fields":
                    JSONArray fieldArrays = json.getJSONArray("fields");
                    ArrayList<DataField> dataFields = new ArrayList<>();
                    ArrayList<ControlField> controlFields = new ArrayList<>();


                    // iterate across the array of fields and parse control and data fields
                    for (int i=0; i < fieldArrays.length(); i++) {
                        JSONObject arrayElement = fieldArrays.getJSONObject(i);

                        Iterator<?> elementKeys = arrayElement.keys();



                        boolean isDataField = false;

                        if(arrayElement.has("ind1")) {
                            isDataField = true;
                            System.out.println("found the datafield");
                        }


                        if(isDataField){
                            DataField dataField = new DataField();
                            ArrayList<SubField> subFieldArrayList = new ArrayList<>();




                            dataField.setIndex1(arrayElement.getString("ind1"));
                            dataField.setIndex2(arrayElement.getString("ind2"));

                            //Iterate across the dataField fields (by key), and parse the elements
                            while(elementKeys.hasNext()){
                                String elementKey = (String)elementKeys.next();

                                if(elementKey.equals("ind1")||elementKey.equals("ind2"))
                                    continue;

                                dataField.setTag(elementKey);
                                JSONObject jsonDataFieldBody = arrayElement.getJSONObject(elementKey);


                                JSONArray jsonSubsArray = jsonDataFieldBody.getJSONArray("subfields");


                                //Iterate across the subfields and parse them
                                for (int j=0; j < jsonSubsArray.length(); j++) {

                                    JSONObject jsonSubElement = jsonSubsArray.getJSONObject(j);
                                    SubField subField = new SubField();

                                    Iterator<?> subElementKeys = jsonSubElement.keys();
                                    String subElementKey = (String)subElementKeys.next();
                                    subField.setTag(subElementKey);
                                    subField.setData(jsonSubElement.getString(subElementKey));

                                    subFieldArrayList.add(subField);

                                }



                            }

                            dataField.setSubFieldList(subFieldArrayList);
                            dataFields.add(dataField);

                        }else{
                            ControlField controlField = new ControlField();
                            String elementKey = (String)elementKeys.next();
                            controlField.setTag(elementKey);
                            controlField.setData(arrayElement.getString(elementKey));

                            controlFields.add(controlField);
                        }

                    }

                    marcObj.setControlFieldList(controlFields);
                    marcObj.setDataFieldList(dataFields);


                    break;
                default:
            }
        }

        return marcObj;
    }


    public MarcLine parseJsonLine(String jsonString) throws JSONException {

        JSONObject json = new JSONObject(jsonString);
        Iterator<?> keys = json.keys();

        MarcLine marcLine = null;

        switch (json.getString("type")){

            case "LEADER":
                Leader leader = new Leader();
                JSONObject jsonObject = json.getJSONObject("data");
                leader.setData(jsonObject.getString("leader"));
                marcLine = leader;
                break;

            case "DATA_FIELD":
                DataField dataField = new DataField();
                JSONObject jsonObjectData = json.getJSONObject("data");

                ArrayList<SubField> subFieldArrayList = new ArrayList<>();




                dataField.setIndex1(jsonObjectData.getString("ind1"));
                dataField.setIndex2(jsonObjectData.getString("ind2"));

                //Iterate across the dataField fields (by key), and parse the elements
                Iterator<?> elementKeys = jsonObjectData.keys();
                while(elementKeys.hasNext()){
                    String elementKey = (String)elementKeys.next();

                    if(elementKey.equals("ind1")||elementKey.equals("ind2"))
                        continue;

                    dataField.setTag(elementKey);
                    JSONObject jsonDataFieldBody = jsonObjectData.getJSONObject(elementKey);


                    JSONArray jsonSubsArray = jsonDataFieldBody.getJSONArray("subfields");


                    //Iterate across the subfields and parse them
                    for (int j=0; j < jsonSubsArray.length(); j++) {

                        JSONObject jsonSubElement = jsonSubsArray.getJSONObject(j);
                        SubField subField = new SubField();

                        Iterator<?> subElementKeys = jsonSubElement.keys();
                        String subElementKey = (String)subElementKeys.next();
                        subField.setTag(subElementKey);
                        subField.setData(jsonSubElement.getString(subElementKey));

                        subFieldArrayList.add(subField);

                    }



                }

                dataField.setSubFieldList(subFieldArrayList);

                marcLine = dataField;
                break;

            case "CONTROL_FIELD":
                ControlField controlField = new ControlField();
                JSONObject jsonObjectControl = json.getJSONObject("data");

                Iterator<?> elementKeysControl = jsonObjectControl.keys();
                String elementKey = (String)elementKeysControl.next();
                controlField.setTag(elementKey);
                controlField.setData(jsonObjectControl.getString(elementKey));

                marcLine =  controlField;
                break;

        }


        return marcLine;
    }

    public void loadMarcTree(Marc marc){

        TreeItem<String> root = new TreeItem<String>("MARC");

        if(marc.getLeader()!=null){
            TreeItem<String> leader = new TreeItem<String>("Leader");
            TreeItem<String> leaderData = new TreeItem<>(marc.getLeader().getData());

            leader.getChildren().add(leaderData);
            root.getChildren().add(leader);
        }

        ArrayList<ControlField> controlFields = new ArrayList<>();
        ArrayList<DataField> dataFields = new ArrayList<>();

        if(marc.getControlFieldList().size()>0){
            controlFields = (ArrayList<ControlField>) marc.getControlFieldList();
        }

        if(marc.getDataFieldList().size()>0){
            dataFields = (ArrayList<DataField>) marc.getDataFieldList();
        }


        TreeItem<String> controlRoot = new TreeItem<>("Control Fields");
        root.getChildren().add(controlRoot);

        for(ControlField controlField:controlFields){
            TreeItem<String> controlItem = new TreeItem<>();
            TreeItem<String> controlItemData = new TreeItem<>();


            controlItem.setValue(controlField.getTag());

            controlItemData.setValue(controlField.getData());


            controlItem.getChildren().add(controlItemData);

            controlRoot.getChildren().add(controlItem);

        }

        TreeItem<String> dataRoot = new TreeItem<>("Data Fields");
        root.getChildren().add(dataRoot);

        for(DataField dataField:dataFields){

            TreeItem<String> dataItem = new TreeItem<>(dataField.getTag());
            TreeItem<String> dataItemIndex1 = new TreeItem<>("IND1: "+dataField.getIndex1());
            TreeItem<String> dataItemIndex2 = new TreeItem<>("IND2: "+dataField.getIndex2());

            TreeItem<String> dataItemSubRoot = new TreeItem<>("Subfields");

            for(SubField subField:dataField.getSubFieldList()){

                TreeItem<String> dataItemSubField = new TreeItem<>(subField.getTag());
                TreeItem<String> dataItemSubFieldData = new TreeItem<>(subField.getData());

                dataItemSubRoot.getChildren().add(dataItemSubField);
                dataItemSubField.getChildren().add(dataItemSubFieldData);

            }

            dataRoot.getChildren().add(dataItem);
            dataItem.getChildren().add(dataItemIndex1);
            dataItem.getChildren().add(dataItemIndex2);
            dataItem.getChildren().add(dataItemSubRoot);

        }


        modelTreeView.setRoot(root);

    }


}