module com.example.csvfiledisplay {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.csvfiledisplay to javafx.fxml;
    exports com.example.csvfiledisplay;
}