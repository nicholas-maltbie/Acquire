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

import com.flyingblock.acquire.model.Investor;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nick_Pro
 */
public class ChatLobbyServer extends javax.swing.JFrame implements ServerListener {

    private Map<Socket, String> names;
    private List<Socket> pendingConnections;
    private Server server;
    private int maxPlayers;
    /**
     * Creates new form ChatLobbyServer
     */
    public ChatLobbyServer() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        portInputField = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        chatInputField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        maxPlayersField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        usersTextArea = new javax.swing.JTextArea();
        gameStart = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Port");

        portInputField.setText("Port");
        portInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                portInputFieldActionPerformed(evt);
            }
        });

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        chatInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chatInputFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Input field");

        jLabel3.setText("Chat");

        jLabel4.setText("Online Users");

        jLabel5.setText("Server Options");

        jLabel6.setText("Max Players");

        maxPlayersField.setText("Players");
        maxPlayersField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxPlayersFieldActionPerformed(evt);
            }
        });

        chatTextArea.setEditable(false);
        chatTextArea.setColumns(20);
        chatTextArea.setLineWrap(true);
        chatTextArea.setRows(5);
        jScrollPane1.setViewportView(chatTextArea);

        usersTextArea.setEditable(false);
        usersTextArea.setColumns(20);
        usersTextArea.setRows(5);
        jScrollPane2.setViewportView(usersTextArea);

        gameStart.setText("StartGame");
        gameStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(startButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stopButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(portInputField, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxPlayersField, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(gameStart))
                .addGap(164, 164, 164)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(22, 22, 22))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(29, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(chatInputField, javax.swing.GroupLayout.PREFERRED_SIZE, 578, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(portInputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(maxPlayersField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(stopButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chatInputField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gameStart))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void portInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portInputFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_portInputFieldActionPerformed

    private void maxPlayersFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxPlayersFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxPlayersFieldActionPerformed

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        // TODO add your handling code here:
        names = new HashMap<>();
        pendingConnections = new ArrayList<>();
        if(server == null || !server.isRunning())
        {
            try{
                maxPlayers = Integer.parseInt(maxPlayersField.getText());
            }
            catch (NumberFormatException ex)
            {
                displayMessage("Invalid max players, it mus tbe a number");
                return;
            }
            try {
                int port = Integer.parseInt(portInputField.getText());
                server = new Server(port);
                server.addListener(this);
                new Thread(){
                    @Override
                    public void run()
                    {
                        updateOnlineUsers();
                        try {
                            server.start();
                        } catch (IOException ex) {
                            Logger.getLogger(ChatLobbyServer.class.getName()).log(Level.SEVERE, null, ex);
                            displayMessage(ex.toString());
                        }
                    }
                }.start();
                displayMessage("Server started");
            }
            catch (NumberFormatException ex)
            {
                displayMessage("Your port number is not valid");
            }
        }
        if(server != null && server.isRunning())
        {
            displayMessage("The server is already running");
        }
    }//GEN-LAST:event_startButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        // TODO add your handling code here:
        if(server != null && server.isRunning())
        {
            try {
                server.stop();
                updateOnlineUsers();
            } catch (IOException ex) {
                Logger.getLogger(ChatLobbyServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            displayMessage("Server has been stopped");
        }
        else
        {
            displayMessage("Server is not running");
        }
    }//GEN-LAST:event_stopButtonActionPerformed

    private void chatInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chatInputFieldActionPerformed
        // TODO add your handling code here:
        sendChatEvent("SERVER", chatInputField.getText());
        chatInputField.setText("");
    }//GEN-LAST:event_chatInputFieldActionPerformed

    private void gameStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameStartActionPerformed
        // TODO add your handling code here:
        //start game
        
        //kick all pending connections
        for(Socket pending : pendingConnections) {
            server.getClient(pending).disconnect();
        }
        //Construct the investors
        List<String> names = new ArrayList<>();
        List<Investor> players = new ArrayList<>();
        for(int i = 0; i < maxPlayers; i++)
        {
            
        }
    }//GEN-LAST:event_gameStartActionPerformed

    public void displayMessage(String message)
    {
        if(!chatTextArea.getText().isEmpty())
            chatTextArea.append("\n" + message);
        else
            chatTextArea.append(message);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatLobbyServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatLobbyServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatLobbyServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatLobbyServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatLobbyServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField chatInputField;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JButton gameStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField maxPlayersField;
    private javax.swing.JTextField portInputField;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JTextArea usersTextArea;
    // End of variables declaration//GEN-END:variables


    public void updateOnlineUsers()
    {
        String str = "";
        for(String name : names.values())
        {
            str += name + "\n";
        }
        for(Socket pending : pendingConnections)
        {
            str += pending.getInetAddress() + " pending...\n";
        }
        usersTextArea.setText(str);
    }
    
    @Override
    public void objectRecieved(Socket client, Object message) 
    {
        if(!names.containsKey(client) && message instanceof String)
        {
            String name = ((String) message).toUpperCase().trim();
            if(names.containsValue(name))
            {
                server.getClient(client).sendData("That name is already taken, choose a new one");
            }
            else
            {
                names.put(client, name);
                sendChatEvent(names.get(client), "Has joined the server");
                pendingConnections.remove(client);
                updateOnlineUsers();
                if(names.size() >= maxPlayers)
                {
                    for(Socket connection : pendingConnections)
                    {
                        ClientThread thread = server.getClient(connection);
                        thread.sendData("Server is full, sorry");
                        thread.disconnect();
                    }
                }
            }
        }
        else if(names.containsKey(client) && message instanceof String)
        {
            String line = (String) message;
            if(line.startsWith("/"))
            {
                //do command
            }
            else
            {
                sendChatEvent(names.get(client), line);
            }
        }
    }
    
    public void sendChatEvent(String sender, String message)
    {
        Set<ClientThread> clients = server.getClients();
        String thingy = "[" + sender + "] " + message;
        for(ClientThread c : clients)
        {
            c.sendData(thingy);
        }
        displayMessage(thingy);
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        ClientThread thread = server.getClient(client);
        if(names.size() >= maxPlayers)
        {
            thread.sendData("The game is full, sorry");
            thread.disconnect();
        }
        else
        {
            //System.out.println(client + " joined the network");
            pendingConnections.add(client);
            thread.sendData("Please tell us your name");
            updateOnlineUsers();
        }
    }

    @Override
    public void disconnectedFromNetwork(Socket client) 
    {
        if(names.containsKey(client))
            sendChatEvent("SERVER", names.get(client) + " has left the server");
        names.remove(client);
        pendingConnections.remove(client);
        updateOnlineUsers();
    }

    @Override
    public void connectionRejected(Socket client) 
    {
        
    }

}
