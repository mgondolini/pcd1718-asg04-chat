package client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import static queues.Queues.SELECTED_ROOM_QUEUE;
import static queues.Queues.SELECTED_USER_QUEUE;

public class ChatRoomClient {
	private ArrayList<String> rooms;

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;

	Boolean received = false;
	private String room;
	private String username;

	public ChatRoomClient() throws IOException, TimeoutException {
		rooms = new ArrayList<>();

		this.factory = new ConnectionFactory();
		this.factory.setHost("localhost");
		this.connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(SELECTED_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(SELECTED_USER_QUEUE, false, false, false, null);

	}

	public String getRoom() throws IOException {
		while (!received){
			Consumer roomConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					room = new String(body, "UTF-8");
					received = true;
				}
			};
			channel.basicConsume(SELECTED_ROOM_QUEUE, true, roomConsumer);
		}
		received = false;
		return room;
	}

	public String getUser() throws IOException {

		while (!received){
			Consumer userConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					username = new String(body, "UTF-8");
					received = true;
				}
			};
			channel.basicConsume(SELECTED_USER_QUEUE, true, userConsumer);
		}
		received = false;
		return username;
	}
}
