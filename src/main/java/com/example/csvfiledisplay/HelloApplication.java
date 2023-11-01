package com.example.csvfiledisplay;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class HelloApplication extends Application {
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
    private int[] maxes;
    private final TableView<Record> tableView = new TableView<>();

    private final ObservableList<Record> dataList
            = FXCollections.observableArrayList();
    int numberOfColumns;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Display");
        readCSV("WASTE_BIN_TYPE.csv");
        Group root = new Group();
        TableColumn<Record, String>[] columns = new TableColumn[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            int index = i + 1;
            columns[i] = new TableColumn<>("f" + index);
            columns[i].setCellValueFactory(data -> data.getValue().getFieldProperty("f" + index));
            columns[i].setStyle("-fx-font-size: 10;");
            columns[i].setPrefWidth(Math.min(maxes[i]*5.66,1650/numberOfColumns));
        }
        tableView.setItems(dataList);
        for (var i: columns)
            tableView.getColumns().add(i);

        VBox vBox = new VBox();

        vBox.getChildren().add(tableView);

        root.getChildren().add(vBox);

        primaryStage.setScene(new Scene(root, 1000, 750));
        primaryStage.show();

    }
    public void readCSV(String fileName) throws IOException {
        String FieldDelimiter = ",";
        BufferedReader br;
        br = new BufferedReader(new FileReader(fileName));
        numberOfColumns = br.readLine().split(FieldDelimiter, -1).length;
        maxes=new int[numberOfColumns];
        br.close();
        br = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(FieldDelimiter, numberOfColumns);
            String[] names = new String[numberOfColumns];
            for (int n = 0; n < numberOfColumns; n++) {
                names[n] = "f" + (n + 1);
                maxes[n]=Math.max(fields[n].length(),maxes[n]);
            }
            HelloApplication.Record record = new HelloApplication.Record(names, fields);
            dataList.add(record);
        }
    }



public static void main(String[] args) {
        launch(args);
    }

}

