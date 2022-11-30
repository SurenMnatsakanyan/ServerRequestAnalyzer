package client;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import org.example.Stage5.client.Args;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 23456;

    public static void main(String[] args) throws IOException {
        System.out.println("Client started!");
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        String encapsulatedValue = null;
        String dataFilePath = "/Users/smnatsakanyan/Desktop/JSON Database/JSON Database/task/src/client/data/";
        if (arguments.getTextFileName() == null)
            encapsulatedValue = new Gson().toJson(arguments);
        else {
            encapsulatedValue = arguments.getTextFileName();
            try {
                encapsulatedValue = new String(Files.readAllBytes(Paths.get(dataFilePath + encapsulatedValue)));
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        System.out.println("Sent: " + encapsulatedValue);
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){
            outputStream.writeUTF(encapsulatedValue);
            String answer = inputStream.readUTF();
            System.out.println("Received: " + answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}