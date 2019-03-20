package client;

import com.rabbitmq.client.*;
import javafx.application.Platform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static config.RabbitConfig.*;

public class ChatClient {

	private Channel channel;
	private Controller controller;
	private String roomsListQueue;
	private ArrayList<String> rooms;
	private Boolean received = false;

	public ChatClient(Controller controller) throws IOException, TimeoutException {
		this.rooms = new ArrayList<>();
		this.controller = controller;

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REQUEST_LIST_QUEUE, false, false, false, null);

		channel.exchangeDeclare(ROOMS_LIST_EXCHANGE, "fanout");
		roomsListQueue = channel.queueDeclare().getQueue();
		channel.queueBind(roomsListQueue, ROOMS_LIST_EXCHANGE, "");
	}

	public void getRoomsList() throws IOException {
		channel.basicPublish("", REQUEST_LIST_QUEUE, null, null);
		while (!received) {
			Consumer roomsListConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					String roomsReceived = new String(body, "UTF-8");
					System.out.println("roomsReceived " + roomsReceived);
					//TODO controllare se la lista è vuota
//					if(!roomsReceived.isEmpty())
						rooms = new ArrayList<>(Arrays.asList(roomsReceived.split(", ")));
						controller.displayRooms(rooms);
//					else
//						rooms.add("bubu");
					received = true;
				}
			};
			channel.basicConsume(roomsListQueue, true, roomsListConsumer);
		}
		received = false;
	}

	public void addRoom(String room) throws IOException
	{
		controller.removeFromList(rooms);
		if(!rooms.contains(room)){
			rooms.add(room);
			channel.basicPublish("", ADD_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		else System.out.println("nome già in uso"); //TODO dialog o label
		getRoomsList();
	}

	public void removeRoom(String room) throws IOException {
		controller.removeFromList(rooms);
		if(!rooms.contains(room))
			System.out.println("nome non presente"); //TODO dialog o label
		else {
			rooms.remove(room); // nel db
			channel.basicPublish("", REMOVE_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		getRoomsList();
	}
}
