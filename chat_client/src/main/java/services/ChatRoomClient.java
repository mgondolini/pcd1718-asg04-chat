package services;

import com.rabbitmq.client.*;
import controller.ChatRoomController;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static config.RabbitConfig.*;

/**
 * @author Monica Gondolini
 */
public class ChatRoomClient {

	private Channel channel;
	private ChatRoomController chatRoomController;
	private String dispatchMsgExchange;
	private String message;
	private String room;

	public ChatRoomClient(ChatRoomController chatRoomController, String room) throws IOException, TimeoutException {

		this.chatRoomController = chatRoomController;
		this.room = room;

		ConnectionFactory factory = new ConnectionFactory();
//		factory.setUsername(mqtt_username);
//		factory.setPassword(mqtt_password);
//		factory.setHost(host);
//		factory.setVirtualHost(virtual_host);
		try {
			factory.setUri("amqps://bsgrnlea:fHulCVGdiH83swYGtCfbl608sLaP1YR2@rat.rmq2.cloudamqp.com/bsgrnlea");
		} catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		Connection connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(CHAT_MSG_QUEUE, false, false, false, null);

		channel.exchangeDeclare(DISPATCH_MESSAGES, "topic");
		dispatchMsgExchange = channel.queueDeclare().getQueue();
		channel.queueBind(dispatchMsgExchange, DISPATCH_MESSAGES, room);

		receiveMessage();
	}

	public void sendMessage(String msg, String username) throws IOException{
		JSONObject message = new JSONObject().put(USERNAME, username).put(MESSAGE, msg).put(ROOM, room);
		channel.basicPublish("", CHAT_MSG_QUEUE, null, message.toString().getBytes("UTF-8"));
		receiveMessage();
	}

	private void receiveMessage() throws IOException{
		Consumer dispatcherConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "UTF-8");
				if(msg.equals(CSrequest)){
					JSONObject message = new JSONObject().put(USERNAME, "").put(MESSAGE, CSaccepted).put(ROOM, room);
					channel.basicPublish("", CHAT_MSG_QUEUE, null, message.toString().getBytes("UTF-8"));
				}else {
					setMessage(msg);
					chatRoomController.displayMessage(message);
				}
			}
		};
		channel.basicConsume(dispatchMsgExchange, true, dispatcherConsumer);
	}

	private void setMessage(String message) {
		this.message = message;
	}

}
