package com.example.csvfiledisplay;

import javafx.beans.property.SimpleStringProperty;

import java.util.HashMap;
import java.util.Map;

public class Record {

    private Map<String, SimpleStringProperty> fields;
    public SimpleStringProperty getFieldProperty(String fieldName) {
        return fields.get(fieldName);
    }

    public Record(String[] fieldNames, String[] fieldValues) {
        if (fieldNames.length != fieldValues.length) {
            throw new IllegalArgumentException("Field names and values arrays must have the same length.");
        }

        this.fields = new HashMap<>();
        for (int i = 0; i < fieldValues.length; i++) {
            this.fields.put(fieldNames[i], new SimpleStringProperty(fieldValues[i]));
        }
    }


}
