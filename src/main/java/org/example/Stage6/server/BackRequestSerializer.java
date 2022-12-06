package org.example.Stage6.server;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BackRequestSerializer implements JsonSerializer<BackRequest> {
    private static boolean isValidJson(String json) {
        try {
            new Gson().fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
    }

    @Override
    public JsonElement serialize(BackRequest backRequest, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject backRequestObject = new JsonObject();
        if(backRequest.getResponse().equals("ERROR")){
            backRequestObject.addProperty("response","ERROR");
            backRequestObject.addProperty("reason","No such key");
        }
        else{
            if(backRequest.getValue() != null){
                backRequestObject.addProperty("response",backRequest.getResponse());
                if(isValidJson(backRequest.getValue())) {
                    JsonObject jsonObject = new Gson().fromJson(backRequest.getValue(),JsonObject.class);
                    backRequestObject.add("value", jsonObject);
                }else {
                    backRequestObject.addProperty("value",backRequest.getValue());
                }
            }
            else
                backRequestObject.addProperty("response",backRequest.getResponse());
            }
        return backRequestObject;
    }
}
