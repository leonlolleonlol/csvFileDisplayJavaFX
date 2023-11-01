package com.example.csvfiledisplay;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVFile {
    boolean processFinished=false;
    String fileName;
    ArrayList<String[]> list= new ArrayList<>();
    public CSVFile(String file)
    {
        this.fileName=file;
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            br.readLine();
            while ((line = br.readLine()) != null)
                list.add(line.replaceAll("\"", "").split(","));
            br.close();
            processFinished=true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public ArrayList<String> getRow(int column)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        for (String[] strings : list) {
            arrayList.add(strings[column]);
        }
            return arrayList;
        }
    @Override
    public String toString() {
        return fileName;
    }
}
