package org.example.Stage4.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Stage3.server.ExitException;
import org.example.Stage4.client.Args;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final int serverPort = 23456;

    public static void main(String[] args) throws ExitException {
        System.out.println("Server started!");
        Map<String, String> container = new HashMap<>(1000);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream in = new DataInputStream(socket.getInputStream());
                     DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
                    String jsonData = in.readUTF();
                    Args arguments = new Gson().fromJson(jsonData, Args.class);
                    BackRequest backRequest = new BackRequest();
                    if (arguments.getType().equals("exit")) {
                        backRequest.setResponse("OK");
                        String toJson = new Gson().toJson(backRequest);
                        out.writeUTF(toJson);
                        throw new ExitException("exit");
                    } else if (arguments.getType().equals("get")) {
                        String index = arguments.getKey();
                        if (container.containsKey(index)) {
                            backRequest.setResponse("OK");
                            backRequest.setValue(container.get(index));
                        } else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    } else if (arguments.getType().equals("delete")) {
                        String index = arguments.getKey();
                        if (container.containsKey(index)) {
                            container.remove(index);
                            backRequest.setResponse("OK");
                        } else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    } else if (arguments.getType().equals("set")) {
                        String index = arguments.getKey();
                        container.put(index, arguments.getValue());
                        backRequest.setResponse("OK");
                    }

                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(BackRequest.class, new BackRequestSerializer())
                            .create();
                    String toJson = gson.toJson(backRequest);
                    out.writeUTF(toJson);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExitException e) {
            System.exit(0);
        }
    }
}