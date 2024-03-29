package services;

import com.rabbitmq.client.*;
import controller.Controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static config.RabbitConfig.*;

/**
 * @author Monica Gondolini
 */
public class ChatClient {

	private Channel channel;
	private Controller controller;
	private String roomsListQueue;
	private ArrayList<String> rooms;

	public ChatClient(Controller controller) throws IOException, TimeoutException {
		this.rooms = new ArrayList<>();
		this.controller = controller;

		ConnectionFactory factory = new ConnectionFactory();
//		factory.setUsername(mqtt_username);
//		factory.setPassword(mqtt_password);
//		factory.setHost(host);
//		factory.setVirtualHost(virtual_host);
//		factory.setPort(port);
		try {
			factory.setUri("amqps://bsgrnlea:fHulCVGdiH83swYGtCfbl608sLaP1YR2@rat.rmq2.cloudamqp.com/bsgrnlea");
		} catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
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
		Consumer roomsListConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String roomsReceived = new String(body, "UTF-8");
				System.out.println("roomsReceived " + roomsReceived);
				//TODO controllare se la lista è vuota
				rooms = new ArrayList<>(Arrays.asList(roomsReceived.split(", ")));
				controller.displayRooms(rooms);
			}
		};
		channel.basicConsume(roomsListQueue, true, roomsListConsumer);
	}

	public void addRoom(String room) throws IOException {
		controller.removeFromObsList(rooms);
		if(!rooms.contains(room)){
			rooms.add(room);
			channel.basicPublish("", ADD_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		else controller.showDialog("Room name already in use");
		getRoomsList();
	}

	public void removeRoom(String room) throws IOException {
		controller.removeFromObsList(rooms);
		if(!rooms.contains(room))
			controller.showDialog("Room name is empty");
		else {
			rooms.remove(room);
			channel.basicPublish("", REMOVE_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		getRoomsList();
	}
}
