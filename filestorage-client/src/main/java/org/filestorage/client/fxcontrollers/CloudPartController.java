package org.filestorage.client.fxcontrollers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.filestorage.client.network.Interaction;
import org.filestorage.common.entity.FileInfo;
import org.filestorage.common.entity.FileList;
import org.filestorage.common.exceptions.OnServerException;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class CloudPartController implements Initializable {
  private final String START_DIR = "./CLOUD_TMP";
  
  @FXML
  protected TableView<FileInfo> fileTable;
  
  @FXML
  protected TextField pathField;

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Name");
    filenameColumn.setPrefWidth(256.0);
    filenameColumn.setCellValueFactory(value -> new SimpleStringProperty(value.getValue().getName()));
    
    TableColumn<FileInfo, Long> filesizeColumn = new TableColumn<>("Size");    
    filesizeColumn.setPrefWidth(128.0);
    filesizeColumn.setCellValueFactory(value -> new SimpleObjectProperty<>(value.getValue().getSize()));
    filesizeColumn.setCellFactory(value -> new TableCell<FileInfo, Long>() {
        @Override
        protected void updateItem(Long item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
            setStyle(null);
          } else {
            String text = String.format("%,d bytes", item);
            if (item == -1L) text = "dir";
            setText(text);
          }
        }
      }
    );
    
    TableColumn<FileInfo, String> lastChangeColumn = new TableColumn<>("Last change");
    lastChangeColumn.setPrefWidth(128.0);
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd hh:MM:ss");
    lastChangeColumn.setCellValueFactory(value ->
      new SimpleStringProperty(value.getValue().getLastModified().format(dtf)));
    
    fileTable.getColumns().addAll(filenameColumn, filesizeColumn, lastChangeColumn);
    
    pathField.setEditable(false);
    
    updateFileList();
  }
  
  public void updateFileList() {
    try {      
      FileList fileList = new Interaction().getFileList();
      pathField.setText(fileList.getRoot());
      fileTable.getItems().addAll(fileList.getList());
    } catch (IOException | OnServerException | ClassNotFoundException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! Can't update the file list.", ButtonType.OK);
      alert.showAndWait();
    }
  }
  
  public void downloadAction(ActionEvent event) {
    System.out.println(event.toString());
  }
  
  public void removeAction(ActionEvent event) {
    System.out.println(event.toString());
  }
}
