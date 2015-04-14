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
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maltbie_N
 */
public class TestClient extends AbstractClient
{

    public TestClient(String address, int port)
    {
        super(address, port);
    }

    @Override
    public void objectRecieved(Object object) 
    {
        System.out.println("Server: " + object);
    }
    
    public static void main(String[] args)
    {
        String address = "localhost";
        try {
            address = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        TestClient client = new TestClient(address, 44);
        client.start();
        Scanner scan = new Scanner(System.in);
        while(client.isConnected())
        {
            String next = scan.nextLine();
            if(next.equalsIgnoreCase("DIE"))
            {
                System.out.println("Leaving the server");
                client.disconnect();
                scan.close();
                return;
            }
            else
                client.sendObject(next);
        }
    }

    @Override
    public void disconnectedFromServer() {
        System.out.println("We're not in Kansas anymore, or connected to the server for that matter.");
    }
    
}
