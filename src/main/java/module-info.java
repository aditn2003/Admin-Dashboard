module com.example.graphsdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens com.example.graphsdemo to javafx.fxml;
    exports com.example.graphsdemo;
}