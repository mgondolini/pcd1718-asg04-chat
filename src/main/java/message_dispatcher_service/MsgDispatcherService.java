package message_dispatcher_service;

import com.rabbitmq.client.*;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static config.RabbitConfig.CHAT_MSG_QUEUE;
import static config.RabbitConfig.DISPATCH_MESSAGES;

public class MsgDispatcherService {

	public static void main(String[] argv) throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(CHAT_MSG_QUEUE, false, false, false, null);
		channel.exchangeDeclare(DISPATCH_MESSAGES, "topic");

		System.out.println("MESSAGE DISPATCHER");

		Consumer chatMessageConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String msg = new String(body, "UTF-8");
				JSONObject jsonMessage = new JSONObject(msg);
				String room = jsonMessage.getString("room");
				String timestampedMsg = getTimestampedMsg(jsonMessage);
				System.out.println(timestampedMsg+"\t\t"+room); //TODO
				channel.basicPublish(DISPATCH_MESSAGES, room, null, timestampedMsg.getBytes("UTF-8"));
			}
		};
		channel.basicConsume(CHAT_MSG_QUEUE, true, chatMessageConsumer);
	}

	private static String getTimestampedMsg(JSONObject jsonMessage){
		String message = jsonMessage.getString("message");
		String timestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
		return message+"\t\t\t\t("+timestamp+")";
	}

}
