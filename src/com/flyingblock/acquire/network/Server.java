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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that will launch a server and communicate with multiple clients to
 * run an acquire game.
 * @author Nicholas Maltbie
 */
public abstract class Server 
{
    /**
     * Server socket to connect with the minions, I mean users.
     */
    private ServerSocket server;
    /**
     * List of clients.
     */
    private HashSet<ClientThread> clients;
    /**
     * Port for the server.
     */
    private int port;
    
    /**
     * Constructs a server that can read and write to its clients.
     * @param port port number for the server.
     */
    public Server(int port)
    {
        clients = new HashSet<>();
        this.port = port;
    }
    
    /**
     * Starts the server.
     */
    public void start()
    {
        try {
            server = new ServerSocket(port);
            while(true)
            {
                
                ClientThread client = new ClientThread(server.accept(), this);
                clients.add(client);
                joinedNetwork(client.getSocket());
                client.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets the number of clients connected to the server.
     * @return Returns the number of clients connected.
     */
    public int getConnected()
    {
        return clients.size();
    }
    
    /**
     * Gets the clients connected to the server.
     * @return Returns the HashSet of sockets connected to this server.
     */
    public HashSet<ClientThread> getClients()
    {
        return clients;
    }
    
    /**
     * Returns a client based on the channel that the client is connected to.
     * @param socket Socket that is being checked.
     * @return Returns the client connected through that channel.
     */
    public ClientThread getClient(Socket socket)
    {
        for(ClientThread thread : clients)
        {
            if(thread.getSocket().equals(socket))
                return thread;
        }
        return null;
    }
    
    /**
     * Method called whenever a client sends a object to this server.
     * @param client Client that send the message.
     * @param message Message sent.
     */
    abstract public void objectRecieved(Socket client, Object message);
    /**
     * Method called whenever a client joins the network. This happens before
     * the client is added to the list of clients.
     * @param client Client that joined.
     */
    abstract public void joinedNetwork(Socket client);
}
