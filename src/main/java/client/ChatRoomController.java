package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatRoomController implements Initializable {

	@FXML private Button sendButton;
	@FXML private Button quitButton;
	@FXML private TextField messageField;
	@FXML private TextArea messagesArea;
	private ViewSwitch viewSwitch;
	private User user;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//lista utenti partecipanti, messaggi
	}

	@FXML private void sendMessage(){
		String message = messageField.getText();
//		messagesArea.appendText(user.getUsername()+": "+message+"\n");
		//inviare il messaggio al dispatcher
	}

	@FXML private void quitChat(){
		String view = "fxml/main_view.fxml";
		Scene scene = quitButton.getScene();
		viewSwitch = new ViewSwitch(view,scene);
		Platform.runLater(()-> {
			try {
				viewSwitch.changeView();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
