package org.example.client;

import com.beust.jcommander.Parameter;

public class Args<K, V> {
    @Parameter(names = "-t", description = "is the type of the request")
    private String type;
    @Parameter(names = "-k", description = " is the index of the cell")
    private K key;
    @Parameter(names = "-v", description = "is the value to save in the database")
    private V value;

    @Parameter(names = "-in", description = " argument followed by the file name was provided, you should read a request from that file")
    private String textFile;


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

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTextFile(String textFile) {
        this.textFile = textFile;
    }
}
