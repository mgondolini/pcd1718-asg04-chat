package client;

import java.util.ArrayList;

public class ChatClient {

	private ArrayList<String> rooms;

	public ChatClient(){
		rooms = new ArrayList<>();
		rooms.add("room1");
	}

	public ArrayList<String> getRoomsList(){
		return rooms;
	}

	public ArrayList<String> addRoom(String room){
		rooms.add(room);
		return getRoomsList();
	}

	public ArrayList<String> removeRoom(String room){
		rooms.remove(room); // nel db
		return getRoomsList();
	}

	//sendmsg
	//Receive msg

}
