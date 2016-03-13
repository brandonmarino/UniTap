

import java.io.*;

public class message implements Serializable{
	public int port;
	public String username;
	public String message;
	
	public message(int p, String u){
		
	}
	
	public void setMessage(String s){
		message=s;
	}
	
	public String getUser(){
		return username;
	}
	public String getMessage(){
		return message;
	}
}
