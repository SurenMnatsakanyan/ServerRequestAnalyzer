package org.example.client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 9005;

    public static void main(String[] args) {
        System.out.println("Client started!");
        Args arguments = new Args();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        String encapsulatedValue = null;
        String dataFilePath = "/Users/smnatsakanyan/Desktop/hard_project/src/main/java/org/example/Stage7/client/data/";
        if (arguments.getTextFileName() == null) {
            if (arguments.getValue() != null && arguments.getValue().getClass().equals(ArrayList.class)) {
                ArrayList<String> arr = (ArrayList<String>) arguments.getValue();
                arguments.setValue(arr.get(0));
            }
            encapsulatedValue = new Gson().toJson(arguments);
        } else {
            encapsulatedValue = arguments.getTextFileName();
            try {
                encapsulatedValue = new String(Files.readAllBytes(Paths.get(dataFilePath + encapsulatedValue)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Sent: " + encapsulatedValue);
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            outputStream.writeUTF(encapsulatedValue);
            String answer = inputStream.readUTF();
            System.out.println("Received: " + answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}