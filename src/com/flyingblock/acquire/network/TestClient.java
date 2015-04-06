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
public class TestClient extends Client
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
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    client.start();
                }
            }, 0);
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scanner scan = new Scanner(System.in);
        while(true)
        {
            String next = scan.nextLine();
            if(next.equalsIgnoreCase("DIE"))
            {
                System.out.println("Leaving the server");
                client.disconnect();
                scan.close();
                System.exit(0);
            }
            else
                client.sendObject(next);
        }
    }

    @Override
    public void disconnectedFromServer() {
        System.out.println("We're not in Kansas anymore, or connected to the server for that matter.");
        System.exit(0);
    }
    
}
