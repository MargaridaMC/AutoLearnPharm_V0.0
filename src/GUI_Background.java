
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.filechooser.*;



public class GUI_Background extends JFrame {

	private Mix myMix; // mix
	private Sample mysp; //sample
	private Working myWork;
	private FileInputStream open_sp;
	private FileInputStream open_theta;

	final JFileChooser file_theta;
	final JFileChooser file_sp;

	JButton B_sp;
	JButton B_run;
	JButton B_theta;
	JButton B_Save;


	public GUI_Background() {
		setSize(1000, 585);
		setTitle("AMC.project");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		java.net.URL imgdir = GUI_Background.class.getResource("resources/GUI_Background1334.png");
		ImageIcon img = new ImageIcon(imgdir);
		JLabel background = new JLabel(img);
		add(background);
		background.setLayout(null);

		B_sp = new JButton("");
		B_sp.setBounds(170, 475, 110, 110);
		B_sp.setOpaque(false);
		B_sp.setContentAreaFilled(false);
		B_sp.setBorderPainted(false);
		background.add(B_sp);
		
		B_Save = new JButton("");
		B_Save.setBounds(810, 280, 140, 140);
		B_Save.setOpaque(false);
		B_Save.setContentAreaFilled(false);
		B_Save.setBorderPainted(false);
		background.add(B_Save);

		B_theta = new JButton("");
		B_theta.setBounds(170, 175, 110, 110);
		B_theta.setOpaque(false);
		B_theta.setContentAreaFilled(false);
		B_theta.setBorderPainted(false);
		background.add(B_theta);

		B_run = new JButton("");
		B_run.setBounds(520, 280, 140, 140);
		B_run.setOpaque(true);
		B_run.setContentAreaFilled(false);
		B_run.setBorderPainted(false);
		background.add(B_run);

		setSize(1334, 750);
		
			
		file_sp = new JFileChooser();
		file_sp.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		B_sp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				//window that allows to choose the desired sample file
				int filetest = file_sp.showOpenDialog(file_sp);

				if (filetest == JFileChooser.APPROVE_OPTION) {
					File file = file_sp.getSelectedFile();
					String filename = file.getPath();
					try {
						mysp =new Sample ();
						open_sp = new FileInputStream(filename);
						mysp.readSample(filename);

					} catch (Exception e1) {
						 //Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});
		


		file_theta = new JFileChooser();
		file_theta.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		B_theta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int filetest = file_theta.showOpenDialog(file_theta);
				if (filetest == JFileChooser.APPROVE_OPTION) {
					File file = file_theta.getSelectedFile();
					String filename = file.getPath();
					try {
						open_theta = new FileInputStream(filename);
						myMix.mixread(filename);// reads and creates the Gaussians Mix with the input values

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		B_run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				RunMe run = new RunMe ();
				myWork = new Working(mysp, myMix);
				run.expectationMaximization(myWork);
				java.awt.Toolkit.getDefaultToolkit().beep();
				JOptionPane ran = new JOptionPane();
				JOptionPane.showMessageDialog(ran, "The computational process has finished. If your wish to see the graphical representation of the curves, please refer to the plot option");
			}
		});
		
		
		
		B_Save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			myWork.workMix.mixwrite("output.txt");
			
				
			}			
		});
	}
	
}