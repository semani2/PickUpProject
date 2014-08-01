package pickupServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class TCPServer extends Thread {
	
	// Variables Section
	public static final int SERVERPORT = 4444;
	public boolean running = false;
	private PrintWriter mOut;
	private OnMessageReceived messageListener;
	
	/**
     * Constructor of the class
     * @param messageListener listens for the messages
     */
	public TCPServer(OnMessageReceived messageListener)
	{
		this.messageListener = messageListener;
	}
	
	/**
     * Method to send the messages from server to client
     * @param message the message sent by the server
     */
	public void sendMessage(String message){
		if(mOut != null && !mOut.checkError()){
			mOut.println(message); 
			mOut.flush();
		}
	}
	
	@Override
	public void run(){
		super.run();
		
		running = true;
		
		try{
			System.out.println("\nPickUp Server connecting...");
			
			// Lets create a server socket
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);
			
			// Let's create a client socket
			Socket client = serverSocket.accept();
			System.out.println("PickUp Server Receiving...");
			try{
				//send message to client
				mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
				
				
				//read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
 
                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {
                    String message = in.readLine();
 
                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                    }
                }
			}
			catch(Exception e)
			{
				 System.out.println("Server: Error");
	                e.printStackTrace();
			}
			finally{
				client.close();
                System.out.println("Server: Done.");
			}
		}
		catch(Exception e)
		{
			System.out.println("Server: Error");
            e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		// Let's open a window here to send and receive messages
		//opens the window where the messages will be received and sent
        ServerBoard frame = new ServerBoard();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

	}

	
	//Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}
