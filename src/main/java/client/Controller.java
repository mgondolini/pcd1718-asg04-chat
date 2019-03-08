package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

	@FXML Button addButton;
	@FXML Button removeButton;
	@FXML Button enterButton;
	@FXML TextField usernameField;
	@FXML TextField chatNameField;
	@FXML ListView<String> roomsListView;
	private String chatName;
	private ViewSwitch viewSwitch;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//caricare le chatroom nella listview
	}

	@FXML private void addRoom(){
		//aggiugere una nuova room nel db
		chatName = chatNameField.getText();
	}

	@FXML private void removeRoom(){
		chatName = chatNameField.getText();
		//rimuovere la room selezionata dal database
	}

	@FXML private void enterRoom() {
		String username = usernameField.getText();
		//entrare nella chatroom selezionata (controllo)
		//cambiare view

		String view = "fxml/chatroom_view.fxml";
		Scene scene = enterButton.getScene();

		viewSwitch = new ViewSwitch(view, scene);
		Platform.runLater(() -> {
			try {
				viewSwitch.changeView();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}


}
