package com.example.csvfiledisplay;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
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
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private static String previousChoice = "CATALOG_2023_09_19.csv";
    private static Group root;
    private static double screenHeight;
    public static final double RATIO_CONTENT_TO_WINDOW = Screen.getPrimary().getVisualBounds().getHeight() / 1300;
    private static Hyperlink hyperlink = new Hyperlink("www.github.com/leonlolleonlol");
    private static File actualFile = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setHeight(screenHeight);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll("DATAOUTPUT_MODIFIED.csv", "WASTE_BIN_TYPE.csv", "WASTE_INVOICES.csv",
                "CATALOG_2022_08_16.csv", "CATALOG_2023_09_19.csv", "CATALOG.csv", "CU_SR_OPEN_DATA_TERM_SESS.csv",
                "DATA_OUTPUT.csv", "FacList.csv", "POINT_LIST.csv", "BUILDING_LIST.csv", "WASTE_TYPE.csv");
        if (previousChoice != null) {
            actualFile = null;
            primaryStage.setTitle("CONCORDIA'S OPEN DATA -> " + previousChoice);
        }
        reset();
        cb.setValue(previousChoice);
        Slider heightSlider = new Slider() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                for (javafx.scene.Node node : lookupAll(".track")) {
                    node.setVisible(false);
                    node.setManaged(false);
                }
            }
        };
        heightSlider.setMin(CSVFile.getFixedCellSize());
        heightSlider.setMax(primaryStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
        heightSlider.setValue(heightSlider.getMax());
        heightSlider.setMinWidth(primaryStage.getWidth() * RATIO_CONTENT_TO_WINDOW);
        heightSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeWidth(heightSlider.getValue(), heightSlider.getMax());
        });
        Label width = new Label("Adjust Width");
        width.setTranslateX(RATIO_CONTENT_TO_WINDOW * primaryStage.getWidth() / 2);
        Slider widthSlider = new Slider() {
            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                for (javafx.scene.Node node : lookupAll(".track")) {
                    node.setVisible(false);
                    node.setManaged(false);
                }
            }
        };
        widthSlider.setTranslateY(-20);
        widthSlider.setOrientation(Orientation.VERTICAL);
        widthSlider.setRotate(180);
        widthSlider.setMin(CSVFile.getFixedCellSize());
        widthSlider.setMax(primaryStage.getHeight());
        widthSlider.setValue(widthSlider.getMax());
        widthSlider.setMinHeight(screenHeight * RATIO_CONTENT_TO_WINDOW);
        widthSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeHeight(widthSlider.getValue());
        });
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
        Label choice = new Label("Current File");
        choice.setFont(Font.font(16));
        Button buttonImport = new Button("Import your .csv file");
        secondVbox.setSpacing(5);
        secondVbox.getChildren().addAll(height, widthSlider, choice, cb, new Text(String.valueOf(CSVFile.getDataList().size())+" rows & "+CSVFile.getNumberOfColumns()+" columns."),buttonImport, new Text("Made by:"), hyperlink);
        secondVbox.setAlignment(Pos.TOP_LEFT);
        hBox.getChildren().addAll(vBox, secondVbox);
        root.getChildren().addAll(hBox);
        CSVFile.resetWidth(primaryStage.getWidth() / CSVFile.getDesiredWidth());
        Scene scene = new Scene(root, CSVFile.getDesiredWidth(), primaryStage.getHeight());
        if (actualFile != null)
            primaryStage.setTitle("IMPORTED DATA -> " + actualFile.toString());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                getHostServices().showDocument("https://www.github.com/leonlolleonlol");
            }
        });
        buttonImport.setOnAction(e -> {
            actualFile = new FileChooser().showOpenDialog(primaryStage);
            // si on peut lire le file
            if (actualFile.exists() && actualFile.canRead()) {
                previousChoice = null;
                try {
                    primaryStage.close();
                    start(new Stage());
                } catch (IOException a) {
                    a.printStackTrace();
                }
            }

        });
        cb.setOnAction((event) -> {
            previousChoice = cb.getSelectionModel().getSelectedItem();
            try {
                primaryStage.close();
                start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void reset() throws IOException {
        new CSVFile(previousChoice, screenHeight, actualFile);
        root = new Group();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
