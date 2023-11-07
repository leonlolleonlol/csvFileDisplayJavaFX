package com.example.csvfiledisplay;

import static java.nio.charset.StandardCharsets.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CSVFile {
    private static int[] maxes;
    private static TableView<Record> tableView;
    private static ObservableList<Record> dataList;
    private static double screenSizeSaved;
    private static int numberOfColumns, desiredWidth, fontSize = 12;
    private static final int FIXED_CELL_SIZE = 25;
    private static String lastChoice, fontSizeString = "-fx-font-size: " + fontSize + ";";
    private static ArrayList<String> header;
    private static TableColumn<Record, String>[] columns;
    private static File importedFile;

    public CSVFile(String file, double screenSize, File fileImport) throws IOException {
        lastChoice = file;
        importedFile = fileImport;
        screenSizeSaved = screenSize;
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
            columns[i].setStyle(fontSizeString);
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

    public static void changeTextSize(int newSize) {
        fontSize = newSize;
        fontSizeString = "-fx-font-size: " + fontSize + ";";
    }

    public static void changeWidth(double newWidth, double screenWidth) {
        tableView.setMaxWidth(newWidth);
    }

    public static void resetWidth(double rateOfChange) {
        final double finalRate = rateOfChange * HelloApplication.RATIO_CONTENT_TO_WINDOW;
        for (int i : maxes)
            desiredWidth += i;
        tableView.getColumns().stream().forEach((column) -> column
                .setPrefWidth(fontSize * 0.6 * finalRate * (maxes[Integer.parseInt(column.getId()) - 1])));
        desiredWidth *= fontSize * 0.6;
    }

    public static BufferedReader checkFile() {
        BufferedReader b = null;
        try {
            if (lastChoice == null)
                b = new BufferedReader(new FileReader(importedFile, UTF_8));
            else
                b = new BufferedReader(new FileReader(lastChoice, UTF_8));
            if (lastChoice != null && lastChoice.equals("DATA_OUTPUT.csv"))
                for (int i = 0; i < 17; i++)
                    b.readLine();
        } catch (IOException e) {
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
        while ((line = br.readLine()) != null) {
            Pattern pattern = Pattern.compile("\"");
            Matcher matcher = pattern.matcher(line);
            int count = 0;
            while (matcher.find())
                count++;
            if (count == 1) {
                line += br.readLine();
            }
            pattern = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] firstFields = pattern.split(line);
            if (firstFields != null) {
                String[] fields = new String[numberOfColumns];
                for (int i = 0; i < firstFields.length; i++)
                    if (firstFields[i] != null && firstFields[i].length() > 2 && firstFields[i].charAt(0) == '\"'
                            && firstFields[i].charAt(firstFields[i].length() - 1) == '\"')
                        firstFields[i] = firstFields[i].substring(1, firstFields[i].length() - 1);
                if (firstFields.length < numberOfColumns) {
                    for (int i = 0; i < numberOfColumns; i++) {
                        if (i < firstFields.length)
                            fields[i] = firstFields[i];
                        else
                            fields[i] = "-";
                    }
                } else
                    fields = firstFields;
                ArrayList<String> realFields = new ArrayList<String>();
                ArrayList<String> names = new ArrayList<String>();
                for (int n = 0; n < numberOfColumns; n++) {
                    names.add("f" + (n + 1));
                    if (fields[n].isEmpty() || ((fields[n].charAt(0) == ',' || fields[n].charAt(0) == '\u00a0')
                            && fields[n].length() == 1))
                        fields[n] = "-";
                    if (fields[n].length() > 1) {
                        String characterToString = String.valueOf(fields[n].charAt(0));
                        if (characterToString.matches("[a-z]"))
                            fields[n] = String.valueOf(characterToString).toUpperCase() + fields[n].substring(1);
                        else if (characterToString.matches(" "))
                            fields[n] = fields[n].substring(1);
                    }
                    realFields.add(fields[n]);
                }
                realFields = checkForErrors(fields, realFields);
                Record record = new Record(transformArrayListToArray(names), transformArrayListToArray(realFields));
                dataList.add(record);
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

    public static double getFontSize() {
        return fontSize;
    }

    public static String getFontsizestring() {
        return fontSizeString;
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
