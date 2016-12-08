package gui;

import javax.swing.UIManager;

public class FrameGameLauncher {
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		GameModel model = new GameModel(10, 10, 4);
		GameView view = new GameView(model);
		view.setVisible(true);		
	}

}
