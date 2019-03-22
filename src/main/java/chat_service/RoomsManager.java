package chat_service;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RoomsManager {

	private DatabaseConnection dbConnection = new DatabaseConnection();
	private Document document;

	public RoomsManager(){
		document = new Document("_id",1);
	}

	public ArrayList<String> populateList() throws ExecutionException, InterruptedException {
		String json = dbConnection.getRooms(document);
		ArrayList<String> rooms = new ArrayList<>(Arrays.asList(json.split(", ")));
		if(rooms.get(0).equals("")) rooms.remove(0);
		return rooms;
	}

	public ArrayList<String> addRoom(String room, ArrayList<String> roomsList) throws ExecutionException, InterruptedException {
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

	public ArrayList<String> removeRoom(String room, ArrayList<String> roomsList) throws ExecutionException, InterruptedException {
		roomsList.remove(room);
		String rooms = roomsToString(roomsList);
		dbConnection.update(document, new Document("rooms", rooms));
		System.out.println("Room to remove: "+room+"\tRooms: " + roomsList);
		return roomsList;
	}

	public String roomsToString(ArrayList<String> roomsList){
		String rooms = roomsList.toString();
		rooms = rooms.replace("[", "");
		rooms = rooms.replace("]","");
		return rooms;
	}
}
