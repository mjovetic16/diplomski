module com.example.fxform {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;

    opens com.example.fxform to javafx.fxml;
    exports com.example.fxform;
}