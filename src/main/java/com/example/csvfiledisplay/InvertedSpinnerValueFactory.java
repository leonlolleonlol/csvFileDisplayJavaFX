package com.example.csvfiledisplay;
import javafx.scene.control.SpinnerValueFactory;

class InvertedSpinnerValueFactory extends SpinnerValueFactory.IntegerSpinnerValueFactory {
    public InvertedSpinnerValueFactory(int min, int max, int initialValue, int amountToStepBy) {
        super(min, max, initialValue, amountToStepBy);
    }

    @Override
    public void decrement(int steps) {
        if (steps < CSVFile.getDataList().size())
            super.increment(steps); // Invert the decrement operation
    }

    @Override
    public void increment(int steps) {
        super.decrement(steps); // Invert the increment operation
    }
}