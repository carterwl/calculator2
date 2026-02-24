package edu.jsu.mcis.cs408.calculator;

public class CalculatorController {

    private final CalculatorModel model;

    public CalculatorController(CalculatorModel model) {
        this.model = model;
    }

    public String handleInput(String tag) {
        model.input(tag);
        return model.getDisplayText();
    }
}
