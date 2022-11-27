package org.example.Stage4.client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private static  final  String SERVER_ADDRESS = "127.0.0.1";
    private static  final int SERVER_PORT = 23456;
    public static void main(String[] args) {
        System.out.println("Client started!");
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        String encapsulatedValue = new Gson().toJson(arguments);
        System.out.println("Sent: " + encapsulatedValue);
        try(Socket socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){

            outputStream.writeUTF(encapsulatedValue);
            String answer = inputStream.readUTF();
            System.out.println("Received: " + answer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}