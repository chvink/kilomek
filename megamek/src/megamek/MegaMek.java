/**
 * MegaMek - Copyright (C) 2000-2002 Ben Mazur (bmazur@sev.org)
 * 
 *  This program is free software; you can redistribute it and/or modify it 
 *  under the terms of the GNU General Public License as published by the Free 
 *  Software Foundation; either version 2 of the License, or (at your option) 
 *  any later version.
 * 
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 *  for more details.
 */

package megamek;

import java.awt.*;
import java.awt.event.*;

import megamek.common.*;
import megamek.client.*;
import megamek.server.*;

public class MegaMek
    implements WindowListener, ActionListener
{
    public Frame            frame;
    
    public Client client;
    public Server server;
    
    /**
     * Contruct a MegaMek, and display the main menu in the
     * specified frame.
     */
    public MegaMek(Frame frame) {
        this.frame = frame;
        frame.addWindowListener(this);
    
        Settings.load();
        if(Settings.windowSizeHeight != 0) {
            frame.setLocation(Settings.windowPosX, Settings.windowPosY);
            frame.setSize(Settings.windowSizeWidth, Settings.windowSizeHeight);
        } else {
            frame.setSize(800, 600);
        }
        
        frame.setBackground(SystemColor.menu);
        frame.setForeground(SystemColor.menuText);
        
        showMainMenu();
        
        frame.setVisible(true);
    }
    
    /**
     * Display the main menu.
     */
    public void showMainMenu() {
        Button hostB, connectB, editB, dedicatedB;
            
        frame.removeAll();
        
        hostB = new Button("Host a New Game...");
        hostB.setActionCommand("game_host");
        hostB.addActionListener(this);
        
        connectB = new Button("Connect to a Game...");
        connectB.setActionCommand("game_connect");
        connectB.addActionListener(this);
        
        dedicatedB = new Button("Dedicated Server...");
        dedicatedB.setActionCommand("server_dedicated");
        dedicatedB.addActionListener(this);
        
        editB = new Button("Editor");
        editB.setActionCommand("editor");
        editB.addActionListener(this);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(gridbag);
        
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;    c.weighty = 0.0;
        c.insets = new Insets(4, 4, 1, 1);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.ipadx = 20;    c.ipady = 5;

        addBag(hostB, gridbag, c);
        addBag(connectB, gridbag, c);
        addBag(dedicatedB, gridbag, c);
        addBag(editB, gridbag, c);

        frame.validate();
    }
    
    /**
     * Display the board editor.
     */
    public void showEditor() {
        Game            game = new Game();
        BoardView        bv;
        BoardEditor        be;
        
        frame.removeAll();
        
        bv = new BoardView(game);
        
        be = new BoardEditor(frame, game.board);
        game.board.addBoardListener(be);
        be.setSize(120, 120);
        
        be.addKeyListener(bv);
        bv.addKeyListener(be);
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(gridbag);
        
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1.0;    c.weighty = 1.0;
        
        c.fill = GridBagConstraints.BOTH;
        addBag(bv, gridbag, c);
        c.fill = GridBagConstraints.VERTICAL;
        c.weightx = 0.0;
        c.gridwidth = GridBagConstraints.REMAINDER; 
        addBag(be, gridbag, c);
        
        frame.validate();
    }
    
    /**
     * 
     */
    public void showGame() {
        frame.removeAll();
        
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(gridbag);
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;    c.weighty = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        c.gridwidth = GridBagConstraints.REMAINDER;

        addBag(client, gridbag, c);

        frame.validate();
    }
    
    /**
     * Host a new game, connect to it, and then launch the 
     * chat lounge.
     */
    public void host() {
        HostDialog hd;
        
        hd = new HostDialog(frame);
        hd.show();
        // verify dialog data
        if(hd.name == null || hd.serverName == null || hd.port == 0) {
            return;
        }
        // start server
        server = new Server(hd.serverName, hd.port);
        // initialize game
        client = new Client(frame, hd.name);
        // verify connection
        if(!client.connect("localhost", hd.port)) {
            server = null;
            client = null;
            new AlertDialog(frame, "Host a Game", "Error: could not connect to local server.").show();
            return;
        }
        // wait for full connection
        client.retrieveServerInfo();
        
        //showChatLounge();
        showGame();
    }
    
    /**
     * Connect to to a game and then launch the chat lounge.
     */
    public void connect() {
        ConnectDialog cd;
        
        cd = new ConnectDialog(frame);
        cd.show();
        // verify dialog data
        if(cd.name == null || cd.serverAddr == null || cd.port == 0) {
            return;
        }
        // initialize game
        client = new Client(frame, cd.name);
        // verify connection
        if(!client.connect(cd.serverAddr, cd.port)) {
            server = null;
            client = null;
            new AlertDialog(frame, "Connect to a Game", "Error: could not connect.").show();
            return;
        }
        // wait for full connection
        client.retrieveServerInfo();
        
        showGame();
    }
  
  /**
   * Starts the server, but no client
   */
  public void dedicatedServer() {
        HostDialog hd;
        
        hd = new HostDialog(frame);
        hd.show();
        // verify dialog data
        if(hd.name == null || hd.serverName == null || hd.port == 0) {
            return;
        }
        // start server
        server = new Server(hd.serverName, hd.port);
    
    // die, frame!
    frame.setVisible(false);
  }
    
    private void addBag(Component comp, GridBagLayout gridbag, GridBagConstraints c) {
        gridbag.setConstraints(comp, c);
        frame.add(comp);
    }
    
    public static void main(String[] args) {
        Frame frame = new Frame("MegaMek");
        MegaMek mm = new MegaMek(frame);
    }
    
    //
    // ActionListener
    //
    public void actionPerformed(ActionEvent ev) {
        if(ev.getActionCommand().equalsIgnoreCase("main_menu")) {
            showMainMenu();
        }
        if(ev.getActionCommand().equalsIgnoreCase("editor")) {
            showEditor();
        }
        if(ev.getActionCommand().equalsIgnoreCase("game_host")) {
            host();
        }
        if(ev.getActionCommand().equalsIgnoreCase("game_connect")) {
            connect();
        }
        if(ev.getActionCommand().equalsIgnoreCase("server_dedicated")) {
            dedicatedServer();
        }
    }
    
    //
    // WindowListener
    //    
    public void windowClosing(WindowEvent ev) {
        // feed last window position to settings
        Settings.windowPosX = frame.getLocation().x;
        Settings.windowPosY = frame.getLocation().y;
        Settings.windowSizeWidth = frame.getSize().width;
        Settings.windowSizeHeight = frame.getSize().height;
        
        // save settings
        Settings.save();
        
        // okay, exit program
        System.exit(0);
    }
    public void windowOpened(WindowEvent ev) {
    }
    public void windowClosed(WindowEvent ev) {
    }
    public void windowDeiconified(WindowEvent ev) {
    }
    public void windowActivated(WindowEvent ev) {
    }
    public void windowIconified(WindowEvent ev) {
    }
    public void windowDeactivated(WindowEvent ev) {
    }
}

