package edu.jsu.mcis.cs408.calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private static final int KEYS_HEIGHT = 4; // rows
    private static final int KEYS_WIDTH  = 5; // columns

    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.rootLayout);

        initLayout();
    }

    private void initLayout() {

        // Guideline IDs (must exist in activity_main.xml)
        final int guideNorth = R.id.guideNorth;
        final int guideSouth = R.id.guideSouth;
        final int guideWest  = R.id.guideWest;
        final int guideEast  = R.id.guideEast;

        // Button text string resources (must exist in strings.xml)
        int[] keyTextRes = new int[] {
                // Row 1
                R.string.btn7, R.string.btn8, R.string.btn9, R.string.btnSqrt, R.string.btnClear,
                // Row 2
                R.string.btn4, R.string.btn5, R.string.btn6, R.string.btnDivide, R.string.btnPercent,
                // Row 3
                R.string.btn1, R.string.btn2, R.string.btn3, R.string.btnMultiply, R.string.btnMinus,
                // Row 4
                R.string.btnSign, R.string.btn0, R.string.btnDot, R.string.btnPlus, R.string.btnEquals
        };

        // Tags (used later for event handling)
        String[] keyTags = new String[] {
                "btn7","btn8","btn9","btnSqrt","btnClear",
                "btn4","btn5","btn6","btnDivide","btnPercent",
                "btn1","btn2","btn3","btnMultiply","btnMinus",
                "btnSign","btn0","btnDot","btnPlus","btnEquals"
        };


        TextView display = new TextView(this);
        int displayId = View.generateViewId();
        display.setId(displayId);
        display.setText(getString(R.string.txtDisplay));
        display.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        display.setPadding(dp(8), dp(8), dp(8), dp(8));
        display.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48);

        display.setLayoutParams(new ConstraintLayout.LayoutParams(
                0,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        ));

        rootLayout.addView(display);

        int[][] horizontals = new int[KEYS_HEIGHT][KEYS_WIDTH];
        int[][] verticals   = new int[KEYS_WIDTH][KEYS_HEIGHT];

        int index = 0;
        for (int r = 0; r < KEYS_HEIGHT; r++) {
            for (int c = 0; c < KEYS_WIDTH; c++) {

                MaterialButton b = new MaterialButton(this);
                int id = View.generateViewId();
                b.setId(id);

                b.setText(getString(keyTextRes[index]));
                b.setTag(keyTags[index]);
                b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                b.setCornerRadius(0);


                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(0, 0);
                b.setLayoutParams(lp);

                rootLayout.addView(b);

                horizontals[r][c] = id;
                verticals[c][r]   = id;

                index++;
            }
        }


        ConstraintSet cs = new ConstraintSet();
        cs.clone(rootLayout);

        // Display constraints (between west/east guidelines, at top guideline)
        cs.connect(displayId, ConstraintSet.TOP, guideNorth, ConstraintSet.TOP);
        cs.connect(displayId, ConstraintSet.START, guideWest, ConstraintSet.START);
        cs.connect(displayId, ConstraintSet.END, guideEast, ConstraintSet.END);
        cs.constrainWidth(displayId, 0);
        cs.constrainHeight(displayId, ConstraintSet.WRAP_CONTENT);


        for (int r = 0; r < KEYS_HEIGHT; r++) {
            for (int c = 0; c < KEYS_WIDTH; c++) {
                int id = horizontals[r][c];
                cs.constrainWidth(id, 0);
                cs.constrainHeight(id, 0);
                cs.setMargin(id, ConstraintSet.START, dp(8));
                cs.setMargin(id, ConstraintSet.END, dp(8));
                cs.setMargin(id, ConstraintSet.TOP, dp(8));
                cs.setMargin(id, ConstraintSet.BOTTOM, dp(8));
            }
        }


        for (int r = 0; r < KEYS_HEIGHT; r++) {
            cs.createHorizontalChain(
                    guideWest, ConstraintSet.RIGHT,
                    guideEast, ConstraintSet.LEFT,
                    horizontals[r],
                    null,
                    ConstraintSet.CHAIN_SPREAD
            );
        }


        for (int c = 0; c < KEYS_WIDTH; c++) {
            cs.createVerticalChain(
                    displayId, ConstraintSet.BOTTOM,
                    guideSouth, ConstraintSet.TOP,
                    verticals[c],
                    null,
                    ConstraintSet.CHAIN_SPREAD
            );
        }

        cs.applyTo(rootLayout);
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}
