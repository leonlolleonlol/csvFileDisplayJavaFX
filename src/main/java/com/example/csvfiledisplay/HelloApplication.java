package com.example.csvfiledisplay;


import java.io.IOException;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    private static String previousChoice="DATAOUTPUT_MODIFIED.csv";
    private static CSVFile csvFile;
    private static Group root;
    private static VBox vBox;
    private static double screenHeight;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Display");
        screenHeight=Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setHeight(screenHeight);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll("DATAOUTPUT_MODIFIED.csv", "WASTE_BIN_TYPE.csv", "WASTE_INVOICES.csv");
        reset();
        cb.setValue(previousChoice);
        cb.setOnAction((event) -> {
            previousChoice = cb.getSelectionModel().getSelectedItem();
            System.out.println(0);
            try {
                primaryStage.close();
                start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Slider heightSlider = new Slider();
        heightSlider.setMin(CSVFile.getFixedCellSize());
        heightSlider.setMax(primaryStage.getWidth()*0.9);
        heightSlider.setValue(heightSlider.getMax());
        heightSlider.setMaxWidth(primaryStage.getWidth()*0.9);
        heightSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeWidth(heightSlider.getValue());
        });
        Label width = new Label("Adjust Width");
        Slider widthSlider = new Slider();
        widthSlider.setOrientation(Orientation.VERTICAL);
        //widthSlider.setTranslateY(screenHeight*0.9/2);
        widthSlider.setRotate(180);
        widthSlider.setMin(CSVFile.getFixedCellSize());
        widthSlider.setMax(primaryStage.getHeight());
        widthSlider.setValue(widthSlider.getMax());
        widthSlider.setMinHeight(screenHeight*0.9);
        widthSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeHeight(widthSlider.getValue());
        });
        Label height = new Label("Adjust Height");
        height.setTranslateY(screenHeight*0.9/2);
        height.setRotate(90);
        vBox.getChildren().addAll(CSVFile.getTableView(),heightSlider,width);
        vBox.setMinWidth(CSVFile.getDesiredWidth());
        vBox.setAlignment(Pos.CENTER);
        HBox hBox=new HBox();
        VBox secondVbox=new VBox();
        Label choice=new Label("Current File");
        secondVbox.getChildren().addAll(height,widthSlider,choice,cb);
        hBox.getChildren().addAll(vBox,secondVbox);
        root.getChildren().addAll(hBox);
        if(CSVFile.getDesiredWidth()>primaryStage.getWidth());
            CSVFile.resetWidth(primaryStage.getWidth()/CSVFile.getDesiredWidth());
        Scene scene=new Scene(root, CSVFile.getDesiredWidth(), primaryStage.getHeight());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public static void reset() throws IOException
    {
    csvFile=new CSVFile(previousChoice,screenHeight);
    csvFile.readCSV();
    root = new Group();
    vBox = new VBox();
    }


public static void main(String[] args) {
        launch(args);
    }

}

