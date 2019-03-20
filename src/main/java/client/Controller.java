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

import static config.ViewConfig.chatRoomView;

public class Controller implements Initializable{

	@FXML private Button enterButton;
	@FXML private TextField usernameField;
	@FXML private TextField chatNameField;
	@FXML private ListView<String> roomsListView;

	private ChatClient chatClient;
	private ObservableList<String> obsList;

	public Controller() throws IOException, TimeoutException {
		this.obsList = FXCollections.observableArrayList();
		this.chatClient = new ChatClient(this);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			chatClient.getRoomsList();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML private void addRoom() throws IOException {
		chatClient.addRoom(getRoomName());
	}

	@FXML private void removeRoom() throws IOException {
		chatClient.removeRoom(getRoomName());
	}

	@FXML private void enterRoom(){
		String username = usernameField.getText();
		String selection = roomsListView.getSelectionModel().getSelectedItem();

		User user = new User(username);

		Scene scene = enterButton.getScene();
		ViewSwitch viewSwitch = new ViewSwitch(chatRoomView, scene);

		Platform.runLater(() -> {
			try {
				viewSwitch.changeToRoomView(user,selection);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void displayRooms(ArrayList<String> chatRooms){
		Platform.runLater(()->{
			obsList.addAll(chatRooms);
			roomsListView.setItems(obsList);
		});
	}

	public void removeFromList(ArrayList<String> chatRooms){
		obsList.removeAll(chatRooms);
	}

	private String getRoomName(){
		return chatNameField.getText();
	}


}
