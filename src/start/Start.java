package start;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import ai.AI;
import gui.ISubscriber;
import logic.Field;

public class Start {
	private static ArrayList<ISubscriber> listeners = new ArrayList<ISubscriber>();
	// public int currentPlayer;
	// private boolean enableShot;

	public static void main(String[] args) throws IOException, TimeoutException {
		System.out.println("It is bot#1");

		Field playerFieldPlayer = new Field(10, 10, 4);
		AI ai = new AI(playerFieldPlayer);

		playerFieldPlayer.setShip();
		boolean enableShot = true;
		int currentPlayer = 0; //TODO do it editable in console!
		// updateSubscribers();

		// send
		// передать, кто ходит первый

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
				// send - делаю выстрел

				while (ai.doShot() != Field.SHUT_MISSED);
				
				currentPlayer = 1;
			}
			if (currentPlayer == 1) {
				//while (ai2.doShot() != Field.SHUT_MISSED);
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
