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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that will launch a server and communicate with multiple clients to
 * run an acquire game.
 * @author Nicholas Maltbie
 */
public abstract class AbstractServer 
{
    /**
     * AbstractServer socket to connect with the minions, I mean users.
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
     * Whether or not to reject new arrivals.
     */
    private boolean acceptUsers = true;
    private boolean running = true;
    
    /**
     * Constructs a server that can read and write to its clients.
     * @param port port number for the server.
     */
    public AbstractServer(int port)
    {
        clients = new HashSet<>();
        this.port = port;
    }
    
    /**
     * Starts the server.
     * @throws java.io.IOException
     */
    public void start() throws IOException
    {
        server = new ServerSocket(port);
        running = true;
        while(running)
        {
            ClientThread client = new ClientThread(server.accept(), this);
            if(acceptUsers)
            {
                clients.add(client);
                joinedNetwork(client.getSocket());
                client.start();
            }
            else
            {
                client.disconnect();
                connectionRejected(client.getSocket());
            }
        }
    }
    
    /**
     * Stops the server.
     */
    public void stop() throws IOException
    {
        running = false;
        server.close();
        for(ClientThread client : clients)
        {
            client.disconnect();
        }
    }
    
    /**
     * Method called whenever a client leaves the network.
     * @param client Client that leaves.
     */
    public void leaveNetwork(ClientThread client)
    {
        try {
            client.getSocket().close();
        } catch (IOException ex) {
            Logger.getLogger(AbstractServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        clients.remove(client);
        client.disconnect();
        disconnectedFromNetwork(client.getSocket());
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
     * Gets if the server is allowing new users to connect.
     * @return Returns true if accepting new users and false if not accepting
     * new users.
     */
    public boolean isAccpeting()
    {
        return acceptUsers;
    }
    
    /**
     * Allows users to join the network.
     */
    public void acceptUsers()
    {
        acceptUsers = true;
    }
    
    /**
     * Stops accepting new users to the network.
     */
    public void stopAccepting()
    {
        acceptUsers = false;
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
     * Gets if the server is open or not.
     * @return Returns a boolean, true if the server is not closed and false
     * if the server is closed.
     */
    public boolean isRunning()
    {
        return server != null && !server.isClosed();
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
    /**
     * Method called whenever a client leaves the network. This happens after
     * the client leaves the network.
     * @param client Client that leaves.
     */
    abstract public void disconnectedFromNetwork(Socket client);
    /**
     * Method called whenever a client is rejected from the network.
     * @param client Client rejected connection
     */
    abstract public void connectionRejected(Socket client);
}
