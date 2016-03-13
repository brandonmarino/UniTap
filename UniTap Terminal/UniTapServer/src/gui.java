
import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import java.util.HashMap;
import java.io.*;
import javax.swing.text.*;


public class gui extends JFrame {
	JTextField your_ipAdd, your_name, your_message, your_port;
	JTextField rec_ipAdd, rec_name, rec_port;
	JTextPane messages;

	
	
	gui self;
	
	boolean multicastServerStarted=false;
	
	public gui(){
		
		self=this;
		
		
							
		rec_port = new JTextField("2016");	
		rec_name = new JTextField();
		
		
		
		try{
			rec_ipAdd = new JTextField(InetAddress.getLocalHost().getHostAddress());	
			your_ipAdd = new JTextField(InetAddress.getLocalHost().getHostAddress());
			your_ipAdd.setEditable(false);
			
		}catch(Exception e){
			your_ipAdd = new JTextField("Unable to identify your address");
			your_ipAdd.setEditable(false);
		}
		
		your_name = new JTextField();
		your_port = new JTextField("2015");
		your_message = new JTextField();
		
		
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(9,1));
		
		main.add( setRow("Recipient IP address", rec_ipAdd));
		main.add( setRow("Recipient port", rec_port));
		main.add( setRow("Recipient name", rec_name));
		main.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		main.add( setRow("Your IP Address", your_ipAdd));
		main.add( setRow("Your port", your_port));
		main.add( setRow("Your name", your_name));
		main.add( setRow("Your message", your_message));
		
		JButton broadcast = new JButton("Multicast my info");
		
		broadcast.addActionListener(new ActionListener() {
 
            public void actionPerformed(ActionEvent e)
            {
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
            }
        });
		JButton send = new JButton("Send message");
		send.addActionListener(new ActionListener() {
 
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
            		
            		
            		message m = new message(port, user);
            		m.setMessage(message);
            		out.writeBytes(m.getMessage());                		                		
            		out.flush();
            		
            		updateArchives( "sent to " + user + " : " + message);
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
        				try{
        					int port = Integer.parseInt( your_port.getText());
        					your_port.setEditable(false);
        					
        					if(your_name.isEditable())
                            	your_name.setEditable(false);
        					
        					ServerSocket server = new ServerSocket(port);
        					updateArchives("Server Started ");
        					updateArchives("Waiting for connections ");
        					while(true){
        						
        						Socket client = server.accept();
        			
        						clientThread ct = new clientThread(client, self);
        						ct.start();
        					}
        				}catch(Exception e){
        					System.out.println(e.getMessage());
        					
        				}
        			}
        		}).start();               
                
            }
        });		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,3));
		p.add(start);
		p.add(broadcast);
		p.add(send);
		main.add(p);
		
		
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		pan.add(main, BorderLayout.NORTH);
		
		
		messages = new JTextPane();
		messages.setEditable(false);
		messages.setContentType("text/html");
		pan.add(new JScrollPane(messages), BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(800, 500));
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
                	gui g  = new gui();                	
	            }
	        });
	    }
}
