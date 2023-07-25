package org.example.server;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BackRequestSerializer implements JsonSerializer<BackResponse> {
    private static boolean isValidJson(String json) {
        try {
            new Gson().fromJson(json, JsonObject.class);
        } catch (JsonSyntaxException e) {
            return false;
        }
        return true;
    }

    @Override
    public JsonElement serialize(BackResponse backResponse, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject backRequestObject = new JsonObject();
        if (backResponse.getResponse().equals("ERROR")) {
            backRequestObject.addProperty("response", "ERROR");
            backRequestObject.addProperty("reason", "No such key");
        } else {
            if (backResponse.getValue() != null) {
                backRequestObject.addProperty("response", backResponse.getResponse());
                if (isValidJson(backResponse.getValue())) {
                    JsonObject jsonObject = new Gson().fromJson(backResponse.getValue(), JsonObject.class);
                    backRequestObject.add("value", jsonObject);
                } else {
                    backRequestObject.addProperty("value", backResponse.getValue());
                }
            } else
                backRequestObject.addProperty("response", backResponse.getResponse());
        }
        return backRequestObject;
    }
}
