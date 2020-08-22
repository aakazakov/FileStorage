package org.filestorage.client.fxcontrollers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.filestorage.client.entity.FileInfo;
import org.filestorage.client.network.Interaction;
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
    fileTable.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        Path path = Paths.get(pathField.getText())
            .resolve(fileTable.getSelectionModel().getSelectedItem().getName());
        
        if (Files.isDirectory(path)) {
          updateFileList(path);
        }
      }
    });
    
    pathField.setEditable(false);
    
    updateFileList(Paths.get(START_DIR));
  }
  
  public void updateFileList(Path path) {
    try {
      pathField.setText(path.toAbsolutePath().normalize().toString());
      fileTable.getItems().clear();
      fileTable.getItems().addAll(
          Files
          .list(path)
          .filter(Files::isReadable)
          .map(FileInfo::new)
          .collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! Can't update the file list.", ButtonType.OK);
      alert.showAndWait();
    }
  }
  
  public void goUpAction() {
    Path path = Paths.get(pathField.getText()).getParent();
    if (path != null) {
      updateFileList(path);
    }
  }
  
  public void downloadAction(ActionEvent event) {
    try {
      new Interaction().getFileList();
    } catch (IOException | OnServerException | ClassNotFoundException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! Can't get file list.", ButtonType.OK);
      alert.showAndWait();
    }
  }
  
  public void removeAction(ActionEvent e) {
    System.out.println(e.toString());
  }
  
  public void createDirAction(ActionEvent e) {
    System.out.println(e.toString());
  }
}
