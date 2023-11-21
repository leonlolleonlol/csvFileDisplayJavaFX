package com.example.csvfiledisplay;

import java.io.IOException;


class TaskLoadCSVFile implements Runnable {
    private final static CSVFile firstCsv=null;
    @Override
    public void run() {
            try {
                new CSVFile(HelloApplication.getPreviousChoice(), HelloApplication.getScreenHeight(),
                        HelloApplication.getForcedDelimiter());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static CSVFile getFirstcsv() {
        return firstCsv;
    }
    
}
