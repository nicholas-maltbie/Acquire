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
    
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
    
    }
    
    static public void main(String[] args)
    {
        TestServer server = new TestServer(44);
        server.start();
    }
    
}
