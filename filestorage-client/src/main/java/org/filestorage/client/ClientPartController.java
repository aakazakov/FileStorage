package org.filestorage.client;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class ClientPartController implements Initializable {
  
  @FXML
  private TableView<FileInfo> fileTable;
  
  @FXML
  private ComboBox<String> volumesBox;
  
  @FXML
  private TextField pathField;

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
    
    pathField.setEditable(false);
    
    fileTable.getColumns().addAll(filenameColumn, filesizeColumn, lastChangeColumn);
    fileTable.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2) {
        Path path = Paths.get(volumesBox.getSelectionModel().getSelectedItem())
            .resolve(pathField.getText())
            .resolve(fileTable.getSelectionModel().getSelectedItem().getName());
        
        if (Files.isDirectory(path)) {
          updateFileList(path);
        }
      }
    });
    
    volumesBoxInit();
    
    updateFileList(Paths.get(volumesBox.getSelectionModel().getSelectedItem()));
  }

  private void volumesBoxInit() {
    for (Path p : FileSystems.getDefault().getRootDirectories()) {
      volumesBox.getItems().add(p.toString());
    }
    volumesBox.getSelectionModel().select(0);
  }
  
  private void updateFileList(Path path) {
    try {
      pathField.setText(path.toAbsolutePath().normalize().toString().replaceFirst("[A-Za-z]+:\\\\",""));
      fileTable.getItems().clear();
      fileTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.WARNING, "Oops! Can't update the file list.", ButtonType.OK);
      alert.showAndWait();
    }
  }
  
  public void uploadAction(ActionEvent e) {
    System.out.println(e.toString());
  }
  
  public void goUpAction() {
    Path path = Paths.get(getConcatinatedPath()).getParent();
    if (path != null) {
      updateFileList(path);
    }
  }
 
  public void pressEnterAction() {
    updateFileList(Paths.get(getConcatinatedPath()));
    pathField.end();
  }
  
  private String getConcatinatedPath() {
    return volumesBox.getSelectionModel().getSelectedItem() + pathField.getText();
  }
  
  public void selectVolumeAction() {
    updateFileList(Paths.get(volumesBox.getSelectionModel().getSelectedItem()));
  }
}
