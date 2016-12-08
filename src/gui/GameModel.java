package gui;

import java.util.ArrayList;
import java.util.Iterator;

import ai.AI;
import logic.*;

public class GameModel {
	private ArrayList<ISubscriber> listeners = new ArrayList<ISubscriber>();
	public Field playerFieldPlayer;
	public Field playerFieldPlayer2;
	public AI ai;
	public AI ai2;
	public int currentPlayer;
	private boolean enableShot;

	public GameModel(int dx, int dy, int numShip) {
		playerFieldPlayer = new Field(dx, dy, numShip);
		playerFieldPlayer2 = new Field(dx, dy, numShip);
		ai = new AI(playerFieldPlayer);
		ai2 = new AI(playerFieldPlayer2);
		setDimension(dx, dy, numShip);
	}

	public void setDimension(int dx, int dy, int numShip) {
		playerFieldPlayer2.setWidth(dx);
		playerFieldPlayer2.setHeight(dy);
		playerFieldPlayer2.setMaxShip(numShip);
		
		playerFieldPlayer.setWidth(dx);
		playerFieldPlayer.setHeight(dy);
		playerFieldPlayer.setMaxShip(numShip);
		enableShot = true;
		newGame();
		updateSubscribers();
	}
	
	public void newGame() {
		playerFieldPlayer.setShip();
		playerFieldPlayer2.setShip();
		enableShot = true;
		currentPlayer = 0;
		updateSubscribers();
	}

	public void doShotByOpponent() {
		if (!enableShot) {
			return;
		}
		if (currentPlayer == 0) {
			/*if (playerFieldPlayer2.getCell(x, y).isMark()) {
				return;
			}*/
			while (ai2.doShot() != Field.SHUT_MISSED);
			currentPlayer = 1;
		}
		if (currentPlayer ==1) {
			while (ai.doShot() != Field.SHUT_MISSED);
			currentPlayer = 0;
		}
		updateSubscribers();

		if ( (playerFieldPlayer.getNumLiveShips() == 0) || (playerFieldPlayer2.getNumLiveShips() == 0) ) {
			enableShot = false;
		}
	}
	
	public void register(ISubscriber o) {
		listeners.add(o);
		o.update();
	}
	
	public void unRegister(ISubscriber o) {
		listeners.remove(o);
	}
	
	public void updateSubscribers() {
		Iterator<ISubscriber> i = listeners.iterator();
		while(i.hasNext()) {
			ISubscriber o = (ISubscriber)i.next();
			o.update();
		}
	}

}
