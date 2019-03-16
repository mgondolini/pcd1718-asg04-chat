package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

class ViewSwitch {

	private String view;
	private Scene scene;

	ViewSwitch(String view, Scene scene){
		this.view = view;
		this.scene = scene;
	}

	void changeView() throws IOException {
		scene.setRoot(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(view))));
		scene.getWindow().sizeToScene();
	}

	void changeToRoomView(User user, String room) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(view));
		AnchorPane anchorPane = loader.load();
		ChatRoomController controller = loader.getController();
		System.out.println("controller"+controller);
		controller.setRoom(room);
		controller.setUser(user);
		scene.setRoot(anchorPane);
		scene.getWindow().sizeToScene();
	}
}
