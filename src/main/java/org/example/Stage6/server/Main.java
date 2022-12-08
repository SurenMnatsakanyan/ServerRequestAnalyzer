package org.example.Stage6.server;
import org.example.Stage6.client.Args;
import com.google.gson.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private static final int serverPort = 8089;
    private static final int POOL_SIZE = 40;

    private static boolean isValidJson(String json) {
        try {
            new Gson().fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws ExitException {
        System.out.println("Server started!");
        Map<String, String> container = new HashMap<>(1000);
        ServerSocket server = null;
        try {
            server = new ServerSocket(serverPort);
            while (true) {
                ClientHandler clientSock
                        = new ClientHandler(server.accept(), container);
                ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
                executor.submit(
                        () -> {
                            try {
                                clientSock.run();
                            }catch (ExitException e){
                                System.exit(0);
                            }
                        }
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
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
                JsonObject jsonObjectForJsonData = new Gson().fromJson(jsonData,JsonObject.class);
                Args arguments = null;
                if(jsonObjectForJsonData.get("type").getAsString().equals("set")) {
                    if (jsonObjectForJsonData.get("key").isJsonArray()) {
                        if (jsonObjectForJsonData.get("value").isJsonObject())
                            arguments = new Args<ArrayList<String>, JsonObject>();
                        else
                            arguments = new Args<ArrayList<String>, String>();
                    } else {
                        if (jsonObjectForJsonData.get("value") != null) {
                            if (jsonObjectForJsonData.get("value").isJsonObject()) {
                                arguments = new Args<String, JsonObject>();
                            }
                        } else
                            arguments = new Args<String, String>();
                    }
                }else if(jsonObjectForJsonData.get("type").getAsString().equals("get")){
                    if (jsonObjectForJsonData.get("key").isJsonArray()) {
                            arguments = new Args<ArrayList<String>,Object>();
                    }
                    else {
                        arguments = new Args<String,Object>();
                    }
                }
                arguments = new Gson().fromJson(jsonData, Args.class);
                BackRequest backRequest = new BackRequest();
                switch (arguments.getType()) {
                    case "exit" -> {
                        backRequest.setResponse("OK");
                        String toJson = new Gson().toJson(backRequest);
                        out.writeUTF(toJson);
                        throw new ExitException("exit");
                    }
                    case "get" -> {
                        String[] keys = null;
                        if(arguments.getKey().getClass() != String.class) {
                            keys = ((ArrayList<String>) arguments.getKey()).toArray(new String[0]);
                        }
                        else{
                            keys = new String[1];
                            keys[0] =  arguments.getKey().toString();
                        }
                        String jsonObjectStringResult = container.get(keys[0]);
                        if (container.containsKey(keys[0])) {
                            backRequest.setResponse("OK");
                            if(keys.length == 1){
                                if(isValidJson(jsonObjectStringResult))
                                backRequest.setValue(jsonObjectStringResult);
                                else
                                    backRequest.setValue((new Gson().fromJson(jsonObjectStringResult, JsonObject.class)).getAsString());
                            }else{
                                JsonObject jsonObject =  new Gson().fromJson(jsonObjectStringResult, JsonObject.class);
                                for(int i=1; i<keys.length-1; i++){
                                    jsonObject = (JsonObject) jsonObject.get(keys[i]);
                                }
                                if (isValidJson(jsonObject.get(keys[keys.length-1]).toString()))
                               backRequest.setValue(jsonObject.get(keys[keys.length-1]).toString());
                                else
                                    backRequest.setValue(jsonObject.get(keys[keys.length-1]).getAsString());
                            }

                        } else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    }
                    case "delete" -> {
                        String[] keys = null;
                        if(arguments.getKey().getClass() != String.class) {
                            keys = ((ArrayList<String>) arguments.getKey()).toArray(new String[0]);
                        }
                        else{
                            keys = new String[1];
                            keys[0] =  arguments.getKey().toString();
                        }
                        String jsonObjectStringResult = container.get(keys[0]);
                        if (container.containsKey(keys[0])) {
                            if(keys.length == 1 )
                                container.remove(keys[0]);
                            else {
                                JsonObject jsonObject = new Gson().fromJson(jsonObjectStringResult, JsonObject.class);
                                JsonObject initialJson = jsonObject;
                                for (int i = 1; i < keys.length - 1; i++) {
                                    jsonObject = (JsonObject) jsonObject.get(keys[i]);
                                }
                                jsonObject.remove(keys[keys.length - 1]);
                                container.put(keys[0],initialJson.toString());
                            }
                            backRequest.setResponse("OK");
                        }
                        else {
                            backRequest.setResponse("ERROR");
                            backRequest.setValue("No such key");
                        }
                    }

                    case "set" -> {
                        String[] keys = null;
                        if(arguments.getKey().getClass() != String.class) {
                            keys = ((ArrayList<String>) arguments.getKey()).toArray(new String[0]);
                        }
                        else{
                            keys = new String[1];
                            keys[0] =  arguments.getKey().toString();
                        }
                        String  predefinedStringObject = container.get(keys[0]);
                        String valueToSet =  arguments.getValue().toString();
                        JsonObject jsonObjectForAssumedKey = null;
                        if(arguments.getValue().getClass().equals(String.class)){
                            if(predefinedStringObject != null) {
                                jsonObjectForAssumedKey = new Gson().fromJson(predefinedStringObject, JsonObject.class);
                                JsonObject partitionJsonObject = jsonObjectForAssumedKey;
                                for (int i = 1; i < keys.length - 1; i++) {
                                    partitionJsonObject = (JsonObject) partitionJsonObject.get(keys[i]);
                                }
                                partitionJsonObject.addProperty(keys[keys.length - 1], valueToSet);
                                container.put(keys[0], jsonObjectForAssumedKey.toString());
                            }else {
                                container.put(keys[0], jsonObjectForJsonData.get("value").toString());
                            }
                            backRequest.setResponse("OK");
                        } else if (!arguments.getValue().getClass().equals(String.class)){
                            JsonObject valueObject = (JsonObject) jsonObjectForJsonData.get("value");
                            if(predefinedStringObject!=null) {
                                jsonObjectForAssumedKey = new Gson().fromJson(predefinedStringObject, JsonObject.class);
                                JsonObject partitionJsonObject = jsonObjectForAssumedKey;
                                for (int i = 1; i < keys.length - 1; i++) {
                                    partitionJsonObject = (JsonObject) partitionJsonObject.get(keys[i]);
                                }
                                partitionJsonObject.add(keys[keys.length - 1],valueObject);
                                container.put(keys[0], jsonObjectForAssumedKey.toString());
                            } else {
                                jsonObjectForAssumedKey = new JsonObject();
                                JsonObject baseJsonObject = new JsonObject();
                                if(keys.length == 1)
                                    jsonObjectForAssumedKey = valueObject;
                                else
                                baseJsonObject.add(keys[keys.length-1],valueObject);
                                for(int i = keys.length -2; i>0; i--){
                                 jsonObjectForAssumedKey.add(keys[i],baseJsonObject);
                                 baseJsonObject = jsonObjectForAssumedKey;
                                }
                                container.put(keys[0], jsonObjectForAssumedKey.toString());
                            }
                            backRequest.setResponse("OK");
                        }

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
