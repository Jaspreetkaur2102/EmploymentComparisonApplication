module com.example.employmentcomparisonapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.employmentcomparisonapplication to javafx.fxml;
    exports com.example.employmentcomparisonapplication;
}