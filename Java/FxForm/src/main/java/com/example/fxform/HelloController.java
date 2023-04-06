package com.example.fxform;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;


public class HelloController {
    public Label textFieldLabel;
    public TextField formField;
    @FXML
    private Label sendFormBtnLabel;

    @FXML
    protected void onSendButtonClick() {

        String txt = "Empty line";
        txt = formField.getText();
//        sendFormBtnLabel.setText(txt);

        String payload = "{\"name\":\"PARSE REQUEST LINE\",\"mbody\":\""+txt+"\\n"+"\"}";

        StringEntity entity = new StringEntity(payload,
                ContentType.APPLICATION_JSON);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://localhost:3000/parseline");
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entityResponse = response.getEntity();


            if (entityResponse != null) {
                String content = EntityUtils.toString(entityResponse);
                System.out.println("The response is:");
                System.out.println("PARSED:"+content);
                sendFormBtnLabel.setText("PARSED:"+content);
                // do something with the JSON object
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}