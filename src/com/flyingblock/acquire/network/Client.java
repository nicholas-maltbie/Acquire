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
    
    /**
     * Constructs a client and connects to a server.
     * @param address Address to connect to.
     * @param port port that the server is running on.
     */
    public Client(String address, int port)
    {
        try {
            server = new Socket(address, port);
            
            ObjectInputStream input = new ObjectInputStream(server.getInputStream());
            
            Object obj = input.readObject();
            
            System.out.println(obj);
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
