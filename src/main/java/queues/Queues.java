package queues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Queues {

	//QUEUES
	public final static String ADD_ROOM_QUEUE = "add_room_queue";
	public final static String REMOVE_ROOM_QUEUE = "remove_room_queue";
	public final static String REQUEST_LIST_QUEUE = "request_list_queue";
	public final static String SELECTED_ROOM_QUEUE = "selected_room_queue";
	public final static String SELECTED_USER_QUEUE = "selected_user_queue";
	//EXCHANGES
	public final static String ROOMS_LIST_EXCHANGE = "rooms_list_exchange";
}
