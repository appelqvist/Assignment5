
package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The GUI for assignment 5
 */
public class GUIServer
{
	/**
	 * These are the components you need to handle.
	 * You have to add listeners and/or code
	 */

	private Server server;

	private JFrame frame;				// The MainClient window
	private JTextField txt;				// Input for text to send
	private JButton btnSend;			// Send text in txt
	private JTextArea lstMsg;			// The logger listbox

	/**
	 * Constructor
	 */
	public GUIServer(Server server) {
		this.server = server;
	}
	
	/**
	 * Starts the application
	 */
	public void Start()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 300,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("Server");			// Change to "Multi Chat Server" on server part and vice versa
		InitializeGUI();					// Fill in components
		frame.setVisible(true);
		frame.setResizable(false);			// Prevent user from change size
	}
	
	/**
	 * Sets up the GUI with components
	 */
	private void InitializeGUI()
	{
		txt = new JTextField();
		txt.setBounds(13,  13, 177, 23);
		frame.add(txt);
		btnSend = new JButton("Send");
		btnSend.setBounds(197, 13, 75, 23);
		frame.add(btnSend);
		lstMsg = new JTextArea();
		lstMsg.setEditable(false);
		JScrollPane pane = new JScrollPane(lstMsg);
		pane.setBounds(12, 51, 260, 199);
		pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.add(pane);


		btnSend.addActionListener(new ClickListener());
	}

	/**
	 * Skriver en rad i gui
	 * @param str
	 */
	public void writeRow(String str){
		lstMsg.append(str);
	}

	/**
	 * ClickListeners
	 */
	private class ClickListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == btnSend){
				server.sendMessageToClients(txt.getText());
			}
		}
	}
}
