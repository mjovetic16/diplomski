package com.example.fxform;

import com.example.fxform.model.JsonMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.util.Scanner;
import com.example.fxform.model.JsonMessage;




public class HelloController {
    public Label textFieldLabel;
    public TextField formField;
    @FXML
    private Label sendFormBtnLabel;
    @FXML
    private Label sendFileFormBtnLabel;

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
            // do something with the JSON object
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
            sendFormBtnLabel.setText("PARSED:"+jsonMessage.getMbody());



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
}