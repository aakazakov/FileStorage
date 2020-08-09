package org.filestorage.client;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

public class Controller implements Initializable {
  @FXML
  protected TableView<?> fileTable;

  public void upload(ActionEvent e) {
    System.out.println(e.toString());
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }
}
