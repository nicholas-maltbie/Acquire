/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that can connect to a Server
 * @author Nicholas Maltbie
 */
public abstract class Client 
{
    /**
     * Server that the client is connected to.
     */
    private Socket server;
    
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    /**
     * Constructs a client and connects to a server.
     * @param address Address to connect to.
     * @param port port that the server is running on.
     */
    public Client(String address, int port)
    {
        try {
            server = new Socket(address, port);
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            disconnect();
        }
    }
    
    private boolean running = true;
    
    /**
     * Starts listening to the server.
     */
    public void start()
    {
        while(running)
        {
            try {
                    Object message = null;
                    if((message = input.readObject()) != null)
                        objectRecieved(message);
            } catch (IOException | ClassNotFoundException ex) {
                if(running)
                {
                    //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    disconnect();
                    disconnectedFromServer();
                }
            }
        }
    }
    
    /**
     * Disconnects from the server. Will not call the disconnected from server
     * method.
     */
    public void disconnect()
    {
        running = false;
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sends an object to the server.
     * @param message Object to send. Must be Serializable.
     */
    public void sendObject(Object message)
    {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Method called whenever the server sends an object to the client.
     * @param object Object received.
     */
    abstract public void objectRecieved(Object object);
    /**
     * Called after the client unexpectedly disconnects from the server.
     */
    abstract public void disconnectedFromServer();
}
