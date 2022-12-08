package org.example.Stage7.server;
import org.example.Stage7.client.Args;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Map;

public class GetCommand implements Command{
    private Args<?,?> arguments;
    private Map<String,String> container;
    private BackRequest backRequest;

    public GetCommand(Args arguments, Map<String, String> container, BackRequest backRequest) {
        this.arguments = arguments;
        this.container = container;
        this.backRequest = backRequest;
    }

    private static boolean isValidJson(String json) {
        try {
            new Gson().fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
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
}
