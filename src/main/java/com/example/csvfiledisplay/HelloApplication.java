package com.example.csvfiledisplay;

import java.io.File;
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
    private static String previousChoice = "CATALOG_2023_09_19.csv", forcedDelimiter = null;
    public static final double RATIO_CONTENT_TO_WINDOW = 0.72;
    private static Hyperlink hyperlink = new Hyperlink("www.github.com/leonlolleonlol");
    private static boolean actualFileImported = false, finished, iJustPressedAkey = false, lineChange;
    private static double screenHeight, value = CSVFile.getFontSize(), loadingTime;
    private Stage changingStage;
    private static ChoiceBox<String> cb = new ChoiceBox<>();
    private static File importedFile;
    private static int antiSpamRestarts = 0, numberOfImports = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        changingStage = primaryStage;
        changingStage.setMaximized(true);
        cb.getItems().addAll("DATAOUTPUT_MODIFIED.csv", "WASTE_BIN_TYPE.csv", "WASTE_INVOICES.csv",
                "CATALOG_2022_08_16.csv", "CATALOG_2023_09_19.csv", "CATALOG.csv",
                "CU_SR_OPEN_DATA_TERM_SESS.csv",
                "DATA_OUTPUT.csv", "FacList.csv", "POINT_LIST.csv", "BUILDING_LIST.csv", "WASTE_TYPE.csv");
        changingStage.getIcons().add(new Image("file:download.png"));

        cb.setValue(previousChoice);
        choiceBoxUpdate();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void restart() {
        try {
            long startTime = System.nanoTime();
            preLoadTable();
            finished = false;
            TaskLoadCVSFile taskLoadCVSFile = new TaskLoadCVSFile();
            Thread thread3 = new Thread(taskLoadCVSFile);
            thread3.setPriority(1);
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
            Platform.runLater(() -> {
                TaskDisplayTable taskDisplayTable = new TaskDisplayTable(new Group(), startTime);
                Thread thread1 = new Thread(taskDisplayTable);
                cb.setStyle(
                        "-fx-font-family: Arial; -fx-font-size: "
                                + (25 * Math.pow(Math.E, -0.1 * cb.getItems().size()) + 4)
                                + "px;");
                thread1.start();
            });
        } catch (Exception e) {
            e.getMessage();
        }
    }

    class TaskLoadCVSFile implements Runnable {

        @Override
        public void run() {
            Platform.runLater(() -> {
                try {
                    new CSVFile(previousChoice, screenHeight, forcedDelimiter);
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
    }

    class TaskDisplayTable implements Runnable {
        private final Group root;
        private final long startTime;

        public TaskDisplayTable(Group root, long startTime) {
            this.root = root;
            this.startTime = startTime;
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

                Button buttonImport = new Button("Import your .csv file");

                Spinner<Integer> spinner = new Spinner<>(
                        new InvertedSpinnerValueFactory(0, CSVFile.getDataList().size(), 0, 1));
                spinner.valueProperty()
                        .addListener((observable, oldValue, newValue) -> CSVFile.takemeToThisLine(newValue));
                spinner.setEditable(true);
                spinner.setPrefWidth(140);

                var hBoxLineNumber = new HBox();
                hBoxLineNumber.setSpacing(5);
                hBoxLineNumber.getChildren().addAll(buttonImport, new Text("Starting from line "), spinner);

                VBox secondVbox = new VBox();
                secondVbox.setLayoutY(0);
                secondVbox.setSpacing(5);
                secondVbox.setAlignment(Pos.TOP_LEFT);

                Button plusButton = new Button("+");
                if (value > 23)
                    plusButton.setVisible(false);
                plusButton.setOnAction(e -> zoomInOut(1));
                plusButton.setPrefSize(25, 25);

                Button minusButton = new Button("-");
                minusButton.setPrefSize(25, 25);
                if (value < 2)
                    minusButton.setVisible(false);
                minusButton.setOnAction(e -> zoomInOut(-1));

                Button downloadButton = new Button();
                downloadButton.setText("Download File");
                downloadButton.setOnAction(event -> {
                    File selectedFile = new File(previousChoice);
                    if (selectedFile != null)
                        CSVFile.download(selectedFile, previousChoice);
                });

                Button resetZoom = new Button("reset");
                if (value == 12)
                    resetZoom.setVisible(false);
                resetZoom.setOnAction(event -> zoomInOut(0));
                resetZoom.setPrefSize(50, 25);

                HBox miniHBox = new HBox();
                miniHBox.setSpacing(5);
                miniHBox.setAlignment(Pos.TOP_LEFT);
                miniHBox.getChildren().addAll(cb,
                        new Text(" Zoom : " + Math.round(value * 100 / 12) + " %"), minusButton, plusButton, resetZoom);

                var bigPortionVBox = new VBox();
                bigPortionVBox.getChildren().addAll(height, widthSlider);

                var delimiterHBox = new HBox();
                delimiterHBox.setSpacing(5);

                var delimiterCharTextField = new TextField();
                delimiterCharTextField.setMaxSize(30, 30);
                delimiterCharTextField.setText(CSVFile.getFieldDelimiter());
                delimiterCharTextField.setEditable(true);
                delimiterHBox.getChildren().addAll(downloadButton, new Text("Separated by "), delimiterCharTextField);

                loadingTime = Double.parseDouble(String.format("%.2f", (System.nanoTime() - startTime) / 1e9));
                Label choice = new Label(
                        "Current File: (" + String.valueOf(CSVFile.getDataList().size()) + " rows & "
                                + CSVFile.getNumberOfColumns() + " columns) loaded in " + loadingTime + " s");
                choice.setFont(Font.font(16));

                var creditsHbox = new HBox();
                creditsHbox.setSpacing(5);
                creditsHbox.getChildren().addAll(new Text("Made by:"), hyperlink);

                TextField searchField = new TextField();
                searchField.setPromptText("Search...");

                // Add a button to trigger the search
                Button searchButton = new Button("Search");
                searchButton.setOnAction(e -> performSearch(searchField.getText()));

                var searchHBox = new HBox();
                searchHBox.setSpacing(5);
                searchHBox.getChildren().addAll(searchField, searchButton);

                var smallPortionVBox = new VBox();
                smallPortionVBox.setStyle("-fx-background-color: #f0f0f0;");
                smallPortionVBox.getChildren().addAll(choice, miniHBox, hBoxLineNumber, delimiterHBox, creditsHbox,
                        searchHBox);
                smallPortionVBox.setAlignment(Pos.TOP_LEFT);
                smallPortionVBox.setTranslateY(-25);
                smallPortionVBox.setSpacing(5);

                var smallBackgroundVBox = new VBox();
                smallBackgroundVBox.setMinSize(changingStage.getWidth(), changingStage.getHeight());
                smallBackgroundVBox.setTranslateX(smallPortionVBox.getTranslateX());
                smallBackgroundVBox.setStyle("-fx-background-color: #f0f0f0;");

                widthSlider.valueProperty().addListener((
                        ObservableValue<? extends Number> ov, Number old_val,
                        Number new_val) -> {
                    CSVFile.changeHeight(widthSlider.getValue());
                    smallBackgroundVBox.setTranslateY(
                            widthSlider.getValue() * RATIO_CONTENT_TO_WINDOW - widthSlider.getMinHeight()
                                    - widthSlider.getMin());
                    smallPortionVBox.setTranslateY(0);
                });

                smallBackgroundVBox.getChildren().add(smallPortionVBox);
                secondVbox.getChildren().addAll(bigPortionVBox, smallBackgroundVBox);

                heightSlider.valueProperty().addListener((
                        ObservableValue<? extends Number> ov, Number old_val,
                        Number new_val) -> {
                    CSVFile.changeWidth(heightSlider.getValue(), heightSlider.getMax());
                    secondVbox.setTranslateX(
                            heightSlider.getValue() - changingStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
                });

                HBox hBox = new HBox();
                hBox.getChildren().addAll(vBox, secondVbox);
                root.getChildren().addAll(hBox);

                CSVFile.resetWidth(changingStage.getWidth() / CSVFile.getDesiredWidth());
                Scene scene = new Scene(root, CSVFile.getDesiredWidth(), changingStage.getHeight());
                scene.setOnKeyPressed((event) -> {
                    if (!iJustPressedAkey) {
                        if (event.getCode() == KeyCode.ESCAPE)
                            Platform.exit();
                        if (event.getCode() == KeyCode.ENTER) {
                            if (lineChange) {
                                CSVFile.takemeToThisLine(spinner.getValue());
                                lineChange = false;
                            } else {
                                forcedDelimiter = delimiterCharTextField.getText();
                                restart();
                            }
                        }
                        if ((event.getCode() == KeyCode.ADD || event.getCode() == KeyCode.PLUS) && !(value > 23))
                            zoomInOut(1);
                        if ((event.getCode() == KeyCode.SUBTRACT || event.getCode() == KeyCode.MINUS) && !(value < 2))
                            zoomInOut(-1);
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

                if (!actualFileImported)
                    changingStage.setTitle("CONCORDIA'S OPEN DATA -> " + previousChoice);
                else {
                    changingStage.setTitle("IMPORTED DATA -> " + importedFile.getName());
                    if (antiSpamRestarts == numberOfImports)
                        restart();
                    antiSpamRestarts++;
                }

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
                    antiSpamRestarts = 0;
                    numberOfImports++;
                    actualFileImported = true;
                    importedFile = new FileChooser().showOpenDialog(changingStage);
                    previousChoice = importedFile.getName();
                    CSVFile.upload(importedFile, importedFile.getName());
                    cb.getItems().add(importedFile.getName());
                    cb.setValue(previousChoice);
                    choiceBoxUpdate();
                });

                cb.setOnAction((event) -> choiceBoxUpdate());

                spinner.setOnKeyPressed((event) -> lineChange = true);
            });
        }
    }

    public void choiceBoxUpdate() {
        previousChoice = cb.getSelectionModel().getSelectedItem();
        restart();
    }

    private void performSearch(String searchTerm) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Create a filtered list based on the search term
            FilteredList<Record> filteredList = CSVFile.getDataList()
                    .filtered(record -> record.containsSearchTerm(searchTerm));
            // Update the TableView with the filtered list
            CSVFile.getTableView().setItems(filteredList);
        } else {
            // If search term is empty, show all rows
            CSVFile.getTableView().setItems(CSVFile.getDataList());
        }
    }

    class TaskAnimation implements Runnable {
        ProgressIndicator pb = new ProgressIndicator();

        @Override
        public void run() {
            if (!finished) {
                double[] progress = { 0.0 };
                AnimationTimer timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        if (CSVFile.getHeader() != null) {
                            progress[0] = CSVFile.getRowsLoaded() / CSVFile.getInitialRows();
                            if (progress[0] > 1.0 && !finished) {
                                progress[0] = 1.0;
                                stop();
                                Platform.runLater(() -> restart());
                            } else
                                Platform.runLater(() -> pb.setProgress(progress[0]));
                        }
                    }
                };
                timer.start();
                if (!finished) {
                    Platform.runLater(() -> {
                        var vBox = new StackPane();
                        vBox.getChildren().add(pb);
                        vBox.setPrefSize(200, 200);
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
        if (i == 0)
            value = 12;
        CSVFile.changeTextSize(value);
        Platform.runLater(() -> restart());
    }
}
