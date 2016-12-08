package start;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import ai.AI;
import gui.ISubscriber;
import logic.Field;

public class Start2 {
	static int currentPlayer = 1;
	
	private static ArrayList<ISubscriber> listeners = new ArrayList<ISubscriber>();
	// public int currentPlayer;
	// private boolean enableShot;

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		System.out.println("It is bot#2");

		Field playerFieldPlayer = new Field(10, 10, 4);
		AI ai = new AI(playerFieldPlayer);

		playerFieldPlayer.setShip();
		boolean enableShot = true;		
		// updateSubscribers();

		// TODO receive
		// получить, кто ходит первый
		String QUEUE_NAME = "hello";
		ConnectionFactory factory = new ConnectionFactory();
		// If we wanted to connect to a broker on a different machine we'd
		// simply specify its name or IP address here.
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				
				System.out.println(" [x] Received '" + message + "'");
				currentPlayer = Integer.parseInt(splitter(message));
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
		
		Thread.sleep(5000);

		//channel.close();
		//connection.close();


		while (enableShot) {
			if (currentPlayer == 0) { // Ходит bot#1
				System.out.println("His shot");
				// TODO send - делаю выстрел

				//while (ai.doShot() != Field.SHUT_MISSED);
				
				currentPlayer = 1;
			}
			if (currentPlayer == 1) {
				System.out.println("My shot");
				//TODO ЗДЕСЬ ПИСАТЬ КОД ДЛ ЭТОГО БОТА! 
				while (ai.doShot() != Field.SHUT_MISSED);
				System.out.println(ai.doShot());
				currentPlayer = 0;
			}
			updateSubscribers();

			if (playerFieldPlayer.getNumLiveShips() == 0) {
				enableShot = false;
				System.out.println("You won!!!");
				break;
			}
		}

	}

	public static void updateSubscribers() {
		Iterator<ISubscriber> i = listeners.iterator();
		while (i.hasNext()) {
			ISubscriber o = (ISubscriber) i.next();
			o.update();
		}
	}
	
	public static String splitter(String str) {
		String [] tmp = str.split(" ");
		return tmp[1];
	}

}
