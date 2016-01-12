package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Andreas Appelqvist on 2016-01-11.
 */
public class Server {

    private ThreadPoolExecutor executor;
    private ServerSocket connectionSocket;
    private ClientConnector cc;
    private ArrayList<ClientHandler> chs;

    private GUIServer gui;

    public Server(int nbrOfThreads, int port) {

        gui = new GUIServer(this);
        gui.Start();

        chs = new ArrayList<>();

        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nbrOfThreads);
        try {
            connectionSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cc = new ClientConnector();
        cc.start();
    }

    public void show(){
        System.out.println(executor.getActiveCount()+" .. "+chs.size());
    }

    public void sendMessageToClients(String str) {
        str = "Server: "+str;
        for (int i = 0; i < chs.size(); i++) {
            chs.get(i).sendMessage(str);
        }
        gui.writeRow(str+"\n");
    }


    public void handleClient(Runnable r) {
        executor.execute(r);
    }

    private class ClientHandler implements Runnable {
        private Socket client;
        private ObjectInputStream clientInput;
        private ObjectOutputStream clientOut;

        public ClientHandler(Socket socket) {
            client = socket;
            try {

                clientOut = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
                clientOut.flush();
                clientInput = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String str){
            try {
                clientOut.writeObject(str);
                clientOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) { //Komma på något så att den inte går i all evighet
                String str = "";
                try {
                    str = (String)clientInput.readObject();
                } catch (Exception e){
                    chs.remove(chs.indexOf(this));
                    gui.writeRow("A user lost connection\n");
                    break;
                }
                gui.writeRow(str+"\n");
            }
        }
    }

    private class ClientConnector extends Thread {
        @Override
        public void run() {
            super.run();
            gui.writeRow("Waiting for connection.\n");
            while (true) {
                try {

                    Socket client = connectionSocket.accept();
                    gui.writeRow("New user connected.\n");
                    ClientHandler ch = new ClientHandler(client);
                    handleClient(ch);
                    chs.add(ch);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
