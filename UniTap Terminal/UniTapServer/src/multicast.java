

import java.net.*;


public class multicast implements Runnable{

	public gui parent;
	public MulticastSocket socket;
	
	public multicast(MulticastSocket s, gui p){
		parent=p;
		socket=s;
	}
	
	public void run(){
		try{
			parent.updateArchives("MulticastSocket waiting for messages ");
			while(true){
				byte[] buf =new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				
				String m = new String(packet.getData(), 0, packet.getLength());
				parent.updateArchives(m);
			}
		}catch(Exception ex){
			
		}
	}
	
}
