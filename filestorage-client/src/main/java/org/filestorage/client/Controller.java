package org.filestorage.client;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class Controller implements Initializable {
  @FXML
  protected TableView<FileInfo> fileTable;

  public void upload(ActionEvent e) {
    System.out.println(e.toString());
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    TableColumn<FileInfo, String> filenameColumn = new TableColumn<>("Name");
    filenameColumn.setCellValueFactory(file -> new SimpleStringProperty(file.getValue().getName()));
    filenameColumn.setPrefWidth(256.0);
    
    TableColumn<FileInfo, Long> filesizeColumn = new TableColumn<>("Size");
    
    fileTable.getColumns().addAll(filenameColumn, filesizeColumn);
    filesizeColumn.setCellValueFactory(file -> new SimpleObjectProperty<>(file.getValue().getSize()));
    filesizeColumn.setPrefWidth(256.0);
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
    
    updateFileList(Paths.get("d:/tmp/"));
  }
  
  public void updateFileList(Path path) {
    try {
      fileTable.getItems().clear();
      fileTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! Can't update the file list.", ButtonType.OK);
      alert.showAndWait();
    }
  }

}
