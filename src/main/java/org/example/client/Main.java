package org.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private static  final  String SERVER_ADDRESS = "127.0.0.1";
    private static  final int SERVER_PORT = 23456;
    public static void main(String[] args) {
        try(Socket socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){
            System.out.println("Client Started");
            outputStream.writeUTF(" Give me a record # 12");
            System.out.println("Sent: Give me a record # 12");
            String receivedMessage = inputStream.readUTF();
            System.out.println("Received: " + receivedMessage);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
