package org.example.Stage5.client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-t", description = "is the type of the request")
    private String type;
    @Parameter(names = "-k", description = " is the index of the cell")
    private String key;
    @Parameter(names = "-v", description = "is the value to save in the database")
    private String value;

    @Parameter(names = "-in", description = " argument followed by the file name was provided, you should read a request from that file")
    private  String textFile;

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    public String getTextFileName() {
        return textFile;
    }

}
