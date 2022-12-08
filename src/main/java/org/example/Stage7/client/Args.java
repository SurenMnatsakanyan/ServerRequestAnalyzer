package org.example.Stage7.client;
import com.beust.jcommander.Parameter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class Args<K,V> {
    @Parameter(names = "-t", description = "is the type of the request")
    private String type;
    @Parameter(names = "-k", description = " is the index of the cell")
    private K key;
    @Parameter(names = "-v", description = "is the value to save in the database")
    private V value;

    @Parameter(names = "-in", description = " argument followed by the file name was provided, you should read a request from that file")
    private  String textFile;


    public String getType() {
        return type;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
    public static Args typeDeterminer(String jsonData, JsonObject jsonObjectForJsonData){
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
        } else if(jsonObjectForJsonData.get("type").getAsString().equals("get")){
            if (jsonObjectForJsonData.get("key").isJsonArray()) {
                arguments = new Args<ArrayList<String>,Object>();
            }
            else {
                arguments = new Args<String,Object>();
            }
        }
        arguments = new Gson().fromJson(jsonData, Args.class);
        return arguments;
    }
    public String getTextFileName() {
        return textFile;
    }
}
