package org.filestorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage.setScene(new Scene(root, 1024, 512));
    stage.setTitle("File cloud storage.");
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
