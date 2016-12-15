package ai;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import logic.*;


public class AIRandom extends AIBase {
	
	public AIRandom(AI ai) {
		super(ai);
	}

	public int doShot() {
		ArrayList<Cell> list = new ArrayList<Cell>();
		for (int j = 0; j < ai.getField().getWidth(); j++) {
			for (int i = 0; i < ai.getField().getHeight(); i++) {
				Cell e = ai.getField().getCell(i, j);
				if (! e.isMark() ) { 
					list.add(e);
				}
			}
		}
		if (list.size() == 0) {
			return Field.SHUT_MISSED;
		}
		Cell cell = list.get(ai.rand.nextInt(list.size()));
		System.out.println(cell.x + " " + cell.y);
		
		String QUEUE_NAME = "hello";
		String message = cell.x + " " + cell.y;
		ConnectionFactory factory = new ConnectionFactory();
		// If we wanted to connect to a broker on a different machine we'd
		// simply specify its name or IP address here.
		factory.setHost("localhost");
		Connection connection;
		try {
			connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		int shot = cell.doShot();
		if (shot == Field.SHUT_INJURED) {
			ai.action = new AIPlace(ai);
			ai.action.setPosition(cell.x, cell.y);
		}
		return shot;
	}

}