/**
 * here's a quick class for the host new game diaglogue box
 */
class HostDialog extends Dialog implements ActionListener {
    public String            name, serverName;
    public int                port;
    
    protected Label            yourNameL, serverNameL, portL;
    protected TextField        yourNameF, serverNameF, portF;
    protected Button        okayB, cancelB;
    
    public HostDialog(Frame frame) {
        super(frame, "Host New Game...", true);
        
        yourNameL = new Label("Your Name:", Label.RIGHT);
        serverNameL = new Label("Server Name:", Label.RIGHT);
        portL = new Label("Port:", Label.RIGHT);
        
        yourNameF = new TextField(Settings.lastPlayerName, 16);
        serverNameF = new TextField(Settings.lastServerName, 16);
        portF = new TextField(Settings.lastServerPort + "", 4);
    
        okayB = new Button("Okay");
        okayB.setActionCommand("done");
        okayB.addActionListener(this);
        okayB.setSize(80, 24);

        cancelB = new Button("Cancel");
        cancelB.setActionCommand("cancel");
        cancelB.addActionListener(this);
        cancelB.setSize(80, 24);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;    c.weighty = 0.0;
        c.insets = new Insets(5, 5, 5, 5);
        
        c.gridwidth = 1;
        gridbag.setConstraints(yourNameL, c);
        add(yourNameL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(yourNameF, c);
        add(yourNameF);
        
        c.gridwidth = 1;
        gridbag.setConstraints(serverNameL, c);
        add(serverNameL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(serverNameF, c);
        add(serverNameF);
        
        c.gridwidth = 1;
        gridbag.setConstraints(portL, c);
        add(portL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(portF, c);
        add(portF);
        
        
        c.ipadx = 20;    c.ipady = 5;
        c.gridwidth = 1;
        gridbag.setConstraints(okayB, c);
        add(okayB);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(cancelB, c);
        add(cancelB);
        
        pack();
        setLocation(frame.getLocation().x + frame.getSize().width/2 - getSize().width/2,
                    frame.getLocation().y + frame.getSize().height/2 - getSize().height/2);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("done")) {
            try {
                name = yourNameF.getText();
                serverName = serverNameF.getText();
                port = Integer.decode(portF.getText()).intValue();
            } catch(NumberFormatException ex) {
                System.err.println(ex.getMessage());
            }
            
            // update settings
            Settings.lastPlayerName = name;
            Settings.lastServerName = serverName;
            Settings.lastServerPort = port;

            setVisible(false);
        }
        if(e.getActionCommand().equals("cancel")) {
            setVisible(false);
        }
    }
}

/**
 * here's a quick class for the connect to game diaglogue box
 */
class ConnectDialog extends Dialog implements ActionListener {
    public String            name, serverAddr;
    public int                port;
    
    protected Label            yourNameL, serverAddrL, portL;
    protected TextField        yourNameF, serverAddrF, portF;
    protected Button        okayB, cancelB;
    
    public ConnectDialog(Frame frame) {
        super(frame, "Connect To Game...", true);
        
        yourNameL = new Label("Your Name:", Label.RIGHT);
        serverAddrL = new Label("Server Address:", Label.RIGHT);
        portL = new Label("Port:", Label.RIGHT);
        
        yourNameF = new TextField(Settings.lastPlayerName, 16);
        serverAddrF = new TextField(Settings.lastConnectAddr, 16);
        portF = new TextField(Settings.lastConnectPort + "", 4);
    
        okayB = new Button("Okay");
        okayB.setActionCommand("done");
        okayB.addActionListener(this);
        okayB.setSize(80, 24);

        cancelB = new Button("Cancel");
        cancelB.setActionCommand("cancel");
        cancelB.addActionListener(this);
        cancelB.setSize(80, 24);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.NONE;
        c.weightx = 0.0;    c.weighty = 0.0;
        c.insets = new Insets(5, 5, 5, 5);
        
        c.gridwidth = 1;
        gridbag.setConstraints(yourNameL, c);
        add(yourNameL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(yourNameF, c);
        add(yourNameF);
        
        c.gridwidth = 1;
        gridbag.setConstraints(serverAddrL, c);
        add(serverAddrL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(serverAddrF, c);
        add(serverAddrF);
        
        c.gridwidth = 1;
        gridbag.setConstraints(portL, c);
        add(portL);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(portF, c);
        add(portF);
        
        
        c.ipadx = 20;    c.ipady = 5;
        c.gridwidth = 1;
        gridbag.setConstraints(okayB, c);
        add(okayB);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(cancelB, c);
        add(cancelB);
        
        pack();
        setLocation(frame.getLocation().x + frame.getSize().width/2 - getSize().width/2,
                    frame.getLocation().y + frame.getSize().height/2 - getSize().height/2);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("done")) {
            try {
                name = yourNameF.getText();
                serverAddr = serverAddrF.getText();
                port = Integer.decode(portF.getText()).intValue();
                
            } catch(NumberFormatException ex) {
                System.err.println(ex.getMessage());
            }
            
            // update settings
            Settings.lastPlayerName = name;
            Settings.lastConnectAddr = serverAddr;
            Settings.lastConnectPort = port;
                
            setVisible(false);
        }
        if(e.getActionCommand().equals("cancel")) {
            setVisible(false);
        }
    }
}