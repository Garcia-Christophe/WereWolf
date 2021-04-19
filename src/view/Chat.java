package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.MatteBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import controller.MessageToSendKeyListener;

/**
 * 
 * @author Christophe
 *
 */
public class Chat extends JPanel {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 8844853108184020645L;

	// Local
	private BufferedWriter writer;

	// Interface
	private Font fontText;
	private JLabel nameDisplay;
	private JScrollPane scrollPane;
	private JTextPane messageArea;
	private JLabel recipient;
	private JTextField messageBeingWritten;
	private Color specificBlue;

	// Other
	private String playerName;
	private Window window;

	/**
	 * ChatAuto constructor creates a new form of Chat.
	 */
	public Chat(String playerName, Window window, BufferedWriter writer) {
		if (playerName == null || window == null || writer == null)
			throw new IllegalArgumentException("Chat: Chat(String, Window, BufferedWriter): parameter null.");
		else {
			this.playerName = playerName;
			this.window = window;
			this.writer = writer;

			// Initialize the components
			this.initComponents();
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents() {
		this.fontText = new Font("Calibri", Font.PLAIN, 20);
		Font fontName = new Font("Calibri", Font.BOLD, 30);
		Font fontRecipient = new Font("Calibri", Font.BOLD, 20);
		this.specificBlue = new Color(63, 81, 181);

		this.nameDisplay = new JLabel(this.playerName.toUpperCase());
		this.nameDisplay.setFont(fontName);
		this.nameDisplay.setForeground(this.specificBlue);

		JPanel panelContainingThePlayerName = new JPanel();
		panelContainingThePlayerName.add(this.nameDisplay);
		panelContainingThePlayerName.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
		panelContainingThePlayerName.setBackground(Color.white);

		this.messageArea = new JTextPane();
		this.messageArea.setFont(this.fontText);
		this.messageArea.setEditable(false);
		this.scrollPane = new JScrollPane(this.messageArea);

		this.recipient = new JLabel();
		this.recipient.setFont(fontRecipient);
		this.recipient.setPreferredSize(new Dimension(70, 40));
		this.recipient.setHorizontalAlignment(JLabel.CENTER);
		this.setTextRecipient(1);

		this.messageBeingWritten = new JTextField();
		this.messageBeingWritten.setMaximumSize(new Dimension(410, 40));
		this.messageBeingWritten.setPreferredSize(new Dimension(410, 40));
		this.messageBeingWritten.setFont(this.fontText);
		this.messageBeingWritten.addKeyListener(new MessageToSendKeyListener(this, this.writer));
		this.messageBeingWritten.setFocusTraversalKeysEnabled(false);

		JPanel recipientTextAndMessageToSend = new JPanel();
		recipientTextAndMessageToSend.setLayout(new FlowLayout());
		recipientTextAndMessageToSend.setBackground(new Color(220, 220, 220));
		recipientTextAndMessageToSend.add(this.recipient);
		recipientTextAndMessageToSend.add(this.messageBeingWritten);

		this.setLayout(new BorderLayout());
		this.add(panelContainingThePlayerName, BorderLayout.NORTH);
		this.add(this.scrollPane, BorderLayout.CENTER);
		this.add(recipientTextAndMessageToSend, BorderLayout.SOUTH);
	}

	public Window getWindow() {
		return this.window;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}

	public JTextPane getMessageArea() {
		return this.messageArea;
	}

	public JTextField getMessageBeingWritten() {
		return this.messageBeingWritten;
	}

	/**
	 * Informs the player that his recipient has changed.
	 * 
	 * @param number "Tous :" (1), "Loups :" (2), "Morts :" (3)
	 */
	public void setTextRecipient(int number) {
		if (number < 1 || number > 3)
			throw new IllegalArgumentException("Chat: setTextRecipient(int): parameter must be 1, 2 or 3.");
		else {
			if (number == 1) {
				this.recipient.setText("Tous :");
				this.recipient.setForeground(this.specificBlue);
			} else if (number == 2) {
				this.recipient.setText("Loups :");
				this.recipient.setForeground(Color.red);
			} else {
				this.recipient.setText("Morts :");
				this.recipient.setForeground(Color.gray);
			}
		}
	}

	public void appendToPane(String playerName, String msg, Color c) {
		if (playerName == null || msg == null || c == null) {
			throw new IllegalArgumentException("Chat: appendToPane(String, String, Color): parameter null.");
		} else {
			this.messageArea.setEditable(true);
			for (int i = 0; i < 2; i++) {
				StyleContext sc = StyleContext.getDefaultStyleContext();
				AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

				if (i == 0)
					aset = sc.addAttribute(aset, StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
				else
					aset = sc.addAttribute(aset, StyleConstants.CharacterConstants.Bold, Boolean.FALSE);
				aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Calibri");
				aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

				int len = this.messageArea.getDocument().getLength();
				this.messageArea.setCaretPosition(len);
				this.messageArea.setCharacterAttributes(aset, false);
				if (i == 0)
					this.messageArea.replaceSelection(playerName);
				else
					this.messageArea.replaceSelection(msg);
			}
		}
		this.messageArea.setEditable(false);
	}

	public void gameLaunched() {
		try {
			this.writer.write("5¤" + "¤" + this.playerName + " a lancé la partie.\r\n");
			this.writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
