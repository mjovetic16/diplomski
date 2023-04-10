module com.example.fxform {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires com.google.gson;

    opens com.example.fxform to javafx.fxml, com.google.gson;
    opens com.example.fxform.model to com.google.gson;

    exports com.example.fxform;
    exports com.example.fxform.model to  com.google.gson;
}