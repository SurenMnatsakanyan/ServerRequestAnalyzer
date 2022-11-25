package org.example.Stage3.client;

import com.beust.jcommander.JCommander;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private static  final  String SERVER_ADDRESS = "127.0.0.1";
    private static  final int SERVER_PORT = 23456;
    public static void main(String[] args) {
        System.out.println("Client Started!");
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);
        try(Socket socket = new Socket(SERVER_ADDRESS,SERVER_PORT);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){
            if(!arguments.getTypeOfParam().equals("exit") && arguments.getCellValue() == null){
                outputStream.writeUTF(arguments.getTypeOfParam());
                outputStream.writeInt(arguments.getIndexCell());
                System.out.println("Sent: " + arguments.getTypeOfParam() + " " + arguments.getIndexCell());
            }else if(arguments.getCellValue() != null){
                outputStream.writeUTF(arguments.getTypeOfParam());
                outputStream.writeInt(arguments.getIndexCell());
                outputStream.writeUTF(arguments.getCellValue());
                System.out.println("Sent: " + arguments.getTypeOfParam() + " " + arguments.getIndexCell() + " "  + arguments.getCellValue());
            }else {
                outputStream.writeUTF(arguments.getTypeOfParam());
                System.out.println("Sent: " + arguments.getTypeOfParam());
            }
            String answer = inputStream.readUTF();
            System.out.println("Received: " + answer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
