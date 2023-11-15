package com.example.csvfiledisplay;

import static java.nio.charset.StandardCharsets.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static int numberOfColumns, desiredWidth,initialRows=1,rowsLoaded=1;
    private static ArrayList<String> header;
    private static TableColumn<Record, String>[] columns;
    private static double cellSize = 25, fontSize = 12;
    private static String lastChoice, fontSizeString = "-fx-font-size: " + fontSize + ";",fieldDelimiter=",",overrideDelimiter;

    public CSVFile(String file, double screenSize,String forcedDelimiter) throws IOException {
        overrideDelimiter=forcedDelimiter;
        lastChoice = file;
        dataList = FXCollections.observableArrayList();
        tableView = new TableView<>();
        tableView.setEditable(true);
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
            tableView.setFixedCellSize(cellSize);
            columns[i].setStyle(fontSizeString);
            columns[i].setId(String.valueOf(index));
        }
        tableView.setItems(dataList);
        for (var i : columns)
            tableView.getColumns().add(i);
        resetWidth(1);
        changeHeight(screenSize);
        takemeToThisLine(0);
    }

    public static void takemeToThisLine(int lineNumber) {
        if (lineNumber > -1 && lineNumber < dataList.size() - 1)
            tableView.scrollTo(lineNumber);
    }

    public static void setCellSize(double cellSize) {
        CSVFile.cellSize = cellSize;
    }

    public static void changeHeight(double newHeight) {
        tableView.setPrefHeight(newHeight * HelloApplication.RATIO_CONTENT_TO_WINDOW);
    }

    public static void changeTextSize(double newSize) {
        fontSize = newSize;
        fontSizeString = "-fx-font-size: " + fontSize + ";";
        setCellSize(newSize * 25 / 12);
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
                b = new BufferedReader(new FileReader(lastChoice, lastChoice.substring(0,2)=="CU"?UTF_16LE:UTF_8));
            if (lastChoice != null && lastChoice.equals("DATA_OUTPUT.csv"))
                for (int i = 0; i < 17; i++)
                    b.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
    public static String findDelimiterChar() throws IOException
    {
        var delimiter = new BufferedReader(new FileReader(lastChoice, lastChoice.substring(0,2)=="CU"?UTF_16LE:UTF_8));
        if (lastChoice != null && lastChoice.equals("DATA_OUTPUT.csv"))
                for (int i = 0; i < 17; i++)
                    delimiter.readLine();
        int commas=0;
        for(char c:delimiter.readLine().toCharArray())
        {
            if(c==',')
                commas++;
            else if (c==';')
                commas--;
        }
        while (delimiter.readLine() != null)
            initialRows++;
        delimiter.close();
        return commas>0?",":";";
    }

    public void readCSV() throws IOException {
        fieldDelimiter=findDelimiterChar();
        if(overrideDelimiter!=null)
            fieldDelimiter=overrideDelimiter.substring(0,1);
        BufferedReader br = checkFile();
        var columnIndexRemoved = new ArrayList<Integer>();
        header = new ArrayList<String>(Arrays.asList(br.readLine().replaceAll("\"", "").split(fieldDelimiter, -1)));
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
        while ((line = br.readLine().replaceAll("\"", "")) != null) {
            Pattern pattern = Pattern.compile("\"");
            Matcher matcher = pattern.matcher(line);
            int count = 0;
            while (matcher.find())
                count++;
            if (count == 1) {
                line += br.readLine();
            }
            pattern = Pattern.compile(fieldDelimiter+"(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
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
                            fields[n] = characterToString.toUpperCase() + fields[n].substring(1);
                        else if (characterToString.matches(" "))
                            fields[n] = fields[n].substring(1);
                    }
                    realFields.add(fields[n]);
                }
                realFields = updateMaxes(fields, realFields);
                Record record = new Record(transformArrayListToArray(names), transformArrayListToArray(realFields));
                dataList.add(record);
                rowsLoaded++;
            }
        }
    }

    private static void openExplorer(Path destination) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        if (os.contains("win"))
            // For Windows
            rt.exec("explorer.exe /select," + destination);
        else if (os.contains("mac"))
            // For Mac
            rt.exec("open " + destination);
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix"))
            // For Linux
            rt.exec("xdg-open " + destination);
        else
            throw new UnsupportedOperationException("Operating system not supported");
    }

    public static void download(File selectedFile, String previousChoice) {
        try {
            Path source = Paths.get(previousChoice); // Specify the path to your file
            Path destination = Paths.get(System.getProperty("user.home"), "Downloads",
                    selectedFile.getName());
            byte[] data = Files.readAllBytes(source);
            Files.write(destination, data);
            openExplorer(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void upload(File selectedFile, String previousChoice) {
        try {
            Path source = Paths.get(previousChoice); // Specify the path to your file
            Path destination = Paths.get(System.getProperty("user.home"), "Downloads",
                    selectedFile.getName());
            byte[] data = Files.readAllBytes(destination);
            Files.write(source, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] transformArrayListToArray(ArrayList<String> a) {
        return a.toArray(new String[a.size()]);
    }

    public ArrayList<String> updateMaxes(String[] fields, ArrayList<String> realFields) {
        for (int i = 0; i < numberOfColumns; i++)
            maxes[i] = Math.max(realFields.get(i).length(), maxes[i]);
        return realFields;
    }

    @Override
    public String toString() {
        return lastChoice;
    }

    public static double getCellsize() {
        return cellSize;
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

    public static String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public static void setFieldDelimiter(String fieldDelimiter) {
        if(!fieldDelimiter.isBlank())
            CSVFile.fieldDelimiter = fieldDelimiter.substring(0,1);
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
    

    public static int getInitialRows() {
        return initialRows;
    }

    public static int getRowsLoaded() {
        return rowsLoaded;
    }

    public static void setFontSize(double fontSize) {
        CSVFile.fontSize = fontSize;
    }
}
