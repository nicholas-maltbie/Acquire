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
        this.stopAccepting();
        System.out.println(client + " sent " + message);
        for(ClientThread c : this.getClients().toArray(new ClientThread[this.getConnected()]))
            c.sendData(message);
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        System.out.println(client + " joined the network");
    }
    
    static public void main(String[] args)
    {
        TestServer server = new TestServer(44);
        server.start();
    }

    @Override
    public void disconnectedFromNetwork(Socket client) {
        System.out.println(client + " left the network");
    }

    @Override
    public void connectionRejected(Socket client) {
        System.out.println(client + " was rejected");
    }
    
}
