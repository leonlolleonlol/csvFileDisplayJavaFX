package com.example.csvfiledisplay;

import java.io.IOException;

import javafx.application.Platform;

class TaskLoadCSVFile implements Runnable {

    @Override
    public void run() {
        Platform.runLater(() -> {
            try {
                new CSVFile(HelloApplication.getPreviousChoice(), HelloApplication.getScreenHeight(),
                        HelloApplication.getForcedDelimiter());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
