package com.example.csvfiledisplay;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;

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

    // Add this method to check if any field contains the search term
    public boolean containsSearchTerm(String searchTerm) {
        for (SimpleStringProperty field : fields.values()) {
            if (field.get().toLowerCase().contains(searchTerm.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
