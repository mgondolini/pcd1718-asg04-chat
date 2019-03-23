package chat_service;

import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static config.RabbitConfig.*;

public class ChatService {

	private static String room;
	private static ArrayList<String> roomsList = new ArrayList<>();
	private static boolean cs = false;
	private static String CSuser = "";

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(localhost);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REQUEST_LIST_QUEUE, false, false, false, null);
		channel.queueDeclare(CHAT_MSG_QUEUE, false, false, false, null);

		channel.exchangeDeclare(ROOMS_LIST_EXCHANGE, "fanout");
		channel.exchangeDeclare(DISPATCH_MESSAGES, "topic");

		RoomsManager roomsManager = new RoomsManager();

		roomsList = roomsManager.populateList();

		Consumer roomsListConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("Rooms List: "+roomsList);
				String rooms = roomsManager.roomsToString(roomsList);
				channel.basicPublish(ROOMS_LIST_EXCHANGE, "", null, rooms.getBytes("UTF-8"));
			}
		};
		channel.basicConsume(REQUEST_LIST_QUEUE, true, roomsListConsumer);

		Consumer addRoomConsumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				room = new String(body, "UTF-8");
				try {
					roomsList = roomsManager.addRoom(room, roomsList);
				} catch (ExecutionException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		channel.basicConsume(ADD_ROOM_QUEUE, true, addRoomConsumer);

		Consumer removeRoomConsumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				room = new String(body, "UTF-8");
				try {
					roomsList = roomsManager.removeRoom(room, roomsList);
				} catch (ExecutionException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		channel.basicConsume(REMOVE_ROOM_QUEUE, true, removeRoomConsumer);

		Consumer chatMessageConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "UTF-8");
				JSONObject jsonMessage = new JSONObject(msg);
				dispatchMessages(jsonMessage, channel);

			}
		};
		channel.basicConsume(CHAT_MSG_QUEUE, true, chatMessageConsumer);
	}

	private static void dispatchMessages(JSONObject jsonMessage, Channel channel) throws IOException {

		String message = jsonMessage.getString("message");
		String username = jsonMessage.getString("username");
		String room = jsonMessage.getString("room");
		String timestampedMsg = getTimestampedMsg(username,message);

		switch (message) {
			case "enter-cs":
				System.out.println("enter-cs");
				channel.basicPublish(DISPATCH_MESSAGES, room, null, "cs-request".getBytes("UTF-8"));
				break;
			case "cs-ok":
				System.out.println("cs-ok");
				cs = true;
				CSuser = "username"; //TODO settare username con quello vero
				break;
			case "exit-cs":
				System.out.println("exit-cs");
				cs = false;
				CSuser = "";
				break;
			default:
				if (cs && CSuser.equals(username)) {
					channel.basicPublish(DISPATCH_MESSAGES, room, null, timestampedMsg.getBytes("UTF-8"));
				} else if (CSuser.equals("") && !cs) {
					channel.basicPublish(DISPATCH_MESSAGES, room, null, timestampedMsg.getBytes("UTF-8"));
				}
				break;
		}
	}

	private static String getTimestampedMsg(String username, String message){
		String timestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
		return username+": "+message+"\t\t("+timestamp+")";
	}

	private static void setCSuser(String username){
		CSuser = username;
	}
}
