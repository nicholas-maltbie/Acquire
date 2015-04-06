package com.flyingblock.acquire.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that allows for server communication with a client.
 * @author Nicholas Maltbie.
 */
public class ClientThread extends Thread
{
    /**
     * Socket that the client sits in.
     */
    private Socket client;
    /**
     * Steam to receive objects from the client.
     */
    private ObjectInputStream inputStream;
    /**
     * Stream to send object to the client.
     */
    private ObjectOutputStream outputStream;
    /**
     * Server to communicate data to.
     */
    private Server server;

    /**
     * Constructs a client thread.
     * @param client Socket for the client.
     * @param server Server the client is connected to.
     */
    public ClientThread(Socket client, Server server) {
        this.client = client;
        this.server = server;
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the socket of the client.
     * @return Returns the socket for the client.
     */
    public Socket getSocket()
    {
        return client;
    }

    /**
     * Sends an object to the client.
     * @param message Message to send to the client.
     */
    public void sendData(Object message)
    {
        try {
            outputStream.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stops listening and sending messages to the client and then closes the 
     * socket.
     */
    public void disconnect()
    {
        run = false;
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Are we listening to the client?
     */
    private boolean run = true;
    
    @Override
    public void run()
    {
        Object input;
        while(run)
        {
            try {
                if((input = inputStream.readObject()) != null)
                {
                    server.objectRecieved(client, input);
                }
            } catch (IOException ex) {
                //client has disconnected. tell server and bail.
                server.leaveNetwork(this);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}