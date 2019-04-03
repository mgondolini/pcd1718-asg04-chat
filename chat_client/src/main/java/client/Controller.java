package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import static config.ViewConfig.chatRoomView;
import static javafx.scene.control.Alert.AlertType.NONE;

/**
 * @author Monica Gondolini
 */
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
		if(chatNameField.getText().isEmpty())
			showDialog("Empty room field.");
		else
			chatClient.addRoom(getRoomName().get());
	}

	@FXML private void removeRoom() throws IOException {
		if(chatNameField.getText().isEmpty())
			showDialog("Empty room field.");
		else
			chatClient.removeRoom(getRoomName().get());
	}

	@FXML private void enterRoom(){
		String username = usernameField.getText();
		String selection = roomsListView.getSelectionModel().getSelectedItem();

		User user = new User(username);

		Scene scene = enterButton.getScene();
		ViewSwitch viewSwitch = new ViewSwitch(chatRoomView, scene);

		if(roomsListView.getSelectionModel().selectedItemProperty().isNull().get()) {
			showDialog("Select room");
		}else if(user.getUsername().equals("")){
			showDialog("Insert username");
		} else {
			Platform.runLater(() -> {
				try {
					viewSwitch.changeToRoomView(user, selection);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void displayRooms(ArrayList<String> chatRooms){
		Platform.runLater(()->{
			obsList.setAll(chatRooms);
			roomsListView.setItems(obsList);
		});
	}

	public void showDialog(String text){
		Alert alert = new Alert(NONE, text, ButtonType.OK);
		alert.showAndWait();
	}

	public void removeFromObsList(ArrayList<String> chatRooms){
		obsList.removeAll(chatRooms);
	}

	private Optional<String> getRoomName(){
		return Optional.of(chatNameField.getText());
	}

}
