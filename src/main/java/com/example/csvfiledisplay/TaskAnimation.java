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
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pb.setProgress(HelloApplication.getLoadingTime() / 10);
            }
        };
        timer.start();
            var vBox = new StackPane();
            vBox.getChildren().add(pb);
            vBox.setPrefSize(200, 200);
            Platform.runLater(() -> {
            HelloApplication.getChangingStage().setScene(new Scene(vBox));
            HelloApplication.getChangingStage().show();
            });
    }
}
