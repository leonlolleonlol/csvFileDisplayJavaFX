package com.example.csvfiledisplay;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        tableView.getColumns().stream().forEach( (column) -> column.setPrefWidth( maxes[column.getText().charAt(1)]));
        desiredWidth*=5.67;
        //tableView.setMinSize( desiredWidth, primaryStage.getHeight());
        vBox.getChildren().add(tableView);
        vBox.setMinWidth(desiredWidth*0.95);
        //vBox.setFillWidth(true);
        root.getChildren().add(vBox);
        primaryStage.setScene(new Scene(root, desiredWidth, primaryStage.getHeight()));
        primaryStage.show();

    }
    public void readCSV(String fileName) throws IOException {
        String FieldDelimiter = ",";
        BufferedReader br;
        var columnIndexRemoved=new ArrayList<Integer>();
        br = new BufferedReader(new FileReader(fileName));
        String header[];
        header = br.readLine().split(FieldDelimiter, -1);
        for(int i=0;i<header.length;i++)
            if(i!=header.length-1&&header[i].isEmpty())
                columnIndexRemoved.add(i);
        numberOfColumns=header.length-columnIndexRemoved.size();
        maxes=new int[numberOfColumns];
        br.close();
        br = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNumber=0;
        while ((line = br.readLine()) != null) {
            if(lineNumber==277)
                lineNumber=lineNumber;
            if(lineNumber==3)
                lineNumber=lineNumber;
            String[] fields = line.split(FieldDelimiter, numberOfColumns);
            ArrayList<String> realFields = new ArrayList<String>();
            ArrayList<String> names = new ArrayList<String>();
            for (int n = 0; n < numberOfColumns; n++) {
                names.add( "f" + (n + 1));
                if(fields[n].isEmpty()||(lineNumber!=0&&fields[n].charAt(0)==','&&fields[n].length()==1))
                    fields[n]="-";
                if(fields[n].length()>1)
                    while(fields[n].charAt(fields[n].length()-1)==',')
                        fields[n]=fields[n].substring(0, fields[n].length()-2);
                if(fields[n].length()>1)
                    while(fields[n].charAt(0)==',')
                        fields[n]=fields[n].substring(1, fields[n].length()-1);
                realFields.add(fields[n]);
            }
            realFields=checkForErrors(fields,realFields);
            Record record = new Record(transformArrayListToArray(names), transformArrayListToArray(realFields));
            dataList.add(record);
            lineNumber++;
        }
    }
    public String[] transformArrayListToArray(ArrayList <String> a)
    {
        return a.toArray(new String[a.size()]);
    }
        public ArrayList<String> checkForErrors(String [] fields,ArrayList<String> realFields)
        {
            for (int i = 0; i < numberOfColumns; i++) {
                if (fields[i].contains("$")&&fields[i+1].matches("^[0-9]+(\\.[0-9]+)?(\"?)+$")) {
                    cutTheSlack(fields, realFields, i);
                }
                maxes[i]=Math.max(realFields.get(i).length(),maxes[i]);
            }
            return realFields;
        }
        public ArrayList<String> cutTheSlack(String [] fields,ArrayList<String> realFields,int i)
        {
            realFields.set(i,fields[i]+" "+fields[i+1]);
            realFields.remove(realFields.get(i+1));
            realFields.add("-");
            return realFields;
        }



public static void main(String[] args) {
        launch(args);
    }

}

