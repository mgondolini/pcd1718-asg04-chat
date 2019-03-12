package chatroom_service;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;

public class RoomsManager {

	//QUEUES
	private final static String ADD_ROOM_QUEUE = "add_room_queue";
	private final static String REMOVE_ROOM_QUEUE = "remove_room_queue";

	//EXCHANGES
	private final static String ROOMS_LIST_EXCHANGE = "rooms_list_exchange";

	private static String room;
	private static ArrayList<String> roomsList;

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);

		channel.exchangeDeclare(ROOMS_LIST_EXCHANGE, "fanout");

		Consumer addRoomConsumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("wewewe");
				room = new String(body, "UTF-8");
				System.out.println("room "+room);
				roomsList.add(room);
				System.out.println("Rooms List 1: " + roomsList);
				System.out.println(" [+][" + room.toUpperCase() + "] joined the group");
//				connessione al db con future
//					channel.basicPublish("", MESSAGES_TO_DISPATCH, null, addUserMsg.getBytes("UTF-8"));
			}
		};
		channel.basicConsume(ADD_ROOM_QUEUE, true, addRoomConsumer);


	}

}
