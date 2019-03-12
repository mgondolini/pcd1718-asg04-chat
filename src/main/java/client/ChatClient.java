package client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class ChatClient {

	private ArrayList<String> rooms;

	//QUEUES
	private final static String ADD_ROOM_QUEUE = "add_room_queue";
	private final static String REMOVE_ROOM_QUEUE = "remove_room_queue";

	ConnectionFactory factory;
	Connection connection;
	Channel channel;

	public ChatClient() throws IOException, TimeoutException {
		rooms = new ArrayList<>();
		rooms.add("room1");

		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		this.connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);
	}

	public ArrayList<String> getRoomsList(){
		return rooms;
	}

	public ArrayList<String> addRoom(String room) throws IOException {
		rooms.add(room);
		channel.basicPublish("", ADD_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		return getRoomsList();
	}

	public ArrayList<String> removeRoom(String room){
		rooms.remove(room); // nel db
		return getRoomsList();
	}

	//sendmsg
	//Receive msg

}
