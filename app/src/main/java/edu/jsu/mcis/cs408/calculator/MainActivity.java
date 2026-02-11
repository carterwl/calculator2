package edu.jsu.mcis.cs408.calculator;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import edu.jsu.mcis.cs408.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private CalculatorModel model;
    private CalculatorController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create Model
        model = new CalculatorModel();

        // Create Controller
        controller = new CalculatorController(model);

        // Attach shared click handler to every button in the layout (recursive)
        CalculatorClickHandler click = new CalculatorClickHandler();
        attachClickHandlers(binding.getRoot(), click);

        // Initialize display
        binding.display.setText("0");
    }

    private void attachClickHandlers(View root, View.OnClickListener click) {
        if (root instanceof Button) {
            root.setOnClickListener(click);
            return;
        }

        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                attachClickHandlers(group.getChildAt(i), click);
            }
        }
    }

    class CalculatorClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Object t = view.getTag();
            if (t == null) return;

            String tag = t.toString();
            String result = controller.handleInput(tag);
            binding.display.setText(result);
        }
    }
}
