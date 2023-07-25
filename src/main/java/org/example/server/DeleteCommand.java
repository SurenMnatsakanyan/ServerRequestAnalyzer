package org.example.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.client.Args;

import java.util.ArrayList;
import java.util.Map;

public class DeleteCommand implements Command {
    private final Args<?, ?> arguments;
    private final Map<String, String> container;

    public DeleteCommand(Args<?, ?> arguments, Map<String, String> container) {
        this.arguments = arguments;
        this.container = container;
    }

    @Override
    public BackResponse execute() {
        String[] keys = null;
        BackResponse backResponse = new BackResponse();
        if (arguments.getKey().getClass() != String.class) {
            keys = ((ArrayList<String>) arguments.getKey()).toArray(new String[0]);
        } else {
            keys = new String[1];
            keys[0] = arguments.getKey().toString();
        }
        String jsonObjectStringResult = container.get(keys[0]);
        if (container.containsKey(keys[0])) {
            if (keys.length == 1)
                container.remove(keys[0]);
            else {
                JsonObject jsonObject = new Gson().fromJson(jsonObjectStringResult, JsonObject.class);
                JsonObject initialJson = jsonObject;
                for (int i = 1; i < keys.length - 1; i++) {
                    jsonObject = (JsonObject) jsonObject.get(keys[i]);
                }
                jsonObject.remove(keys[keys.length - 1]);
                container.put(keys[0], initialJson.toString());
            }
            backResponse.setResponse("OK");
        } else {
            backResponse.setResponse("ERROR");
            backResponse.setValue("No such key");
        }
        return backResponse;
    }
}
