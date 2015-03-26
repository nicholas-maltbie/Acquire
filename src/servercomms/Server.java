package servercomms;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server
{
    private HashSet<ClientThread> clients;
    
    public Server(int port)
    {
        clients = new HashSet<>();
        try {
            ServerSocket server = new ServerSocket(port);
            while(true)
            {
                ClientThread client = new ClientThread(server.accept());
                clients.add(client);
                client.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static class ClientThread extends Thread
    {
        private Socket client;
        
        public ClientThread(Socket client) {
            this.client = client;
        }
        
        @Override
        public void run()
        {
            try {
                ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
                stream.writeObject(new Point(40, 50));
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public static void main(String[] args)
    {
        Server server = new Server(67);
    }
}


