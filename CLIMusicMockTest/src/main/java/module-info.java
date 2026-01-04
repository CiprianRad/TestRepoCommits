module com.example.climusicmocktest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.climusicmocktest to javafx.fxml;
    opens com.example.climusicmocktest.domain to javafx.base;
    exports com.example.climusicmocktest;
    exports com.example.climusicmocktest.domain;
}