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
        tableView.setMinSize( desiredWidth, primaryStage.getHeight());
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
        int numberOfColumnsRemoved=0;
        br = new BufferedReader(new FileReader(fileName));
        String header[];
        header = br.readLine().split(FieldDelimiter, -1);
        for(int i=0;i<header.length;i++)
            if(i!=header.length-1&&header[i].isEmpty())
                numberOfColumnsRemoved++;
        numberOfColumns=header.length-numberOfColumnsRemoved;
        maxes=new int[numberOfColumns];
        br.close();
        br = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNumber=0;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(FieldDelimiter, numberOfColumns);
            ArrayList<String> realFields = new ArrayList<String>();
            String[] names = new String[numberOfColumns];
            for (int n = 0; n < numberOfColumns; n++) {
                names[n] = "f" + (n + 1);
                maxes[n]=Math.max(fields[n].length(),maxes[n]);
                fields[n]=fields[n].isEmpty()?"-":fields[n];
                if(lineNumber!=0&&fields[n].length()==1&&!fields[n].matches("^(?:\r\n" + //
                        "[\\x09\\x0A\\x0D\\x20-\\x7E]              # ASCII\r\n" + //
                        "| [\\xC2-\\xDF][\\x80-\\xBF]             # non-overlong 2-byte\r\n" + //
                        "| \\xE0[\\xA0-\\xBF][\\x80-\\xBF]         # excluding overlongs\r\n" + //
                        "| [\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}  # straight 3-byte\r\n" + //
                        "| \\xED[\\x80-\\x9F][\\x80-\\xBF]         # excluding surrogates\r\n" + //
                        "| \\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}      # planes 1-3\r\n" + //
                        "| [\\xF1-\\xF3][\\x80-\\xBF]{3}          # planes 4-15\r\n" + //
                        "| \\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}      # plane 16\r\n" + //
                        ")*$\r\n" + //
                        ""))
                    fields[n]="-";
                realFields.add(fields[n]);
            }
            realFields=checkForErrors(fields,realFields);
            String []finalFields = realFields.toArray(new String[realFields.size()]);
            Record record = new Record(names, finalFields);
            dataList.add(record);
            lineNumber++;
        }
    }
        public ArrayList<String> checkForErrors(String [] fields,ArrayList<String> realFields)
        {
            for (int i = 0; i < numberOfColumns; i++) {
                if (fields[i].contains("$")&&fields[i+1].matches("^[0-9]+(\\.[0-9]+)?(\"?)+$")) {
                    cutTheSlack(fields, realFields, i);
                }
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

