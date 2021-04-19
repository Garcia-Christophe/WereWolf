package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Timer extends JPanel {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = 3024473891881793605L;

	/**
	 * Downtime line.
	 */
	public JProgressBar progressBar;

	/**
	 * Creates a new form of timer.
	 */
	public Timer() {
		this.progressBar = new JProgressBar(0, 100); // test (ca change de toute manière suivant les timers dujeu)
		this.progressBar.setValue(0);
		this.progressBar.setStringPainted(true);

		this.setBackground(new Color(63, 81, 181));
		this.setLayout(new BorderLayout());
		this.add(this.progressBar, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		Timer timer = new Timer();

		JFrame frame = new JFrame("test");
		frame.setSize(700, 700);
		frame.setLayout(new BorderLayout());
		frame.add(timer, BorderLayout.CENTER);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		for (int j = 0; j <= 1000000; j++)
			timer.progressBar.setValue(j);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
