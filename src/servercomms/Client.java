package servercomms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client 
{
    
    public Client(String address, int port)
    {
        try {
            Socket server = new Socket(address, port);
            
            InputStream stream = server.getInputStream();
            
            ObjectInputStream input = new ObjectInputStream(stream);
            
            Object obj = input.readObject();
            
            System.out.println(obj);
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args)
    {
        Client client = new Client("172.16.240.60", 67);
    }
    
}
