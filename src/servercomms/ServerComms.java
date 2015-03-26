/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servercomms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maltbie_N
 */
public class ServerComms {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InetAddress i = null;
        try {
            i = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerComms.class.getName()).log(Level.SEVERE, null, ex);
        }
           System.out.println(i);                  // host name and IP address
           System.out.println(i.getHostName());    // name
           System.out.println(i.getHostAddress()); // IP address only
    }
    
}
