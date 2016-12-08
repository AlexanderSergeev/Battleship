package rabbit;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class Send {

	private final static String QUEUE_NAME = "battle";
	private static ArrayList<String> data = new ArrayList<String>();

	public static void main(String[] argv) throws Exception {

		// 1 - missed
		// 2 - injured
		// 3 - killed
		send("2 1 3"); // miss/kill/injure -> x -> y
		recieve();
		process();
		Thread.sleep(4000);
		System.out.println(data.toString());
		System.exit(0);
	}

	private static synchronized void process() {
		// TODO Auto-generated method stub
		
	}

	public static synchronized void send(String message) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		// If we wanted to connect to a broker on a different machine we'd
		// simply specify its name or IP address here.
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
		System.out.println(" [x] Sent " + message + "");
	}

	public static synchronized void recieve() throws IOException, TimeoutException {
		System.out.println(" [*] Waiting for messages.");

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public synchronized void handleDelivery(String consumerTag, Envelope envelope,
					AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				String[] variables = message.split(" ");
				data.add(variables[0]);
				if (variables.length > 1) {
					data.add(variables[1]);
					data.add(variables[2]);
				}
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);

	}

}