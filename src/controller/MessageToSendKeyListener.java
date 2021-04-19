package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.IOException;

import view.Chat;

/**
 * 
 * @author Christophe
 *
 */
public class MessageToSendKeyListener implements KeyListener {

	private Chat chat;
	private BufferedWriter writer;

	public MessageToSendKeyListener(Chat chat, BufferedWriter writer) {
		if (chat == null || writer == null)
			throw new IllegalArgumentException(
					"MessageToSendKeyListener: MessageToSendKeyListener(Chat, BufferedWriter): parameter null.");
		else {
			this.chat = chat;
			this.writer = writer;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// If the user types TAB
		if (e.getKeyCode() == KeyEvent.VK_TAB) {
			this.chat.getMessageBeingWritten().setText("change le recipient + label");
		}

		// If the user types ENTER
		else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

			// If the message is not empty
			if (this.chat.getMessageBeingWritten().getText().length() > 0) {

				// If the users decides to begin the game by writing BEGIN
				if (this.chat.getMessageBeingWritten().getText().equalsIgnoreCase("/begin")) {
					this.chat.getWindow().playersDecidedToStartTheGame();
					this.chat.gameLaunched();
				} else {
					try {
						this.writer.write("0¤" + this.chat.getPlayerName() + "¤ : "
								+ this.chat.getMessageBeingWritten().getText() + "\r\n");
						this.writer.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				// Removes the text field because the message has already been sent.
				this.chat.getMessageBeingWritten().setText("");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
