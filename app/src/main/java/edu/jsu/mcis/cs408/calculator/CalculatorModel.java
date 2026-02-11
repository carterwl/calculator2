package edu.jsu.mcis.cs408.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorModel {

    // ----- State Machine -----
    public enum CalculatorState {
        CLEAR, LHS, OP_SCHEDULED, RHS, RESULT, ERROR
    }

    public enum Operator {
        NONE, ADD, SUB, MUL, DIV
    }

    // ----- Display limits -----
    private static final int DISPLAY_MAX = 14;
    private static final int DIV_SCALE = 10;

    // ----- Core Data -----
    private CalculatorState state = CalculatorState.CLEAR;

    private BigDecimal lhs = BigDecimal.ZERO;
    private BigDecimal rhs = BigDecimal.ZERO;
    private Operator op = Operator.NONE;

    private StringBuilder entry = new StringBuilder("0");
    private String display = "0";

    // ----- Public API -----

    public void input(String tag) {

        if (state == CalculatorState.ERROR && !tag.equals("C")) {
            return;
        }

        if (isDigit(tag) || tag.equals(".")) {
            handleDigit(tag);
        }
        else if (isBinaryOp(tag)) {
            handleOperator(tag);
        }
        else if (tag.equals("=")) {
            handleEquals();
        }
        else if (tag.equals("C")) {
            clearAll();
        }
        else if (tag.equals("±")) {
            negate();
        }
        else if (tag.equals("√")) {
            squareRoot();
        }
        else if (tag.equals("%")) {
            percent();
        }
    }

    public String getDisplayText() {
        return display;
    }

    // ----- Digit Handling -----

    private void handleDigit(String tag) {

        if (state == CalculatorState.RESULT) {
            clearAll();
        }

        if (state == CalculatorState.OP_SCHEDULED) {
            entry.setLength(0);
            state = CalculatorState.RHS;
        }

        if (entry.length() >= DISPLAY_MAX) return;

        if (tag.equals(".")) {
            if (entry.indexOf(".") != -1) return;
            if (entry.length() == 0) entry.append("0");
        }

        if (entry.toString().equals("0") && !tag.equals(".")) {
            entry.setLength(0);
        }

        entry.append(tag);
        display = entry.toString();

        BigDecimal value = new BigDecimal(display);

        if (state == CalculatorState.RHS) {
            rhs = value;
        } else {
            lhs = value;
            state = CalculatorState.LHS;
        }
    }

    // ----- Operator Handling -----

    private void handleOperator(String tag) {

        Operator newOp = toOperator(tag);

        if (state == CalculatorState.RHS) {
            compute();
        }

        op = newOp;
        state = CalculatorState.OP_SCHEDULED;
    }

    private void handleEquals() {

        if (state == CalculatorState.RHS) {
            compute();
            state = CalculatorState.RESULT;
        }
    }

    private void compute() {

        try {
            switch (op) {
                case ADD:
                    lhs = lhs.add(rhs);
                    break;
                case SUB:
                    lhs = lhs.subtract(rhs);
                    break;
                case MUL:
                    lhs = lhs.multiply(rhs);
                    break;
                case DIV:
                    if (rhs.compareTo(BigDecimal.ZERO) == 0) {
                        setError();
                        return;
                    }
                    lhs = lhs.divide(rhs, DIV_SCALE, RoundingMode.HALF_UP);
                    break;
                default:
                    return;
            }

            display = format(lhs);
            entry = new StringBuilder(display);
            rhs = BigDecimal.ZERO;
            op = Operator.NONE;

        } catch (Exception e) {
            setError();
        }
    }

    // ----- Unary Operators -----

    private void negate() {
        BigDecimal value = new BigDecimal(display).negate();
        display = format(value);
        entry = new StringBuilder(display);

        if (state == CalculatorState.RHS) {
            rhs = value;
        } else {
            lhs = value;
        }
    }

    private void squareRoot() {

        BigDecimal value = new BigDecimal(display);

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            setError();
            return;
        }

        double sqrt = Math.sqrt(value.doubleValue());
        value = new BigDecimal(sqrt);

        display = format(value);
        entry = new StringBuilder(display);

        if (state == CalculatorState.RHS) {
            rhs = value;
        } else {
            lhs = value;
        }
    }

    private void percent() {

        if (op == Operator.NONE) return;

        rhs = lhs.multiply(rhs)
                .divide(new BigDecimal("100"), DIV_SCALE, RoundingMode.HALF_UP);

        display = format(rhs);
        entry = new StringBuilder(display);
    }

    // ----- Helpers -----

    private void clearAll() {
        state = CalculatorState.CLEAR;
        lhs = BigDecimal.ZERO;
        rhs = BigDecimal.ZERO;
        op = Operator.NONE;
        entry = new StringBuilder("0");
        display = "0";
    }

    private void setError() {
        state = CalculatorState.ERROR;
        display = "Error";
        lhs = BigDecimal.ZERO;
        rhs = BigDecimal.ZERO;
        op = Operator.NONE;
    }

    private boolean isDigit(String s) {
        return s.matches("[0-9]");
    }

    private boolean isBinaryOp(String s) {
        return s.equals("+") || s.equals("-")
                || s.equals("×") || s.equals("*")
                || s.equals("÷") || s.equals("/");
    }

    private Operator toOperator(String s) {
        switch (s) {
            case "+": return Operator.ADD;
            case "-": return Operator.SUB;
            case "×":
            case "*": return Operator.MUL;
            case "÷":
            case "/": return Operator.DIV;
            default: return Operator.NONE;
        }
    }

    private String format(BigDecimal value) {

        value = value.stripTrailingZeros();
        String s = value.toPlainString();

        if (s.length() > DISPLAY_MAX) {
            s = s.substring(0, DISPLAY_MAX);
        }

        return s;
    }
}
