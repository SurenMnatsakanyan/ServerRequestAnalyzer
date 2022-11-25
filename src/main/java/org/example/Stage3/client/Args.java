package org.example.Stage3.client;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-t", description = "is the type of the request")
    private String typeOfParam;
    @Parameter(names = "-i", description = " is the index of the cell")
    private Integer indexCell;
    @Parameter(names = "-m", description = "is the value to save in the database")
    private String cellValue;

    public String getTypeOfParam() {
        return typeOfParam;
    }

    public Integer getIndexCell() {
        return indexCell;
    }

    public String getCellValue() {
        return cellValue;
    }
}
