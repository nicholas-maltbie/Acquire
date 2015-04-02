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
        TestClient client = new TestClient("192.168.1.20", 44);
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
            client.sendObject(next);
        }
    }
    
}
