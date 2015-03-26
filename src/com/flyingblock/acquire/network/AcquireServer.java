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

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import servercomms.Server;

/**
 * A class that will launch a server and communicate with multiple clients to
 * run an acquire game.
 * @author Nicholas Maltbie
 */
public class AcquireServer 
{
    /**
     * Server socket to connect with the minions, I mean users.
     */
    private ServerSocket server;
    
    private HashSet<AcquireServer.ClientThread> clients;
    
    public AcquireServer(int port)
    {
        clients = new HashSet<>();
        try {
            ServerSocket server = new ServerSocket(port);
            while(true)
            {
                AcquireServer.ClientThread client = new AcquireServer.ClientThread(server.accept());
                clients.add(client);
                client.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static class ClientThread extends Thread
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
         * Constructs a client thread.
         * @param client Socket for the client.
         */
        public ClientThread(Socket client) {
            this.client = client;
            try {
                inputStream = new ObjectInputStream(client.getInputStream());
                outputStream = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(AcquireServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        @Override
        public void run()
        {
            while(inputStream.readO)
        }
    }
}
