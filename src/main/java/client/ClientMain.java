package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class ClientMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("fxml/main_view.fxml"))));
		primaryStage.setTitle("Chat");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
  
}
