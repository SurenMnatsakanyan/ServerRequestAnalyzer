package org.example.Stage7.server;
import org.example.Stage7.client.Args;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

public class DeleteCommand implements Command{
    private Args<?,?> arguments;
    private Map<String,String> container;
    private BackRequest backRequest;

    public DeleteCommand(Args<?, ?> arguments, Map<String, String> container, BackRequest backRequest) {
        this.arguments = arguments;
        this.container = container;
        this.backRequest = backRequest;
    }

    @Override
    public void execute() {
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
}