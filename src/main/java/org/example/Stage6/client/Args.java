package org.example.Stage6.client;

import com.beust.jcommander.Parameter;
import com.google.gson.JsonObject;

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

    public String getTextFileName() {
        return textFile;
    }
}
