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
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class HelloApplication extends Application {
    private static String previousChoice="DATAOUTPUT_MODIFIED.csv";
    private static CSVFile csvFile;
    private static Group root;
    private static double screenHeight;
    public static final double RATIO_CONTENT_TO_WINDOW=Screen.getPrimary().getVisualBounds().getHeight()/1150;
    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("CONCORDIA'S OPEN DATA -> "+previousChoice);
        screenHeight=Screen.getPrimary().getVisualBounds().getHeight();
        primaryStage.setHeight(screenHeight);
        primaryStage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
        ChoiceBox<String> cb = new ChoiceBox<>();
        cb.getItems().addAll("DATAOUTPUT_MODIFIED.csv", "WASTE_BIN_TYPE.csv", "WASTE_INVOICES.csv","CATALOG_2022_08_16.csv","CATALOG_2023_09_19.csv","CATALOG.csv","CU_SR_OPEN_DATA_TERM_SESS.csv","DATA_OUTPUT.csv","FacList.csv","POINT_LIST.csv","BUILDING_LIST.csv","WASTE_TYPE.csv");
        reset();
        cb.setValue(previousChoice);
        cb.setOnAction((event) -> {
            previousChoice = cb.getSelectionModel().getSelectedItem();
            try {
                primaryStage.close();
                start(new Stage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Slider heightSlider = new Slider();
        heightSlider.setMin(CSVFile.getFixedCellSize());
        heightSlider.setMax(primaryStage.getWidth()*RATIO_CONTENT_TO_WINDOW);
        heightSlider.setValue(heightSlider.getMax());
        heightSlider.setMinWidth(primaryStage.getWidth()*RATIO_CONTENT_TO_WINDOW);
        heightSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeWidth(heightSlider.getValue(),heightSlider.getMax());
        });
        Label width = new Label("Adjust Width");
        Slider widthSlider = new Slider();
        widthSlider.setOrientation(Orientation.VERTICAL);
        widthSlider.setRotate(180);
        widthSlider.setMin(CSVFile.getFixedCellSize());
        widthSlider.setMax(primaryStage.getHeight());
        widthSlider.setValue(widthSlider.getMax());
        widthSlider.setMinHeight(screenHeight*RATIO_CONTENT_TO_WINDOW);
        widthSlider.valueProperty().addListener((
                ObservableValue<? extends Number> ov, Number old_val,
                Number new_val) -> {
            CSVFile.changeHeight(widthSlider.getValue());
        });
        Label height = new Label("Adjust Height");
        height.setTranslateY(screenHeight*RATIO_CONTENT_TO_WINDOW/2);
        height.setRotate(90);
        var vBox=new VBox();
        vBox.getChildren().addAll(CSVFile.getTableView(),heightSlider,width);
        vBox.setMinWidth(CSVFile.getTableView().getMinWidth());
        vBox.setAlignment(Pos.CENTER);
        HBox hBox=new HBox();
        VBox secondVbox=new VBox();
        Label choice=new Label("Current File");
        choice.setFont(Font.font(16));
        secondVbox.getChildren().addAll(height,widthSlider,choice,cb);
        hBox.getChildren().addAll(vBox,secondVbox);
        root.getChildren().addAll(hBox);
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
    }


public static void main(String[] args) {
        launch(args);
    }

}

