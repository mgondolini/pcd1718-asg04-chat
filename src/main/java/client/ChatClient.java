package client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static queues.Queues.*;

public class ChatClient {

	private ArrayList<String> rooms;

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private String queueListName;
	Boolean received = false;

	public ChatClient() throws IOException, TimeoutException {
		rooms = new ArrayList<>();

		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		this.connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REQUEST_LIST_QUEUE, false, false, false, null);
		channel.queueDeclare(SELECTED_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(SELECTED_USER_QUEUE, false, false, false, null);

		channel.exchangeDeclare(ROOMS_LIST_EXCHANGE, "fanout");
		queueListName = channel.queueDeclare().getQueue();
		channel.queueBind(queueListName, ROOMS_LIST_EXCHANGE, "");
	}

	public ArrayList<String> getRoomsList() throws IOException {

		channel.basicPublish("", REQUEST_LIST_QUEUE, null, null);
		while (!received) {
			Consumer roomsListConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					String roomsReceived = new String(body, "UTF-8");
					System.out.println("roomsReceived " + roomsReceived);
//					if(!roomsReceived.isEmpty())
						rooms = new ArrayList<>(Arrays.asList(roomsReceived.split(", ")));
//					else
//						rooms.add("bubu");
					received = true;
				}
			};
			channel.basicConsume(queueListName, true, roomsListConsumer);
		}
		return rooms;
	}

	public ArrayList<String> addRoom(String room) throws IOException {
		if(!rooms.contains(room)){
			rooms.add(room);
			channel.basicPublish("", ADD_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		else System.out.println("nome già in uso"); //dialog o label
		return getRoomsList();
	}

	public ArrayList<String> removeRoom(String room) throws IOException {
		if(!rooms.contains(room))
			System.out.println("nome non presente"); //dialog o label
		else {
			rooms.remove(room); // nel db
			channel.basicPublish("", REMOVE_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		return getRoomsList();
	}

	public void setRoom(String room) throws IOException {
		channel.basicPublish("", SELECTED_ROOM_QUEUE, null, room.getBytes("UTF-8"));
	}

	public void setUser(User user) throws IOException {
		channel.basicPublish("", SELECTED_USER_QUEUE, null, user.getUsername().getBytes("UTF-8"));
	}

	//sendmsg
	//Receive msg

}
