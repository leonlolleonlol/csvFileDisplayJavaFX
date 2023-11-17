package com.example.csvfiledisplay;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

class TaskAnimation implements Runnable {
        ProgressIndicator pb = new ProgressIndicator();
        @Override
        public void run() {
            if (!HelloApplication.isFinished()) {
                AnimationTimer timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                                Platform.runLater(() -> pb.setProgress(HelloApplication.getLoadingTime()/10));
                    }
                };
                timer.start();
                if (!HelloApplication.isFinished()) {
                    Platform.runLater(() -> {
                        var vBox = new StackPane();
                        vBox.getChildren().add(pb);
                        vBox.setPrefSize(200, 200);
                        vBox.setTranslateX(0);
                        vBox.setTranslateY(0);
                        HelloApplication.getChangingStage().setScene(new Scene(vBox));
                        HelloApplication.getChangingStage().show();
                    });
                }
            } else
                Thread.currentThread().interrupt();
        }
    }
