package org.example.Stage5.server;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.Stage3.server.ExitException;
import org.example.Stage4.server.BackRequest;
import org.example.Stage4.server.BackRequestSerializer;
import org.example.Stage5.client.Args;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    private static final int serverPort = 23456;
    private static final int POOL_SIZE = 40;
    private static final String fileName = "/Users/smnatsakanyan/Desktop/JSON Database/JSON Database/task/src/server/data/db.json";
    public static  void fromMapToJsonFile(String fileName, Map<String,String> container){
        String value = new Gson().toJson(container);
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))){
            bufferedWriter.write(value);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws ExitException {
        System.out.println("Server started!");
        Map<String, String> container = new ConcurrentHashMap<>(1000);
        AtomicReference<ServerSocket> server = null;
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(POOL_SIZE);
            executorService.submit(() -> {
                try {
                    server.set(new ServerSocket(serverPort));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            while (true) {

                Socket client = server.get().accept();

                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());


                ClientHandler clientSock
                        = new ClientHandler(client, container);

                ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
                executor.submit(clientSock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExitException e) {
            System.exit(0);
        }finally {
            if (server.get() != null) {
                try {
                    server.get().close();
                }
                catch (IOException e) {
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
                Args arguments = new Gson().fromJson(jsonData, Args.class);
                BackRequest backRequest = new BackRequest();
                switch (arguments.getType()) {
                    case "exit" -> {
                        backRequest.setResponse("OK");
                        String toJson = new Gson().toJson(backRequest);
                        out.writeUTF(toJson);
                        throw new ExitException("exit");
                    }
                    case "get" -> {
                        String index = arguments.getKey();
                        if (container.containsKey(index)) {
                            backRequest.setResponse("OK");
                            backRequest.setValue(container.get(index));
                        } else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    }
                    case "delete" -> {
                        String index = arguments.getKey();
                        if (container.containsKey(index)) {
                            container.remove(index);
                            backRequest.setResponse("OK");
                        } else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    }
                    case "set" -> {
                        String index = arguments.getKey();
                        container.put(index, arguments.getValue());
                        backRequest.setResponse("OK");
                    }
                }

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(BackRequest.class, new BackRequestSerializer())
                        .create();
                String toJson = gson.toJson(backRequest);
                out.writeUTF(toJson);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fromMapToJsonFile(fileName,container);
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