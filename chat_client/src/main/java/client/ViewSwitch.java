package client;

import controller.ChatRoomController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Monica Gondolini
 */
public class ViewSwitch {

	private String view;
	private Scene scene;

	public ViewSwitch(String view, Scene scene){
		this.view = view;
		this.scene = scene;
	}

	public void changeView() throws IOException {
		scene.setRoot(FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource(view))));
		scene.getWindow().sizeToScene();
	}

	public void changeToRoomView(User user, String room) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(view));
		AnchorPane anchorPane = loader.load();
		ChatRoomController controller = loader.getController();
		controller.setRoom(room);
		controller.setUser(user);
		scene.setRoot(anchorPane);
		scene.getWindow().sizeToScene();
	}
}
