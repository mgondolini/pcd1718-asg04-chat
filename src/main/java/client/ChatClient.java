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
	private ChatRoomController chatRoomController;
	private String roomsListQueue, dispatchMsgExchange;
	private ArrayList<String> rooms;
	private String message;
	private Boolean received = false;
	private Boolean received2 = false;

	public ChatClient() throws IOException, TimeoutException {
		this.rooms = new ArrayList<>();
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

		channel.exchangeDeclare(DISPATCH_MESSAGES_EXCHANGE, "fanout");
		dispatchMsgExchange = channel.queueDeclare().getQueue();
		channel.queueBind(dispatchMsgExchange, DISPATCH_MESSAGES_EXCHANGE, "");
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
					//TODO controllare se la lista è vuota
//					if(!roomsReceived.isEmpty())
						rooms = new ArrayList<>(Arrays.asList(roomsReceived.split(", ")));
//					else
//						rooms.add("bubu");
					received = true;
				}
			};
			channel.basicConsume(roomsListQueue, true, roomsListConsumer);
		}
		received = false;
		return rooms;
	}

	public ArrayList<String> addRoom(String room) throws IOException {
		if(!rooms.contains(room)){
			rooms.add(room);
			channel.basicPublish("", ADD_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		else System.out.println("nome già in uso"); //TODO dialog o label
		return getRoomsList();
	}

	public ArrayList<String> removeRoom(String room) throws IOException {
		if(!rooms.contains(room))
			System.out.println("nome non presente"); //TODO dialog o label
		else {
			rooms.remove(room); // nel db
			channel.basicPublish("", REMOVE_ROOM_QUEUE, null, room.getBytes("UTF-8"));
		}
		return getRoomsList();
	}

	public void sendMessage(String msg) throws IOException{
		if(!msg.equals("")){
			System.out.println("invio msg");
			channel.basicPublish("", CHAT_MSG_QUEUE, null, msg.getBytes("UTF-8"));
		}else{
			System.out.println("cannote send empty msg");
		}
		receiveMessage();
	}

//	public void receiveMessage() throws IOException{
//		//display dispatched messages
//		while (!received2) {
//		Consumer dispatcherConsumer = new DefaultConsumer(channel) {
//			@Override
//			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
//					throws IOException {
//				String msg = new String(body, "UTF-8");
//				System.out.println("consumer "+msg);
//				setMessage(msg);
//				received2 = true;
//			}
//		};
//		channel.basicConsume(dispatchMsgExchange, true, dispatcherConsumer);
//		}
//		chatRoomController.receiveMessage(message);
//		received2 = false;
//
//	}

	public String receiveMessage() throws IOException{
		//display dispatched messages
//		while (!received2) {
			Consumer dispatcherConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					String msg = new String(body, "UTF-8");
					System.out.println("consumer "+msg);
					setMessage(msg);
					received2 = true;
				}
			};
			channel.basicConsume(dispatchMsgExchange, true, dispatcherConsumer);
//		}
		received2 = false;
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	//sendmsg
	//Receive msg

}
