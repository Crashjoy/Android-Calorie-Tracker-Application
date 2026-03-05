package com.example.aldiyarbaibogurov_test2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class FoodItemDialogFragment extends DialogFragment {

    public interface FoodItemListener {
        void onFoodItemCreated(FoodItem item);
    }

    private FoodItemListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FoodItemListener) {
            listener = (FoodItemListener) context;
        } else if (getParentFragment() instanceof FoodItemListener) {
            listener = (FoodItemListener) getParentFragment();
        } else {
            throw new RuntimeException("Must implement FoodItemListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_food_item_dialog_fragment, null);

        EditText etName = view.findViewById(R.id.etDialogFoodName);
        EditText etQty = view.findViewById(R.id.etDialogQuantity);
        EditText etCal = view.findViewById(R.id.etDialogCalories);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle("Add Food Item")
                .setPositiveButton("Add", (d, which) -> {
                    String name = etName.getText().toString().trim();
                    int qty = parseIntSafe(etQty.getText().toString().trim());
                    int cal = parseIntSafe(etCal.getText().toString().trim());
                    if (!name.isEmpty() && qty > 0 && cal >= 0) {
                        FoodItem item = new FoodItem(name, qty, cal);
                        if (listener != null) listener.onFoodItemCreated(item);
                    }
                })
                .setNegativeButton("Cancel", (d, which) -> d.dismiss());

        return builder.create();
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
