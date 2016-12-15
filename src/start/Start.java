package start;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
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

public class Start {
	private static ArrayList<ISubscriber> listeners = new ArrayList<ISubscriber>();
	// public int currentPlayer;
	// private boolean enableShot;
	static String shot;

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		
		System.out.println("It is bot#1");

		Field playerFieldPlayer = new Field(10, 10, 4);
		AI ai = new AI(playerFieldPlayer);

		playerFieldPlayer.setShip();
		boolean enableShot = true;
		int currentPlayer = 0; //TODO do it editable in console!
		// updateSubscribers();

		// send
		// передать, кто ходит первый
		Scanner sc = new Scanner(System.in);
		currentPlayer = sc.nextInt();
		
		String QUEUE_NAME = "hello";
		String message = "bot " + currentPlayer;
		ConnectionFactory factory = new ConnectionFactory();
		// If we wanted to connect to a broker on a different machine we'd
		// simply specify its name or IP address here.
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
		System.out.println(" [x] Sent '" + message + "'");


		while (enableShot) {
			if (currentPlayer == 0) { // Ходит bot#1
				System.out.println("My shot");
				// send - делаю выстрел

				while (ai.doShot() != Field.SHUT_MISSED);
				//channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
				//System.out.println(" [x] Sent '" + message + "'");
				
				currentPlayer = 1;
			}
			if (currentPlayer == 1) {
				System.out.println("His shot");
				Thread.sleep(2000);
				// TODO ждем ответа, затем получаем координаты, проверяем и отправляем
				factory.setHost("localhost"); 
				Consumer consumer2 = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
						String message = new String(body, "UTF-8");
						
						System.out.println(" [x] Received '" + message + "'");
						shot = message;
					}
				};
				channel.basicConsume(QUEUE_NAME, true, consumer2);
				currentPlayer = 0;
			}
			updateSubscribers();

			
			if (playerFieldPlayer.getNumLiveShips() == 0) {
				if (currentPlayer == 0) {
					enableShot = false;
					System.out.println("You won!!!");
					break;
				}
				else if (currentPlayer == 1) {
					enableShot = false;
					System.out.println("You lose!!!");
					break;
				}
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
