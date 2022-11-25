package org.example.Stage3.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {
    private static final int serverPort = 23456;
    public static void main(String[] args) throws ExitException{

        System.out.println("Server started!");
        String[] container = new String[1000];
        Arrays.fill(container, "");
        try ( ServerSocket serverSocket = new ServerSocket(serverPort))
        {
            while(true){
                try(Socket socket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream())){
                    String typeOfParam = in.readUTF();
                    if(typeOfParam.equals("exit")){
                        out.writeUTF("OK");
                        throw new ExitException("exit");
                    } else if(typeOfParam.equals("get")){
                        int index = in.readInt();
                        if(index -1> container.length -1 || index -1 < 0  || container[index-1].equals("")) {
                            out.writeUTF("Error");
                        }
                        else{
                            out.writeUTF(container[index-1]);
                        }
                    }else if(typeOfParam.equals("delete")){
                        int index = in.readInt();
                        if(index - 1> container.length -1 || index -1 < 0 ){
                            out.writeUTF("Error");
                        }
                        else {
                            out.writeUTF("OK");
                            container[index-1] = "";
                        }
                    }else if(typeOfParam.equals("set")){
                        int index = in.readInt();
                        if(index -1> container.length -1 || index -1 < 0 ){
                            out.writeUTF("Error");
                        }
                        else{
                            String valueOfData = in.readUTF();
                            container[index-1] = valueOfData;
                            out.writeUTF("OK");
                        }
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (ExitException e){
            System.exit(0);
        }
    }
}
