package message_dispatcher_service;

import com.rabbitmq.client.*;

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

		System.out.println(" _-_ MESSAGE DISPATCHER _-_");

		//Dispatching chat messages
		Consumer chatMessageConsumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				String timestamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
				String timestampedMsg = message+"\t\t("+timestamp+")";
				System.out.println(timestampedMsg);
				//TODO trovare un modo per passare la ROOM per exchage tipo topic
				channel.basicPublish(DISPATCH_MESSAGES, "room2", null, timestampedMsg.getBytes("UTF-8"));
			}
		};
		channel.basicConsume(CHAT_MSG_QUEUE, true, chatMessageConsumer);

	}

}
