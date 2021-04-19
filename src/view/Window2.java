package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * 
 * @author Christophe
 */
public class Window2 extends JFrame {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 1292874948700725722L;

	/**
	 * Window constructor creates a new form of the game window.
	 */
	public Window2() {
	}

	private boolean validIP(String ip) {
		boolean ret = false;
		try {
			ret = true;

			if (ip == null || ip.isEmpty())
				ret = false;

			if (ret && this.ip.equalsIgnoreCase("exit"))
				System.exit(0);

			if (ret) {
				String[] parts = ip.split("\\.");
				if (parts.length != 4)
					ret = false;
				if (ret)
					for (String s : parts) {
						int i = Integer.parseInt(s);
						if ((i < 0) || (i > 255)) {
							ret = false;
						}
					}
			}

			if (ret && ip.endsWith("."))
				ret = false;
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			ret = false;
		}

		return ret;
	}

	/**
	 * This method is called to initialize the form.
	 */
	public void initComponents() {
		if (!this.windowAlreadyInitialized) {
			this.windowAlreadyInitialized = true;
			this.backgroundColor = new Color(63, 81, 181);
			this.logo = (new ImageIcon(ClassLoader.getSystemResource("logo.png"))).getImage();

			this.setTitle("Loup-Garou");
			this.setIconImage(this.logo);
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			this.setMinimumSize(new Dimension(1600, 900));
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.getContentPane().setLayout(new BorderLayout());
			this.setBackground(new Color(47, 47, 47));

			this.chat = new Chat(this.playerName, this, this.writer);
			this.welcome = new Welcome(this.logo, this.backgroundColor);

			this.add(this.welcome, BorderLayout.CENTER);
			this.add(this.chat, BorderLayout.EAST);

			this.setVisible(true);
			this.joinGame();

			// Makes a music loop
			Thread music = new Thread() {
				public void run() {
					Clip clip;
					try {
						AudioInputStream input = AudioSystem
								.getAudioInputStream(new File("musique-isolated-gedanken.wav"));
						clip = AudioSystem.getClip();
						clip.open(input);
						clip.loop(Clip.LOOP_CONTINUOUSLY);
						clip.start();
					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
				}
			};
			music.start();
		}
	}

	public void changeCenterPanel() {

		// Removes the welcome page in order to let the game page to be displayed
		this.remove(this.welcome);
		this.main = new JPanel();
		this.main.setLayout(new BorderLayout());

		JPanel playersTimerStatsRolePlayer = new JPanel();
		playersTimerStatsRolePlayer.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Players and timer
		JPanel playersTimer = new JPanel();
		playersTimer.setLayout(new BorderLayout());
		JPanel playersPanel = new JPanel();
		int x = 5; // A changer suivant le nb de playres : ArrayList to know all players + pseudo.
		playersPanel.setLayout(new GridLayout(2, x));
		for (int i = 0; i < 10; i++) {
			JPanel pan = new JPanel();
			if (i % 2 == 0)
				pan.setBackground(Color.blue);
			else
				pan.setBackground(Color.cyan);
			playersPanel.add(new JPanel());
		}
		this.timer = new Timer();
		this.timer.setBackground(Color.red);
		playersTimer.add(playersPanel, BorderLayout.CENTER);
		playersTimer.add(this.timer, BorderLayout.SOUTH);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.weightx = 4;
		gbc.gridy = 0;
		gbc.weighty = 1;
		playersTimerStatsRolePlayer.add(playersTimer, gbc);

		// Stats and rolePlayer
		JPanel statsRolePlayer = new JPanel();
		statsRolePlayer.setLayout(new GridLayout(2, 1));
		this.stats = new Statistics();
		this.stats.setBackground(Color.yellow);
		statsRolePlayer.add(this.stats);
		this.rolePlayer = new RolePlayer();
		this.rolePlayer.setBackground(Color.orange);
		statsRolePlayer.add(this.rolePlayer);
		gbc.gridx = 1;
		gbc.weightx = 1;
		playersTimerStatsRolePlayer.add(statsRolePlayer, gbc);

		// ChatAuto and roleExplanation
		JPanel chatAutoRoleExplanation = new JPanel();
		chatAutoRoleExplanation.setLayout(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		this.chatAuto = new ChatAuto();
		this.chatAuto.setBackground(Color.green);
		gbc2.fill = GridBagConstraints.BOTH;
		gbc2.gridx = 0;
		gbc2.weightx = 3;
		gbc2.gridy = 0;
		chatAutoRoleExplanation.add(this.chatAuto, gbc2);
		this.roleExp = new RoleExplanation();
		this.roleExp.setBackground(Color.magenta);
		gbc2.gridx++;
		gbc2.weightx = 2;
		chatAutoRoleExplanation.add(this.roleExp, gbc2);

		// The game page
		this.main.add(playersTimerStatsRolePlayer, BorderLayout.CENTER);
		this.main.add(chatAutoRoleExplanation, BorderLayout.SOUTH);
		this.add(this.main, BorderLayout.CENTER);
		this.invalidate();
		this.validate();
	}

	private void joinGame() {
		try {
			this.writer.write("4¤" + "¤" + this.playerName + " a rejoint la partie.\r\n");
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}