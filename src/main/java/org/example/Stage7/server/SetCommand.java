package org.example.Stage7.server;
import org.example.Stage7.client.Args;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Map;

public class SetCommand implements Command{
    private Args<?,?> arguments;
    private Map<String,String> container;
   private JsonObject jsonObjectForJsonData;

    public SetCommand(Args<?, ?> arguments, Map<String, String> container, JsonObject jsonObjectForJsonData) {
        this.arguments = arguments;
        this.container = container;
        this.jsonObjectForJsonData = jsonObjectForJsonData;
    }

    @Override
    public BackResponse execute() {
        String[] keys = null;
        BackResponse backResponse = new BackResponse();
        if(arguments.getKey().getClass() != String.class) {
            keys = ((ArrayList<String>) arguments.getKey()).toArray(new String[0]);
            System.out.println(keys[0]);
        }
        else{
            keys = new String[1];
            keys[0] =  arguments.getKey().toString();
        }
        String  firstValue = container.get(keys[0]);
        String valueToSet =  arguments.getValue().toString();
        JsonObject jsonObjectForAssumedKey = null;
        if(arguments.getValue().getClass().equals(String.class)){
            if(firstValue != null && keys.length!=1 ) {
                jsonObjectForAssumedKey = new Gson().fromJson(firstValue, JsonObject.class);
                JsonObject partitionJsonObject = jsonObjectForAssumedKey;
                for (int i = 1; i < keys.length - 1; i++) {
                    partitionJsonObject = (JsonObject) partitionJsonObject.get(keys[i]);
                }
                partitionJsonObject.addProperty(keys[keys.length - 1], valueToSet);
                container.put(keys[0], jsonObjectForAssumedKey.toString());
            }else {
                container.put(keys[0], jsonObjectForJsonData.get("value").getAsString());
            }
            backResponse.setResponse("OK");
        } else if (!arguments.getValue().getClass().equals(String.class)){
            JsonObject valueObject = (JsonObject) jsonObjectForJsonData.get("value");
            if(firstValue!=null && keys.length != 1) {
                jsonObjectForAssumedKey = new Gson().fromJson(firstValue, JsonObject.class);
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
                else {
                    baseJsonObject.add(keys[keys.length - 1], valueObject);
                    for (int i = keys.length - 2; i > 0; i--) {
                        jsonObjectForAssumedKey.add(keys[i], baseJsonObject);
                        baseJsonObject = jsonObjectForAssumedKey;
                    }
                }
                container.put(keys[0], jsonObjectForAssumedKey.toString());
            }
            backResponse.setResponse("OK");
        }
        else {
            backResponse.setResponse("Error");
        }
    return backResponse;
    }

    public Map<String, String> getContainer() {
        return container;
    }
}
