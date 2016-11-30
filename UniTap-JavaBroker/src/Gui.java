
import javax.swing.*;

import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;

import java.awt.BorderLayout;


public class Gui extends JFrame {
	JTextField your_ipAdd, your_name, your_message, your_port;
	JTextField rec_ipAdd, rec_name, rec_port;
	JTextPane messages;
	Gui self;
	boolean multicastServerStarted=false;
	
	public Gui(){
		self=this;
		rec_port = new JTextField("2016");
		rec_port.setEditable(false);
		rec_name = new JTextField("Terminal 1");
		rec_name.setEditable(false);
		try{
			rec_ipAdd = new JTextField(InetAddress.getLocalHost().getHostAddress());	
			your_ipAdd = new JTextField(InetAddress.getLocalHost().getHostAddress());
			your_ipAdd.setEditable(false);
			
		}catch(Exception e){
			your_ipAdd = new JTextField("Unable to identify your address");
			your_ipAdd.setEditable(false);
		}
		
		your_name = new JTextField("Officer");
		your_name.setEditable(false);
		your_port = new JTextField("2015");
		your_port.setEditable(false);
		your_message = new JTextField();

		JPanel main = new JPanel();
		main.setLayout(new GridLayout(5,2));
		
		JTabbedPane tabs = new JTabbedPane();
		
		JPanel Terminals = new JPanel();
		Terminals.setLayout(new GridLayout(2,2));
		
		JPanel Desks = new JPanel();
		Desks.setLayout(new GridLayout(2,2));
	
		tabs.addTab("Terminals", Terminals);
	    tabs.addTab("Desks", Desks);
		
		main.add(tabs);
		
		Terminals.add( setRow("Access Gate", rec_name));
		Terminals.add( setRow("IP address", rec_ipAdd));
		//main.add( setRow("Recipient port", rec_port));
		//main.add(new JSeparator(SwingConstants.VERTICAL));
		Desks.add( setRow("Front Desk", your_name));
		Desks.add( setRow("Front IP Address", your_ipAdd));
		
		JLabel grant = new JLabel("Grant Access");

		main.add(grant);

		JButton on = new JButton("ON");
		on.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
                String ip = rec_ipAdd.getText();
                String user = rec_name.getText();
                String message = your_message.getText();
                int port = Integer.parseInt(rec_port.getText());
                your_message.setText("");
                try{
            		Socket s = new Socket(ip, port);
            		DataOutputStream out = new DataOutputStream(s.getOutputStream());
            		
            		 // Sending Array of bytes
            		 byte[] msg = {0x0,0x0,0x0,0x5,0x1};
            		 out.write(msg);

            		//out.writeBytes("ON");                		                		
            		out.flush();
            		
            		updateArchives( "Server Response has been sent " + user + " : " + "ON\n");
            		s.close();
            	}catch(Exception ex){   
            		System.out.println(ex.getMessage());
            	}                                
            }
        });
		JButton off = new JButton("OFF");
		off.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                String ip = rec_ipAdd.getText();
                String user = rec_name.getText();
                String message = your_message.getText();
                int port = Integer.parseInt(rec_port.getText());
                your_message.setText("");
                try{
            		Socket s = new Socket(ip, port);
            		DataOutputStream out = new DataOutputStream(s.getOutputStream());
            		// Sending Array of dummy bytes
					byte[] msg = {0x0,0x0,0x0,0x5,0x0};
            		out.write(msg);
            		//out.writeBytes("OFF");                		                		
            		out.flush();
            		updateArchives( "Server Response has been sent" + user + " : " + "OFF");
            		s.close();
            	}catch(Exception ex){   
            		System.out.println(ex.getMessage());
            	}                                
            }
        });
		
		JButton start = new JButton("Start Server");
		start.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
            	JButton start = (JButton) e.getSource();
                start.setEnabled(false);
        		new Thread(new Runnable(){
        			public void run(){
		                /*
		                 * Start the server
		                 */
        				
        				try{
        					int port = Integer.parseInt( your_port.getText());
        					your_port.setEditable(false);
        					
        					if(your_name.isEditable())
                            	your_name.setEditable(false);
        					
        					ServerSocket server = new ServerSocket(port);
        					updateArchives("Server Started ");
        					updateArchives("Waiting for connections ");
        					while(true){
                                System.out.print(".");
        						Socket client = server.accept();
        						ClientThread ct = new ClientThread(client, self);
        						ct.start();
        					}
        				}catch(Exception e) {
							System.out.println(e.getMessage());
						}
		                
		                /*
		                 * End the server
		                 */
        				
        				
        				/*	
        				 *  Multicast your info to the network
        		         */
						try{
							MulticastSocket socket = new MulticastSocket(4015);
        		            InetAddress address = InetAddress.getByName("230.0.0.1");
        		            socket.joinGroup(address);
        		                    
        		            if(your_name.isEditable())
        		              	your_name.setEditable(false);
        		                    
        		            String info = your_ipAdd.getText() +"/"+ your_port.getText() + "/" + your_name.getText();
        		           
        		            byte[] buf = info.getBytes();
        		               
        		       		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4015);
        		            socket.send(packet);
        		            socket.close();
        		            updateArchives("Send info to users");
        		           if(multicastServerStarted==false){
        			            multicastServerStarted=true;
        					   	new Thread(new Runnable(){
        					   		public void run(){
        					   			try{
        					   				MulticastSocket socket = new MulticastSocket(4015);
        					                InetAddress address = InetAddress.getByName("230.0.0.1");
        					                socket.joinGroup(address);
        					           		updateArchives("MulticastSocket waiting for messages ");
        					           		while(true){
        					           			byte[] buf =new byte[256];
        					           			DatagramPacket packet = new DatagramPacket(buf, buf.length);
        					           			socket.receive(packet);

        					           			String m = new String(packet.getData(), 0, packet.getLength());
        					           			updateArchives(m);
        					           		}
										}catch(Exception ex){
        					           		System.out.println("==="+ex.getMessage());
										}
									}
								}).start();
						   }
						}catch(Exception ex){
        		            		System.out.println(ex.getMessage());
						}
						/*
        		         * End of Multicast
        		         */
        			}
        		}).start();
            }
        });		
		JPanel Buttonspanel = new JPanel();
		Buttonspanel.setLayout(new GridLayout(2,1));
		Buttonspanel.add(on);
		Buttonspanel.add(off);
		//p.add(start);
//		p.add(broadcast);
		Buttonspanel.add(start, BorderLayout.CENTER);
		
		main.add(Buttonspanel);
		
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add(main, BorderLayout.WEST);
		
		messages = new JTextPane();
		messages.setEditable(false);
		messages.setContentType("text/html");
		pan.add(new JScrollPane(messages), BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(500, 500));
		add(pan);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	
	public JPanel setRow(String mess, JTextField f){
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,2));
		p.add(new JLabel(mess));
		p.add(f);
		return p;
	}
	
	
	
	public void updateArchives(String message){
		try{
			StyledDocument doc = messages.getStyledDocument();
			
			int end = doc.getLength();
			doc.insertString(end, message+"\n", null );
		}catch(Exception e){}
	}
	
	public static void main(String args[]) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                //Turn off metal's use of bold fonts
	                UIManager.put("swing.boldMetal", Boolean.FALSE);
                	Gui g  = new Gui();
	            }
	        });
	    }
}
