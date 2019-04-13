package controller;

import client.User;
import client.ViewSwitch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import services.ChatRoomClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import static config.ViewConfig.mainView;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.Alert.AlertType.ERROR;

/**
 * @author Monica Gondolini
 */
public class ChatRoomController implements Initializable {

	@FXML private Button quitButton;
	@FXML private TextField messageField;
	@FXML private TextArea messagesArea;
	@FXML private Label chatRoomLabel;

	private ViewSwitch viewSwitch;
	private ChatRoomClient chatRoomClient;
	private User user;
	private String username;
	private String room;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			username = getUser().getUsername();
			chatRoomLabel.setText(getRoom());
			try {
				chatRoomClient = new ChatRoomClient(this, getRoom());
				chatRoomClient.sendMessage(" joined the room.", username);
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML private void sendMessage() throws IOException {
		String message = messageField.getText();
		if(message.equals("")){
			Alert alert = new Alert(ERROR, "Cannot send an empty message", ButtonType.OK);
			alert.showAndWait();
		}else {
			chatRoomClient.sendMessage(message, username);
			messageField.clear();
		}
	}

	@FXML private void quitChat() throws IOException {
		Alert alert = new Alert(CONFIRMATION, "Are you sure you want to leave?",  ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {
			String message = " left the room.";
			chatRoomClient.sendMessage(message, username);

			Scene scene = quitButton.getScene();
			viewSwitch = new ViewSwitch(mainView, scene);
			Platform.runLater(() -> {
				try {
					viewSwitch.changeView();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void receiveMessage(String message) {
		messagesArea.appendText(message + "\n");
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	private User getUser() {
		return user;
	}

	private String getRoom() {
		return room;
	}


}
