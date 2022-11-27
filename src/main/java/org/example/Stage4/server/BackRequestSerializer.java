package org.example.Stage4.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class BackRequestSerializer implements JsonSerializer<BackRequest> {

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
                backRequestObject.addProperty("value",backRequest.getValue());
            }
            else
                backRequestObject.addProperty("response",backRequest.getResponse());
        }
        return backRequestObject;
    }
}