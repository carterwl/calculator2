package edu.jsu.mcis.cs408.calculator;

public class CalculatorModel {

    /* =========================
       Calculator State
       ========================= */

    private String display = "0";

    private double storedValue = 0.0;      // A
    private String pendingOp = null;       // "+", "-", "*", "/" (or null)
    private boolean startNewNumber = true; // next digit begins a new entry
    private boolean error = false;

    public String getDisplay() {
        return error ? "Error" : display;
    }

    /* =========================
       Public Entry Point
       ========================= */

    public void handle(String tag) {

        // After an error, only Clear works
        if (error) {
            if ("btnClear".equals(tag)) clearAll();
            return;
        }

        // Digits: btn0..btn9
        if (isDigitTag(tag)) {
            inputDigit(tag.charAt(3)); // '0'..'9'
            return;
        }

        switch (tag) {

            case "btnDot":
                inputDot();
                break;

            case "btnClear":
                clearAll();
                break;

            case "btnPlus":
                setBinaryOp("+");
                break;

            case "btnMinus":
                setBinaryOp("-");
                break;

            case "btnMultiply":
                setBinaryOp("*");
                break;

            case "btnDivide":
                setBinaryOp("/");
                break;

            case "btnEquals":
                equals();
                break;

            case "btnSqrt":
                sqrt();
                break;

            case "btnSign":
                negate();
                break;

            case "btnPercent":
                percent();
                break;

            default:
                // Unknown tag: ignore safely
                break;
        }
    }

    /* =========================
       Digits / Decimal
       ========================= */

    private void inputDigit(char d) {

        if (startNewNumber) {
            display = String.valueOf(d);
            startNewNumber = false;
            return;
        }

        // Prevent leading zeros like "0002"
        if ("0".equals(display)) {
            display = String.valueOf(d);
        } else {
            display += d;
        }
    }

    private void inputDot() {

        if (startNewNumber) {
            display = "0.";
            startNewNumber = false;
            return;
        }

        if (!display.contains(".")) {
            display += ".";
        }
    }

    /* =========================
       Binary Operators + - * /
       ========================= */

    private void setBinaryOp(String op) {

        double current = parseDisplay();

        // First operator pressed: store A
        if (pendingOp == null) {
            storedValue = current;
            pendingOp = op;
            startNewNumber = true;
            return;
        }

        // If user presses operator again without typing B, just change operator
        if (startNewNumber) {
            pendingOp = op;
            return;
        }

        // Chain: compute A (pendingOp) B
        double result = applyBinary(storedValue, current, pendingOp);
        if (error) return;

        storedValue = result;
        display = format(result);

        pendingOp = op;
        startNewNumber = true;
    }

    private void equals() {

        if (pendingOp == null) return;

        // If "=" pressed right after operator (no B typed), do nothing
        if (startNewNumber) return;

        double b = parseDisplay();

        double result = applyBinary(storedValue, b, pendingOp);
        if (error) return;

        display = format(result);

        // Reset for next calculation
        storedValue = result;
        pendingOp = null;
        startNewNumber = true;
    }

    private double applyBinary(double a, double b, String op) {

        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0.0) {
                    setError();
                    return 0.0;
                }
                return a / b;
            default:
                setError();
                return 0.0;
        }
    }

    /* =========================
       Unary Operators √  ±  %
       ========================= */

    private void sqrt() {

        double x = parseDisplay();

        if (x < 0) {
            setError();
            return;
        }

        double r = Math.sqrt(x);
        display = format(r);
        startNewNumber = true;
    }

    private void negate() {

        // Toggle sign on display text (keeps decimals as typed)
        if ("0".equals(display) || "0.".equals(display)) return;

        if (display.startsWith("-")) display = display.substring(1);
        else display = "-" + display;
    }

    /*
      Percent behavior (common desktop style):
      - If no pending op: x% = x/100
      - If pending op exists:
          + or - : treat B% as A*(B/100)
          * or / : treat B% as (B/100)
    */
    private void percent() {

        double x = parseDisplay();

        if (pendingOp == null) {
            display = format(x / 100.0);
            startNewNumber = true;
            return;
        }

        // If user hasn't typed B yet, treat B as 0
        if (startNewNumber) x = 0.0;

        double b;
        switch (pendingOp) {
            case "+":
            case "-":
                b = storedValue * (x / 100.0);
                break;
            case "*":
            case "/":
            default:
                b = x / 100.0;
                break;
        }

        display = format(b);
        startNewNumber = true;
    }

    /* =========================
       Helpers
       ========================= */

    private boolean isDigitTag(String tag) {
        // Exactly "btn0".."btn9"
        return tag != null
                && tag.length() == 4
                && tag.startsWith("btn")
                && Character.isDigit(tag.charAt(3));
    }

    private double parseDisplay() {
        try {
            return Double.parseDouble(display);
        } catch (Exception e) {
            setError();
            return 0.0;
        }
    }

    private String format(double v) {

        if (Double.isNaN(v) || Double.isInfinite(v)) {
            setError();
            return "Error";
        }

        // If it's basically an integer, show without .0
        long iv = (long) v;
        if (v == iv) return String.valueOf(iv);

        // Default string, optionally trim if you want
        String s = String.valueOf(v);

        // Optional: limit length so it doesn't overflow the display
        if (s.length() > 14) s = s.substring(0, 14);

        return s;
    }

    private void clearAll() {
        display = "0";
        storedValue = 0.0;
        pendingOp = null;
        startNewNumber = true;
        error = false;
    }

    private void setError() {
        error = true;
        display = "Error";
        storedValue = 0.0;
        pendingOp = null;
        startNewNumber = true;
    }
}
