package com.example.csvfiledisplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CSVFile {
    private static int[] maxes;
    private static TableView<Record> tableView;
    private static ObservableList<Record> dataList;
    private static int numberOfColumns, desiredWidth;
    private static final int FONT_SIZE = 12, FIXED_CELL_SIZE = 25;
    private static String fontsize = "-fx-font-size: " + FONT_SIZE + ";";
    private static ArrayList<String> header;
    private static String lastChoice;
    private static TableColumn<Record, String>[] columns;
    private static File importedFile;

    public CSVFile(String file, double screenSize, File fileImport) throws IOException {
        lastChoice = file;
        importedFile = fileImport;
        dataList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        tableView.setTranslateX(0);
        maxes = null;
        header = null;
        desiredWidth = 0;
        readCSV();
        columns = new TableColumn[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++) {
            int index = i + 1;
            columns[i] = new TableColumn<>(header.get(index - 1));
            columns[i].setCellValueFactory(data -> data.getValue().getFieldProperty("f" + index));
            tableView.setFixedCellSize(FIXED_CELL_SIZE);
            columns[i].setStyle(fontsize);
            columns[i].setId(String.valueOf(index));
        }
        tableView.setItems(dataList);
        for (var i : columns)
            tableView.getColumns().add(i);
        resetWidth(1);
        changeHeight(screenSize);
    }

    public static void changeHeight(double newHeight) {
        tableView.setPrefHeight(newHeight * HelloApplication.RATIO_CONTENT_TO_WINDOW);
    }

    public static void changeWidth(double newWidth, double screenWidth) {
        tableView.setMaxWidth(newWidth);
    }

    public static void resetWidth(double rateOfChange) {
        final double finalRate = rateOfChange * HelloApplication.RATIO_CONTENT_TO_WINDOW;
        for (int i : maxes)
            desiredWidth += i;
        tableView.getColumns().stream().forEach((column) -> column
                .setPrefWidth(FONT_SIZE * 0.6 * finalRate * (maxes[Integer.parseInt(column.getId()) - 1])));
        desiredWidth *= FONT_SIZE * 0.6;
    }

    public static BufferedReader checkFile() {
        BufferedReader b = null;
        try {
            if (lastChoice == null)
                b = new BufferedReader(new FileReader(importedFile));
            else

                b = new BufferedReader(new FileReader(lastChoice));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return b;
    }

    public void readCSV() throws IOException {
        String fieldDelimiter = ",";
        BufferedReader br = checkFile();
        if (lastChoice == null)
            fieldDelimiter = ";";
        var columnIndexRemoved = new ArrayList<Integer>();
        header = new ArrayList<String>(Arrays.asList(br.readLine().split(fieldDelimiter, -1)));
        for (int i = 0; i < header.size(); i++)
            if (i != header.size() - 1 && header.get(i).isEmpty()) {
                columnIndexRemoved.add(i);
                header.remove(i);
            }
        numberOfColumns = header.size();
        maxes = new int[numberOfColumns];
        for (int i = 0; i < numberOfColumns; i++)
            maxes[i] = Math.max(header.get(i).length(), maxes[i]);
        br.close();
        br = checkFile();
        br.readLine();
        String line = null;
        int lineNumber = 1;
        while ((line = br.readLine()) != null) {
            String[] firstFields = line.split(fieldDelimiter, numberOfColumns);
            String[] fields = new String[numberOfColumns];
            if (firstFields.length < numberOfColumns) {
                for (int i = 0; i < numberOfColumns; i++) {
                    if (i < firstFields.length)
                        fields[i] = firstFields[i];
                    else
                        fields[i] = "-";
                }
            } else
                fields = firstFields;
            if (fields != null) {
                ArrayList<String> realFields = new ArrayList<String>();
                ArrayList<String> names = new ArrayList<String>();
                for (int n = 0; n < numberOfColumns; n++) {
                    names.add("f" + (n + 1));
                    if (fields[n].isEmpty() || (fields[n].charAt(0) == ',' && fields[n].length() == 1))
                        fields[n] = "-";
                    if (fields[n].length() > 1) {
                        while (fields[n].charAt(fields[n].length() - 1) == ',')
                            fields[n] = fields[n].substring(0, fields[n].length() - 2);
                        while (fields[n].charAt(0) == ',')
                            fields[n] = fields[n].substring(1, fields[n].length());
                    }
                    realFields.add(fields[n]);
                }
                realFields = checkForErrors(fields, realFields);
                Record record = new Record(transformArrayListToArray(names), transformArrayListToArray(realFields));
                dataList.add(record);
                lineNumber++;
            }
        }
    }

    public String[] transformArrayListToArray(ArrayList<String> a) {
        return a.toArray(new String[a.size()]);
    }

    public ArrayList<String> checkForErrors(String[] fields, ArrayList<String> realFields) {
        for (int i = 0; i < numberOfColumns; i++) {
            if (fields[i].contains("$") && fields[i + 1].matches("^[0-9]+(\\.[0-9]+)?(\"?)+$")) {
                realFields.set(i, fields[i] + " " + fields[i + 1]);
                realFields.remove(realFields.get(i + 1));
                realFields.add("-");
            }
            maxes[i] = Math.max(realFields.get(i).length(), maxes[i]);
        }
        return realFields;
    }

    @Override
    public String toString() {
        return lastChoice;
    }

    public static int getFixedCellSize() {
        return FIXED_CELL_SIZE;
    }

    public static int[] getMaxes() {
        return maxes;
    }

    public static TableView<Record> getTableView() {
        return tableView;
    }

    public static ObservableList<Record> getDataList() {
        return dataList;
    }

    public static int getNumberOfColumns() {
        return numberOfColumns;
    }

    public static int getFONT_SIZE() {
        return FONT_SIZE;
    }

    public static String getFontsize() {
        return fontsize;
    }

    public static ArrayList<String> getHeader() {
        return header;
    }

    public static String getLastChoice() {
        return lastChoice;
    }

    public static TableColumn<Record, String>[] getColumns() {
        return columns;
    }

    public static int getDesiredWidth() {
        return desiredWidth;
    }
}
