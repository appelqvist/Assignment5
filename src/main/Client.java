package main;

import java.io.*;
import java.net.Socket;

/**
 * Created by Andreas Appelqvist on 2016-01-11.
 */
public class Client {

    private GUIClient gui;
    private Socket socket;
    private String ip;
    private int port;
    private Listener listener;
    private boolean connected = false;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private boolean listenerRunning = false;

    public Client(String ip, int port) {
        gui = new GUIClient(this);
        gui.Start();
        this.ip = ip;
        this.port = port;

        if (connect()) {
            listenerRunning = true;
            listener.start();
        }
    }

    public void sendMessage(String str) {
        if (connected) {
            try {
                oos.writeObject(str);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gui.writeRow(str);
        } else {
            gui.writeRow("You're not connected.. ");
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(ip, port);
            oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            oos.flush();
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            listener = new Listener();
            connected = true;
            gui.writeRow("Connected to server: "+ip+":"+port);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void disconnect() {
        try {
            socket.close();
            listenerRunning = false;
            connected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Listener extends Thread {
        @Override
        public void run() {
            super.run();
            String str = "";
            while (listenerRunning) {
                try {
                    str = (String) ois.readObject();
                } catch (Exception e) {
                    disconnect();
                    gui.writeRow("Lost connection");
                    break;
                }
                gui.writeRow(str);

                try {
                    sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
