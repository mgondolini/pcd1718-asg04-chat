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
import java.util.concurrent.TimeoutException;

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
		try {
			this.chatClient = new ChatClient();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//caricare le chatroom nella listview
		try {
			chatList = chatClient.getRoomsList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void addRoom() throws IOException {
		//aggiugere una nuova room nel db
		roomName = chatNameField.getText();
		obsList.removeAll(chatList);
		chatList = chatClient.addRoom(roomName);
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void removeRoom() throws IOException {
		roomName = chatNameField.getText();
		//rimuovere la room selezionata dal database
		obsList.removeAll(chatList);
		chatList = chatClient.removeRoom(roomName);
		obsList.addAll(chatList);
		roomsListView.setItems(obsList);
	}

	@FXML private void enterRoom() throws IOException {
		String username = usernameField.getText();
		String selection = roomsListView.getSelectionModel().getSelectedItem();

		user = new User(username);
		user.setUsername(username);


		String view = "fxml/chatroom_view.fxml";
		Scene scene = enterButton.getScene();

		viewSwitch = new ViewSwitch(view, scene);
		chatClient.setRoom(selection);
		chatClient.setUser(user);

		Platform.runLater(() -> {
			try {
				viewSwitch.changeView();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}


}
