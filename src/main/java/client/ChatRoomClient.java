package client;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static config.RabbitConfig.CHAT_MSG_QUEUE;
import static config.RabbitConfig.DISPATCH_MESSAGES_EXCHANGE;

public class ChatRoomClient {

	private Channel channel;
	private ChatRoomController chatRoomController;
	private String dispatchMsgExchange;
	private String message;

	public ChatRoomClient(ChatRoomController chatRoomController) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		this.channel = connection.createChannel();

		channel.queueDeclare(CHAT_MSG_QUEUE, false, false, false, null);

		channel.exchangeDeclare(DISPATCH_MESSAGES_EXCHANGE, "fanout");
		dispatchMsgExchange = channel.queueDeclare().getQueue();
		channel.queueBind(dispatchMsgExchange, DISPATCH_MESSAGES_EXCHANGE, "");

		this.chatRoomController = chatRoomController;
		receiveMessage();
	}

	public void sendMessage(String msg) throws IOException{
		if(!msg.equals("")){
			channel.basicPublish("", CHAT_MSG_QUEUE, null, msg.getBytes("UTF-8"));
		}else{
			System.out.println("cannote send empty msg");
		}
		receiveMessage();
	}

	public void receiveMessage() throws IOException{
		Consumer dispatcherConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "UTF-8");
				setMessage(msg);
				chatRoomController.receiveMessage(message);
			}
		};
		channel.basicConsume(dispatchMsgExchange, true, dispatcherConsumer);
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
