
import java.net.*;
import java.io.*;
import javax.swing.*;




public class clientThread extends Thread {
	Socket connection;
	
	gui parent;
	
	public clientThread(Socket c,  gui g){
		connection=c;
		
		parent=g;
	}
	public void send(String message){
		
	}
	
	
	public void run(){
		try{
			
			/*
			 * ObjectInputStream in = new ObjectInputStream( connection.getInputStream());
			 */
			
			   BufferedReader b = new BufferedReader( new InputStreamReader ( connection.getInputStream() ) );
		        /*
		         * 
		         */
			
			while(true){
				//message s = (message) in.readObject();
				String msg = b.readLine();
	            System.out.println(msg);
	            
				parent.updateArchives( msg);
			}
		}catch(Exception e){
			System.out.println("Exception in client thread");
			
		}
	}

}
