package chatroom_service;

import com.rabbitmq.client.*;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RoomsManager {

	//QUEUES
	private final static String ADD_ROOM_QUEUE = "add_room_queue";
	private final static String REMOVE_ROOM_QUEUE = "remove_room_queue";
	private final static String REQUEST_LIST_QUEUE = "request_list_queue";

	//EXCHANGES
	private final static String ROOMS_LIST_EXCHANGE = "rooms_list_exchange";

	private static String room;
	private static ArrayList<String> roomsList = new ArrayList<>(); //da riempire dal db
	private static DatabaseConnection dbConnection = new DatabaseConnection();
	private static Document document;

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(ADD_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REMOVE_ROOM_QUEUE, false, false, false, null);
		channel.queueDeclare(REQUEST_LIST_QUEUE, false, false, false, null);

		channel.exchangeDeclare(ROOMS_LIST_EXCHANGE, "fanout");

		roomsList = populateList();

		Consumer roomsListConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				System.out.println("Rooms List: "+roomsList);
				String usr = new String(body, "UTF-8");
				channel.basicPublish(ROOMS_LIST_EXCHANGE, usr, null, roomsToString(roomsList).getBytes("UTF-8"));
			}
		};
		channel.basicConsume(REQUEST_LIST_QUEUE, true, roomsListConsumer);

		Consumer addRoomConsumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				document = new Document("_id",1);
				room = new String(body, "UTF-8");
				try {
					roomsList = addRoom(room, roomsList);
				} catch (ExecutionException | InterruptedException e) {e.printStackTrace();}
//					channel.basicPublish("", ROOMS_LIST_EXCHANGE, null, roomsList.toString().getBytes("UTF-8"));
			}
		};
		channel.basicConsume(ADD_ROOM_QUEUE, true, addRoomConsumer);

		Consumer removeRoomConsumer = new DefaultConsumer(channel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				room = new String(body, "UTF-8");
				try {
					roomsList = removeRoom(room, roomsList);
				} catch (ExecutionException | InterruptedException e) {e.printStackTrace();}
//					channel.basicPublish("", ROOMS_LIST_EXCHANGE, null, roomsList.toString().getBytes("UTF-8"));
			}
		};
		channel.basicConsume(REMOVE_ROOM_QUEUE, true, removeRoomConsumer);
	}

	private static ArrayList<String> populateList() throws ExecutionException, InterruptedException {
		document = new Document("_id",1);
		String json = dbConnection.getRooms(document);
		ArrayList<String> rooms = new ArrayList<>(Arrays.asList(json.split(", ")));
		if(rooms.get(0).equals("")) rooms.remove(0);
		return rooms;
	}

	private static ArrayList<String> addRoom(String room, ArrayList<String> roomsList) throws ExecutionException, InterruptedException {
		if(roomsList.isEmpty()){
			roomsList.add(room);
			dbConnection.insert(document.append("rooms", room));
		}else{
			roomsList.add(room);
			String rooms = roomsToString(roomsList);
			dbConnection.update(document, new Document("rooms", rooms));
		}
		System.out.println("Room to add: "+room+"\tRooms: " + roomsList);
		return roomsList;
	}

	private static ArrayList<String> removeRoom(String room, ArrayList<String> roomsList) throws ExecutionException, InterruptedException {
		document = new Document("_id",1);
		roomsList.remove(room);
		String rooms = roomsToString(roomsList);
		dbConnection.update(document, new Document("rooms", rooms));
		System.out.println("Room to remove: "+room+"\tRooms: " + roomsList);
		return roomsList;
	}

	private static String roomsToString(ArrayList<String> roomsList){
		String rooms = roomsList.toString();
		rooms = rooms.replace("[", "");
		rooms = rooms.replace("]","");
		return rooms;
	}

}
