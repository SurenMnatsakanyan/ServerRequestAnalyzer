package org.example.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static final int serverPort = 23456;
    public static void main(String[] args) {
        System.out.println("Server started!");
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            Socket client = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(client);
            new Thread(clientHandler).start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable{
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket){
            this.clientSocket = clientSocket;
        }
        @Override
        public void run() {
            DataInputStream in  = null;
            DataOutputStream out = null;
            try {
                in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

                String line = in.readUTF();
                System.out.println("Received: " + line);
                String[] arr = line.split(" ");
                int number  = Integer.parseInt(arr[arr.length -1]);
                out.writeUTF("A record # " + number +  " was sent!");
                System.out.println("Sent: A record # " + number +  " was sent!");
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try{
                    if (out!=null)
                        out.close();
                    if(in!=null) {
                        in.close();
                        clientSocket.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
