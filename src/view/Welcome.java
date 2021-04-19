package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Welcome extends JPanel {

	/**
	 * Generated serial version ID.
	 */
	private static final long serialVersionUID = -5711057253134386117L;

	private Image logo;
	private BufferedImage bfLogo;
	private Color backgroundColor;

	public Welcome(Image logo, Color backgroundColor) {
		if (logo == null || backgroundColor == null)
			throw new IllegalArgumentException("Welcome: Welcome(Image, Color): parameter null.");
		else {
			this.logo = logo;
			this.backgroundColor = backgroundColor;

			// Draws the logo on another image
			this.bfLogo = null;
			try {
				this.bfLogo = ImageIO.read(ClassLoader.getSystemResource("whiteBG.png"));
				Graphics2D g2d = this.bfLogo.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g2d.drawImage(this.logo, this.bfLogo.getWidth() / 2 - this.logo.getWidth(null) / 2,
						this.bfLogo.getHeight() / 2 - this.logo.getHeight(null) / 2, null);
				g2d.dispose();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Initializes components
			this.initComponents();
		}
	}

	private void initComponents() {
		Font fontWelcome = new Font("Calibri", Font.BOLD, 50);
		Font fontBegin = new Font("Calibri", Font.PLAIN, 30);

		JLabel labelWelcome = new JLabel("Bienvenue au Loup-Garou !");
		labelWelcome.setFont(fontWelcome);
		labelWelcome.setForeground(Color.white);
		labelWelcome.setHorizontalAlignment(JLabel.CENTER);
		JLabel labelBegin = new JLabel("Tapez /begin pour commencer...");
		labelBegin.setFont(fontBegin);
		labelBegin.setForeground(Color.white);
		labelBegin.setHorizontalAlignment(JLabel.CENTER);
		JPanel labelsSouth = new JPanel();
		labelsSouth.setLayout(new BorderLayout());
		labelsSouth.setBackground(this.backgroundColor);
		JPanel labelsTextPanel = new JPanel();
		labelsTextPanel.setLayout(new GridLayout(2, 1));
		labelsTextPanel.setBackground(this.backgroundColor);
		labelsTextPanel.add(labelWelcome);
		labelsTextPanel.add(labelBegin);
		JLabel emptyLabel1 = new JLabel(" ");
		emptyLabel1.setFont(fontWelcome);
		JLabel emptyLabel2 = new JLabel(" ");
		emptyLabel2.setFont(fontBegin);
		labelsSouth.add(labelsTextPanel, BorderLayout.NORTH);
		labelsSouth.add(emptyLabel1, BorderLayout.CENTER);
		labelsSouth.add(emptyLabel2, BorderLayout.SOUTH);

		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new BorderLayout());
		imagePanel.setBackground(this.backgroundColor);
		imagePanel.add(new JLabel(new ImageIcon(this.bfLogo)), BorderLayout.CENTER);

		this.setLayout(new BorderLayout());
		this.add(imagePanel, BorderLayout.CENTER);
		this.add(labelsSouth, BorderLayout.SOUTH);
	}
}
