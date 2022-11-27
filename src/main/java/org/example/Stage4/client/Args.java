package org.example.Stage4.client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-t", description = "is the type of the request")
    private String type;
    @Parameter(names = "-k", description = " is the index of the cell")
    private String key;
    @Parameter(names = "-v", description = "is the value to save in the database")
    private String value;


    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
