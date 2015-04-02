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

import java.net.Socket;

/**
 *
 * @author Nicholas Maltbie
 */
public class TestServer extends Server
{

    public TestServer(int port) 
    {
        super(port);
    }

    @Override
    public void objectRecieved(Socket client, Object message)
    {
        System.out.println(client + " sent " + message);
        this.getClient(client).sendData(message);
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        System.out.println(client + " joined the network");
    }
    
    static public void main(String[] args)
    {
        TestServer server = new TestServer(44);
        System.out.println("Server starting...");
        server.start();
    }
    
}
