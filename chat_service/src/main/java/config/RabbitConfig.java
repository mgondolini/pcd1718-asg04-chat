package config;


public class RabbitConfig {

	//CONNECTION
	public final static String host = "fox.rmq.cloudamqp.com";
	public final static String mqtt_username = "dytjujcm";
	public final static String mqtt_password = "e6rQGUTU-6Psc9cf8fBR1Miypb2t7qBi";
	public final static String virtual_host = "dytjujcm";
	public final static int port = 1883;

	//QUEUES
	public final static String ADD_ROOM_QUEUE = "add_room_queue";
	public final static String REMOVE_ROOM_QUEUE = "remove_room_queue";
	public final static String REQUEST_LIST_QUEUE = "request_list_queue";
	public final static String CHAT_MSG_QUEUE = "chat_msg_queue";

	//EXCHANGES
	public final static String ROOMS_LIST_EXCHANGE = "rooms_list_exchange";
	public final static String DISPATCH_MESSAGES = "dispatch_messages";

	//OTHERS
	public final static String CSenter = "enter-cs";
	public final static String CSrequest = "cs-request";
	public final static String CSaccepted = "cs-accepted";
	public final static String CSexit = "exit-cs";
	public final static String USERNAME = "username";
	public final static String MESSAGE = "message";
	public final static String ROOM = "room";


}
