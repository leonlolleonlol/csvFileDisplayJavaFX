package com.example.csvfiledisplay;

import java.io.IOException;


class TaskLoadCSVFile implements Runnable {

    @Override
    public void run() {
            try {
                new CSVFile(HelloApplication.getPreviousChoice(), HelloApplication.getScreenHeight(),
                        HelloApplication.getForcedDelimiter());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
