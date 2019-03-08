package client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

	@FXML Button addButton;
	@FXML Button removeButton;
	@FXML Button enterButton;
	@FXML TextField usernameField;
	@FXML ListView<String> roomsListView;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//caricare le chatroom nwella listview
	}

	@FXML private void addRoom(ActionEvent event){

	}

}
