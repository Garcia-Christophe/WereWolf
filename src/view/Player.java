package view;

import java.awt.Color;

import javax.swing.JPanel;

public class Player extends JPanel {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 9176862146777384539L;

	private String playerName;

	public Player(String name) {
		if (name == null)
			throw new IllegalArgumentException("Player: Player(String): parameter null.");
		else {
			this.setBackground(Color.cyan);
			this.playerName = name;
		}
	}

	public String getPlayerName() {
		return this.playerName;
	}
}
