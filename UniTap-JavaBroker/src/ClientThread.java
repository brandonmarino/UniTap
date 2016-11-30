
import java.net.*;
import java.io.*;

public class ClientThread extends Thread {
	Socket connection;
	Gui parent;
	
	public ClientThread(Socket c, Gui g){
		connection=c;
		parent=g;
	}
	public void sendToArduino(byte[] apdu){
		try{
			String ip = parent.rec_ipAdd.getText();
			int port = Integer.parseInt(parent.rec_port.getText());

			Socket s = new Socket(ip, port);
			DataOutputStream out = new DataOutputStream(s.getOutputStream());

			// Sending Array of bytes
			out.write(apdu);
			out.flush();

			parent.updateArchives("Server response has been relayed to the Terminal.");
			s.close();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	
	public void run(){
		try{

			BufferedReader b = new BufferedReader( new InputStreamReader ( connection.getInputStream() ) );
			while(true){
				String msg = b.readLine();
	            System.out.println(msg);
				parent.updateArchives( msg);
				
				/*
		         *	Try reading Bytes 
		         */
				DataInputStream in = new DataInputStream (new BufferedInputStream (connection.getInputStream()));
		        byte[] bytes = new byte [31];
				in.read(bytes);
				parent.updateArchives(byteArrayToHex(bytes));
		
			}
		}catch(Exception e){
			System.out.println("Exception in client thread");
			
		}
	}


	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}
}
