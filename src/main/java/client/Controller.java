package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

	@FXML private Button addButton;
	@FXML private Button removeButton;
	@FXML private Button enterButton;
	@FXML private TextField usernameField;
	@FXML private TextField chatNameField;
	@FXML private ListView<String> roomsListView;
	private String roomName;
	private ViewSwitch viewSwitch;
	private User user;
	private ChatClient chatClient;
	private ObservableList<String> obsList = FXCollections.observableArrayList();
	private ArrayList<String> chatList;

	public Controller(){
		this.chatClient = new ChatClient();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//caricare le chatroom nella listview
		chatList = chatClient.getRoomsList();
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void addRoom(){
		//aggiugere una nuova room nel db
		roomName = chatNameField.getText();
		obsList.removeAll(chatList);
		chatList = chatClient.addRoom(roomName);
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void removeRoom() {
		roomName = chatNameField.getText();
		//rimuovere la room selezionata dal database
		obsList.removeAll(chatList);
		chatList = chatClient.removeRoom(roomName);
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void enterRoom() {
		String username = usernameField.getText();
		user = new User(username);
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
