package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeoutException;

import static config.ViewConfig.mainView;

public class ChatRoomController implements Initializable {

	@FXML private Button quitButton;
	@FXML private TextField messageField;
	@FXML private TextArea messagesArea;
	@FXML private Label chatRoomLabel;

	private ViewSwitch viewSwitch;
	private ChatClient chatClient;
	private User user;
	private String username;
	private String room;

	public ChatRoomController() throws IOException, TimeoutException {
		chatClient = new ChatClient();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Platform.runLater(() -> {
			username = getUser().getUsername();
			chatRoomLabel.setText(getRoom());
			System.out.println("Welcome to "+getRoom()+"user "+username); //TODO debug
			try {
				String msg = chatClient.receiveMessage();
				receiveMessage(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML private void sendMessage() throws IOException {
		String message = username+": "+messageField.getText();
		chatClient.sendMessage(message);
	}

	@FXML private void quitChat(){
		Scene scene = quitButton.getScene();
		viewSwitch = new ViewSwitch(mainView, scene);
		Platform.runLater(()-> {
			try {
				viewSwitch.changeView();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void receiveMessage(String message) throws IOException {
//		String message = chatClient.receiveMessage();
		messagesArea.appendText(message+"\n"); //se ha successo il dispatching
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public User getUser() {
		return user;
	}

	public String getRoom() {
		return room;
	}

}
