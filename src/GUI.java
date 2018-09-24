
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;




public class GUI {

	final JFileChooser fc = new JFileChooser();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					//window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the a
	 * pplication.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUI_Background bcgnd = new GUI_Background();
		bcgnd.setResizable(false);
	}
}



