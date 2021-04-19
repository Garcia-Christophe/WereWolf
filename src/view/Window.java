package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * 
 * @author Christophe
 */
public class Window extends JFrame {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -3739008754324139579L;

	// Local
	private final int PORT = 2001;
	private String ip;
	private Socket socket;
	private ServerSocket serverSocket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private ObjectInputStream objInput;
	private ArrayList<Socket> clientSocket;
//	private Thread thread;

	// Interface
	private Chat chat;
	private ArrayList<String> playersName;
	private Timer timer;
	private ChatAuto chatAuto;
	private RoleExplanation roleExp;
	private RolePlayer rolePlayer;
	private Statistics stats;
	private Welcome welcome;
	private String playerName;
	private JPanel main;
	private boolean windowAlreadyInitialized;
	private Image logo;
	private Color backgroundColor;
	private boolean playersDecidedToStartTheGame;
	private final String DELIMITER = "\\s*¤\\s*";

	/**
	 * Window constructor creates a new form of the game window.
	 */
	public Window() {

		// Asks the user to enter the IP address
		while (!this.validIP(this.ip)) {
			this.ip = JOptionPane.showInputDialog(null, "Entrez l'adresse IPv4 du réseau :\n(\"exit\" pour quitter)",
					"Adresse IP", JOptionPane.INFORMATION_MESSAGE);
		}

		// True if the client who has launched the game is the host player.
		boolean isServer = false;

		// Creating the server
		try {
			this.serverSocket = new ServerSocket(this.PORT, 14, InetAddress.getByName(this.ip));
			isServer = true;
		} catch (IOException ioe) {
		}

		// Thread allowing to wait other players
		if (isServer) {
			this.clientSocket = new ArrayList<Socket>();
			Thread waitingAnotherPlayer = new Thread() {
				public void run() {
					Window.this.socket = null;
					try {
						Window.this.socket = new Socket(Window.this.ip, Window.this.PORT);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Socket soc = null;

					// While players didn't decide to start the game
					while (!Window.this.playersDecidedToStartTheGame) {

						// Waiting for other players
						try {
							soc = Window.this.serverSocket.accept();

							// Authorize the initComponents call
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(soc.getOutputStream());
							objectOutputStream.writeObject("initComponents");
							objectOutputStream.flush();

							Window.this.clientSocket.add(soc);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			waitingAnotherPlayer.start();
		}

		// Asks the player to enter his pseudo, while it's respets the caracteristics
		// and is not already used
		while (this.playerName == null || this.playerName.length() >= 18 || this.playerName.length() <= 0) {
			System.out.println("test");
			this.playerName = JOptionPane.showInputDialog(null, "Entrez votre nom :\n-> Entre 1 et 17 caratères",
					"Pseudo du joueur", JOptionPane.INFORMATION_MESSAGE);
		}

		try {
			this.socket = new Socket(this.ip, this.PORT);
			this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			objInput = new ObjectInputStream(this.socket.getInputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Initialize the frame
		this.initComponents();

		// Updates messages area
		Thread updateMessages = new Thread() {
			public void run() {
				String msg = "";
				Font font1 = new Font("Calibri", Font.BOLD, 20);
				Font font2 = new Font("Calibri", Font.PLAIN, 20);
				FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
				try {
					while (true) {
						System.out.println("non");
						if ((msg = reader.readLine()) != null) {

							System.out.println("passe");
							// Reading and initializing data
							ArrayList<String> data = new ArrayList<>(Arrays.asList(msg.split(DELIMITER)));

							// The recipient is caracterised by a number :
							// 0: initComponents, 1: all, 2: werewolves, 3: deads, 4: join the game,
							// 5: launch the game, 6: updates players list
							int numberSpecification = Integer.parseInt(data.get(0)); // Agir maintenant
							// avec les numéros en
							// conséquence.
							// Player name and his message
							String name = data.get(1);
							String message = data.get(2);
							String entireMessage = name + message;

							int textWidthPlayerName = (int) (font1.getStringBounds(name, frc).getWidth());
							int textWidthMessage = (int) (font2.getStringBounds(message, frc).getWidth());
							int textWidth = textWidthPlayerName + textWidthMessage;
							ArrayList<String> msgParts = new ArrayList<String>();
							boolean bigMessage = false;

							// Checks if the message is not too big, if it is the case, then cut it in
							// parts, so the message will be sent in multiple lines
							int j = 0;
							while (textWidth >= 450) {
								bigMessage = true;
								int newTextWidth = textWidth;
								String partOfMessage = null;
								int i = 1;

								while (newTextWidth >= 450) {
									partOfMessage = entireMessage.substring(0, entireMessage.length() - i);
									if (j == 0)
										newTextWidth = (int) (font1.getStringBounds(name, frc).getWidth())
												+ (int) (font2.getStringBounds(partOfMessage, frc).getWidth());
									else
										newTextWidth = (int) (font2.getStringBounds(partOfMessage, frc).getWidth());
									i++;
								}

								if (j == 0)
									partOfMessage = entireMessage.substring(name.length(),
											entireMessage.length() - i + 1);
								msgParts.add(partOfMessage);
								entireMessage = entireMessage.substring(entireMessage.length() - i + 1,
										entireMessage.length());
								textWidth = (int) (font2.getStringBounds(entireMessage, frc).getWidth());
								j++;
							}

							if (!bigMessage) {
								if (name.equals(playerName))
									chat.appendToPane("Vous ", message + "\n", backgroundColor);
								else
									chat.appendToPane(name + " ", message + "\n", Color.black);
							} else {
								boolean firstLine = true;
								msgParts.add(entireMessage);
								for (String s : msgParts) {
									if (firstLine) {
										if (name.equals(playerName))
											chat.appendToPane("Vous ", s + "\n", backgroundColor);
										else
											chat.appendToPane(name + " ", s + "\n", Color.black);
									} else {
										if (name.equals(playerName))
											chat.appendToPane("", s + "\n", backgroundColor);
										else
											chat.appendToPane("", s + "\n", Color.black);
									}
									firstLine = false;
								}
							}

							// Automatically brings down the JscrollBar
							chat.getScrollPane().getVerticalScrollBar()
									.setValue(chat.getScrollPane().getVerticalScrollBar().getMaximum());
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		updateMessages.start();

		// Check if receives an object
		Thread clientMessages = new Thread() {
//			public void run() {
//				Socket soc = null;
//				try {
//					soc = this.serverSocket.accept();
//					this.server = new Server();
//
//					// Asks the player to enter his pseudo, while it's respets the caracteristics
//					// and is not already used
//					String newPlayerName = null;
//					newPlayerName = null;
//					while (newPlayerName == null || newPlayerName.length() >= 18 || newPlayerName.length() <= 0
//							|| server.isTheNameAlreadyUsed(newPlayerName)) {
//						newPlayerName = JOptionPane.showInputDialog(null,
//								"Entrez votre nom :\n- 18 caractères max\n- Nom unique", "Pseudo du joueur",
//								JOptionPane.INFORMATION_MESSAGE);
//					}
//
//					// Adds the first player to the game
//					// Player player = new Player(newPlayerName);
//					this.server.addClient(soc, newPlayerName);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//				// While players didn't decide to start the game
//				while (!this.playersDecidedToStartTheGame) {
//
//					// Waiting for other players
//					try {
//						soc = this.serverSocket.accept();
//
//						// Asks the player to enter his pseudo, while it's respets the caracteristics
//						// and is not already used
//						String newPlayerName = null;
//						while (newPlayerName == null || newPlayerName.length() >= 18 || newPlayerName.length() <= 0
//								|| server.isTheNameAlreadyUsed(newPlayerName)) {
//							newPlayerName = JOptionPane.showInputDialog(null,
//									"Entrez votre nom :\n- 18 caractères max\n- Nom unique", "Pseudo du joueur",
//									JOptionPane.INFORMATION_MESSAGE);
//						}
//
//						// Adds another client to the game
//						// Player player = new Player(newPlayerName);
//						this.server.addClient(soc, playerName);
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				}
//			}

			public void run() {
				try {
					Object obj = null;
					while (true) {
						try {
							obj = objInput.readObject();

							// à modifier
							if (obj instanceof String) {
								String data = obj.toString().trim();

								// If the server authorizes the initComponents call to initialize the frame
								if (data.equalsIgnoreCase("initComponents")) {
									// initComponents();
								} else {

								}
							} else {
//								String data = obj.toString().trim();
//
//								for (int i = 0; i < Window.this.clientSocket.size(); i++) {
//									(new BufferedWriter(
//											new OutputStreamWriter(Window.this.clientSocket.get(i).getOutputStream())))
//													.write(String.valueOf(data + "\r\n"));
//									Window.this.clientSocket.get(i).getOutputStream().flush();
//								}
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		clientMessages.start();
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

	public void playersDecidedToStartTheGame() {
		if (this.serverSocket != null)
			this.thread.interrupt();
		this.playersDecidedToStartTheGame = true;
		this.changeCenterPanel();
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