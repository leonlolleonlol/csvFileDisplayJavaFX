package com.example.csvfiledisplay;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    private int[] maxes;
    private final TableView<Record> tableView = new TableView<>();

    private final ObservableList<Record> dataList
            = FXCollections.observableArrayList();
    int numberOfColumns;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Display");
        primaryStage.setHeight(Screen.getPrimary().getVisualBounds().getHeight()*0.9);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth()*0.9);
        readCSV("WASTE_INVOICES.csv");
        Group root = new Group();
        TableColumn<Record, String>[] columns = new TableColumn[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            int index = i + 1;
            columns[i] = new TableColumn<>("f" + index);
            columns[i].setCellValueFactory(data -> data.getValue().getFieldProperty("f" + index));
            columns[i].setStyle("-fx-font-size: 12;");
        }
        tableView.getColumns().stream().forEach( (column) -> column.setPrefWidth( maxes[column.getText().charAt(1)] + 10.0d ));
        tableView.setItems(dataList);
        for (var i: columns)
            tableView.getColumns().add(i);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        //primaryStage.sizeToScene();
        //tableView.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
        int desiredWidth=0;
        for(int i:maxes)
            desiredWidth+=i;
        desiredWidth*=5.66;
        tableView.setMinSize( desiredWidth-25, primaryStage.getHeight());
        vBox.getChildren().add(tableView);
        vBox.setMinWidth(desiredWidth*0.9);
        //vBox.setFillWidth(true);
        root.getChildren().add(vBox);
        primaryStage.setScene(new Scene(root, desiredWidth, primaryStage.getHeight()));
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
            String[] realFields = new String[numberOfColumns];
            String[] names = new String[numberOfColumns];
            int indexCommaDelimiterInMoneyString=-1;
            for (int n = 0; n < numberOfColumns; n++) {
                names[n] = "f" + (n + 1);
                maxes[n]=Math.max(fields[n].length(),maxes[n]);
                if (fields[n].contains("$")&&fields[n+1].matches("^[0-9]+(\\.[0-9]+)?(\"?)+$")) {
                    realFields[n]=fields[n]+" "+fields[n+1];
                    indexCommaDelimiterInMoneyString=n;
                }
                if(n!=numberOfColumns-1)
                {
                if(indexCommaDelimiterInMoneyString<n)
                    realFields[n]=fields[n+1].isEmpty()?"-":fields[n+1];
                else if (indexCommaDelimiterInMoneyString!=n)
                    realFields[n]=fields[n].isEmpty()?"-":fields[n];
                }
                else if(indexCommaDelimiterInMoneyString<n)
                    realFields[n]="-";


            }
            Record record = new Record(names, realFields);
            dataList.add(record);
        }
    }



public static void main(String[] args) {
        launch(args);
    }

}

