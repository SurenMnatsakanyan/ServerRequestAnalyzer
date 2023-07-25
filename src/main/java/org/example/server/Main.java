package org.example.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.example.client.Args;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int serverPort = 9005;
    private static final int POOL_SIZE = 40;

    public static void main(String[] args) throws ExitException {
        System.out.println("Server started!");
        Map<String, String> container = new HashMap<>(1000);
        ServerSocket server = null;
        try {
            AtomicBoolean isRunning = new AtomicBoolean(true);
            server = new ServerSocket(serverPort);
            while (isRunning.get()) {
                ClientHandler clientSock
                        = new ClientHandler(server.accept(), container);
                ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
                executor.submit(
                        () -> {
                            try {
                                clientSock.run();
                            } catch (ExitException e) {
                                System.exit(0);
                            }
                        }
                );
                executor.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final Map<String, String> container;

        public ClientHandler(Socket clientSocket, Map<String, String> container) {
            this.clientSocket = clientSocket;
            this.container = container;
        }

        @Override
        public void run() throws ExitException {
            DataInputStream in = null;
            DataOutputStream out = null;
            try {
                in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                String jsonData = in.readUTF();
                JsonObject jsonObjectForJsonData = new Gson().fromJson(jsonData, JsonObject.class);
                Args arguments = new Gson().fromJson(jsonData, Args.class);
                Command command = null;
                switch (arguments.getType()) {
                    case "exit" -> {
                        BackResponse backResponse = new BackResponse();
                        backResponse.setResponse("OK");
                        String toJson = new Gson().toJson(backResponse);
                        out.writeUTF(toJson);
                        throw new ExitException("exit");
                    }
                    case "get" -> {
                        command = new GetCommand(arguments, container);
                    }

                    case "set" -> {
                        command = new SetCommand(arguments, container, jsonObjectForJsonData);
                    }
                    case "delete" -> {
                        command = new DeleteCommand(arguments, container);
                    }
                    default -> throw new ExitException("Exit");
                }
                BackResponse backResponse = command.execute();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(BackResponse.class, new BackRequestSerializer())
                        .create();
                String toJson = gson.toJson(backResponse);
                out.writeUTF(toJson);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null)
                        out.close();
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}