package com.example.csvfiledisplay;

import java.io.File;
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private static String previousChoice = "CATALOG_2023_09_19.csv";
    public static final double RATIO_CONTENT_TO_WINDOW = Screen.getPrimary().getVisualBounds().getHeight() / 1150;
    private static Hyperlink hyperlink = new Hyperlink("www.github.com/leonlolleonlol");
    private static File actualFile = null;
    private static boolean finished, iJustPressedAkey = false;
    private static double screenHeight, value = CSVFile.getFontSize();
    private Stage changingStage;
    private static ChoiceBox<String> cb = new ChoiceBox<>();
    private static File importedFile;

    @Override
    public void start(Stage primaryStage) throws IOException {
        changingStage = primaryStage;
        changingStage.setMaximized(true);
        cb.getItems().addAll("DATAOUTPUT_MODIFIED.csv", "WASTE_BIN_TYPE.csv", "WASTE_INVOICES.csv",
                "CATALOG_2022_08_16.csv", "CATALOG_2023_09_19.csv", "CATALOG.csv",
                "CU_SR_OPEN_DATA_TERM_SESS.csv",
                "DATA_OUTPUT.csv", "FacList.csv", "POINT_LIST.csv", "BUILDING_LIST.csv", "WASTE_TYPE.csv");
        restart();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void restart() throws IOException {
        preLoadTable();
        finished = false;
        TaskLoadCVSFile taskLoadCVSFile = new TaskLoadCVSFile();
        Thread thread3 = new Thread(taskLoadCVSFile);
        thread3.setPriority(4);
        TaskAnimation taskAnimation = new TaskAnimation();
        Thread thread2 = new Thread(taskAnimation);
        thread2.start();
        thread3.start();
        thread2.setPriority(10);
        try {
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finished = true;
        TaskDisplayTable taskDisplayTable = new TaskDisplayTable(new Group());
        Thread thread1 = new Thread(taskDisplayTable);
        cb.setValue(previousChoice);
        cb.setStyle("-fx-font-family: Arial; -fx-font-size: "+(25*Math.pow(Math.E,-0.1*cb.getItems().size())+4)+"px;");
        thread1.start();
    }

    class TaskLoadCVSFile implements Runnable {

        @Override
        public void run() {
            Platform.runLater(() -> {
                try {
                    new CSVFile(previousChoice, screenHeight, actualFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void preLoadTable() {
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        changingStage.setHeight(screenHeight);
        changingStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        if (previousChoice != null) {
            actualFile = null;
            changingStage.setTitle("CONCORDIA'S OPEN DATA -> " + previousChoice);
        }
    }

    class TaskDisplayTable implements Runnable {
        private final Group root;

        public TaskDisplayTable(Group root) {
            this.root = root;
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                root.getChildren().clear(); // Clear the children before adding new nodes
                Slider heightSlider = new Slider();
                heightSlider.setMin(CSVFile.getCellsize());
                heightSlider.setMax(changingStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
                heightSlider.setValue(heightSlider.getMax());
                heightSlider.setMinWidth(changingStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
                Label width = new Label("Adjust Width");
                width.setTranslateX(RATIO_CONTENT_TO_WINDOW * changingStage.getWidth() / 2);
                Slider widthSlider = new Slider();
                widthSlider.setTranslateY(-20);
                widthSlider.setOrientation(Orientation.VERTICAL);
                widthSlider.setRotate(180);
                widthSlider.setMin(CSVFile.getCellsize());
                widthSlider.setMax(changingStage.getHeight());
                widthSlider.setValue(widthSlider.getMax());
                widthSlider.setMinHeight(screenHeight * RATIO_CONTENT_TO_WINDOW);
                Label height = new Label("Adjust Height");
                height.setTranslateY(screenHeight * RATIO_CONTENT_TO_WINDOW / 2);
                height.setRotate(90);
                var vBox = new VBox();

                vBox.getChildren().addAll(CSVFile.getTableView(), heightSlider, width);
                vBox.setMinWidth(CSVFile.getTableView().getMinWidth());
                vBox.setAlignment(Pos.TOP_LEFT);
                HBox hBox = new HBox();
                VBox secondVbox = new VBox();
                secondVbox.setLayoutY(0);
                Label choice = new Label(
                        "Current File: (" + String.valueOf(CSVFile.getDataList().size()) + " rows & "
                                + CSVFile.getNumberOfColumns() + " columns)");
                choice.setFont(Font.font(16));
                Button buttonImport = new Button("Import your .csv file");
                var hBoxLineNumber = new HBox();
                hBoxLineNumber.setSpacing(5);
                Spinner<Integer> spinner = new Spinner<>(
                        new InvertedSpinnerValueFactory(0, CSVFile.getDataList().size(), 0, 1));
                // Configure the spinner to wrap around when reaching the minimum or maximum
                // value
                spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                    CSVFile.takemeToThisLine(newValue);
                });
                spinner.setEditable(true);
                spinner.setPrefWidth(140);
                hBoxLineNumber.getChildren().addAll(buttonImport, new Text("Starting from line "), spinner);
                secondVbox.setSpacing(5);
                Button plusButton = new Button("+");
                if (value > 23)
                    plusButton.setVisible(false);
                plusButton.setOnAction(e -> {
                    zoomInOut(1);
                });
                plusButton.setPrefSize(25, 25);
                Button minusButton = new Button("-");
                minusButton.setPrefSize(25, 25);
                if (value < 2)
                    minusButton.setVisible(false);
                minusButton.setOnAction(e -> {
                    zoomInOut(-1);
                });
                Button btn = new Button();
                btn.setText("Download File");
                btn.setOnAction(event -> {
                    File selectedFile = new File(previousChoice);
                    if (selectedFile != null) {
                        CSVFile.download(selectedFile, previousChoice);
                    }
                });
                HBox miniHBox = new HBox();
                miniHBox.setSpacing(5);
                miniHBox.setAlignment(Pos.TOP_LEFT);
                miniHBox.getChildren().addAll(cb,
                        new Text(" Zoom : " + Math.round(value * 100 / 12) + " %"), minusButton, plusButton);
                var bigPortionVBox = new VBox();
                bigPortionVBox.getChildren().addAll(height, widthSlider);
                var smallPortionVBox = new VBox();
                smallPortionVBox.setStyle("-fx-background-color: #f0f0f0;");
                smallPortionVBox.getChildren().addAll(choice, miniHBox, hBoxLineNumber, btn,
                        new Text("Made by:"),
                        hyperlink);
                smallPortionVBox.setAlignment(Pos.TOP_LEFT);
                smallPortionVBox.setTranslateY(-25);
                smallPortionVBox.setSpacing(5);
                var smallBackVBox = new VBox();
                smallBackVBox.setMinSize(changingStage.getWidth(), changingStage.getHeight());
                smallBackVBox.setTranslateX(smallPortionVBox.getTranslateX());
                smallBackVBox.setStyle("-fx-background-color: #f0f0f0;");
                widthSlider.valueProperty().addListener((
                        ObservableValue<? extends Number> ov, Number old_val,
                        Number new_val) -> {
                    CSVFile.changeHeight(widthSlider.getValue());
                    smallBackVBox.setTranslateY(
                            widthSlider.getValue() * RATIO_CONTENT_TO_WINDOW - changingStage.getHeight()
                                    + smallPortionVBox.getHeight() + 25);
                    smallPortionVBox.setTranslateY(0);

                });
                smallBackVBox.getChildren().add(smallPortionVBox);
                secondVbox.getChildren().addAll(bigPortionVBox, smallBackVBox);
                heightSlider.valueProperty().addListener((
                        ObservableValue<? extends Number> ov, Number old_val,
                        Number new_val) -> {
                    CSVFile.changeWidth(heightSlider.getValue(), heightSlider.getMax());
                    secondVbox.setTranslateX(
                            heightSlider.getValue() - changingStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
                });
                secondVbox.setSpacing(5);
                secondVbox.setAlignment(Pos.TOP_LEFT);
                hBox.getChildren().addAll(vBox, secondVbox);
                root.getChildren().addAll(hBox);
                CSVFile.resetWidth(changingStage.getWidth() / CSVFile.getDesiredWidth());
                Scene scene = new Scene(root, CSVFile.getDesiredWidth(), changingStage.getHeight());
                scene.setOnKeyPressed((event) -> {
                    if (!iJustPressedAkey) {
                        if (event.getCode() == KeyCode.ESCAPE) {
                            Platform.exit();
                        }
                        if (event.getCode() == KeyCode.ENTER) {
                            CSVFile.takemeToThisLine(spinner.getValue());
                        }
                        if ((event.getCode() == KeyCode.ADD || event.getCode() == KeyCode.PLUS) && !(value > 23)) {
                            zoomInOut(1);
                        }
                        if ((event.getCode() == KeyCode.SUBTRACT || event.getCode() == KeyCode.MINUS) && !(value < 2)) {
                            zoomInOut(-1);
                        }
                        iJustPressedAkey = true;
                        try {
                            Thread.sleep(333);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        event.consume();
                        Platform.runLater(() -> iJustPressedAkey = false);
                    }
                });
                if (importedFile!=null)
                    changingStage.setTitle("IMPORTED DATA -> " + importedFile.getName());
                if (finished) {
                    changingStage.setScene(scene);
                    changingStage.show();
                }
                hyperlink.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        getHostServices().showDocument("https://www.github.com/leonlolleonlol");
                    }
                });
                buttonImport.setOnAction(e -> {
                    importedFile = new FileChooser().showOpenDialog(changingStage);
                    Platform.runLater(() -> previousChoice = importedFile.getName());
                    CSVFile.upload(importedFile, importedFile.getName());
                    cb.getItems().add(importedFile.getName());
                    try {
                        restart();
                    } catch (IOException a) {
                        a.printStackTrace();
                    }

                });
                cb.setOnAction((event) -> {
                    previousChoice = cb.getSelectionModel().getSelectedItem();
                    try {
                        restart();
                        changingStage.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            
            });
        
        }
    }

    class TaskAnimation implements Runnable {
        long startTime;
        ProgressIndicator pb = new ProgressIndicator();

        @Override
        public void run() {
            if (!finished) {
                double[] progress = { 0.0 };
                startTime = System.nanoTime();
                AnimationTimer timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        long elapsedTime = now - startTime;
                        progress[0] = elapsedTime / 2e9; // Change this value to adjust the animation speed
                        if (progress[0] > 1.033 && !finished) {
                            progress[0] = 1.0;
                            stop();
                            try {
                                Platform.runLater(() -> {
                                    try {
                                        changingStage.close();
                                        restart();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Platform.runLater(() -> pb.setProgress(progress[0]));
                        }
                    }
                };
                timer.start();
                if (!finished) {
                    Platform.runLater(() -> {
                        var vBox=new StackPane();
                        vBox.getChildren().add(pb);
                        vBox.setPrefSize(100,100);
                        vBox.setTranslateX(0);
                        vBox.setTranslateY(0);
                        changingStage.setScene(new Scene(vBox));
                        changingStage.show();
                        
                    });
                }
            } else
                Thread.currentThread().interrupt();
        }
    }

    private void zoomInOut(int i) {
        value = 1.2 * i + value;
        CSVFile.changeTextSize(value);
        try {
            restart();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
