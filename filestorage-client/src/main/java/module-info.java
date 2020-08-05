module org.filestorage.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.filestorage.client to javafx.fxml;
    exports org.filestorage.client;
}