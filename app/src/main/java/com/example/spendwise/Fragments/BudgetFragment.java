package com.example.spendwise.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spendwise.Data.BudgetViewModel;
import com.example.spendwise.Data.DataManager;
import com.example.spendwise.MainActivity;
import com.example.spendwise.Model.Transaction;
import com.example.spendwise.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class BudgetFragment extends Fragment {
    FloatingActionButton budgetManager_FAB_close;
    AppCompatEditText budgetManager_EDIT_budget;
    MaterialButton budgetManager_BTN_save;
    private BudgetViewModel budgetViewModel;
    private Integer budget = 0;

    public BudgetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_budget, container, false);

       findViews(v);
       initViews();

        return v;
    }


    private void findViews(View v) {
        budgetManager_FAB_close = v.findViewById(R.id.budgetManager_FAB_close);
        budgetManager_EDIT_budget = v.findViewById(R.id.budgetManager_EDIT_budget);
        budgetManager_BTN_save = v.findViewById(R.id.budgetManager_BTN_save);
    }

    private void initViews() {
        budgetManager_FAB_close.setOnClickListener(v -> transactToMainActivity());
        budgetManager_BTN_save.setOnClickListener(v -> saveBudget());
        // Ensures that budgetViewModel is correctly instantiated and tied to the activity's lifecycle,
        // Allowing it to persist during configuration changes.
        budgetViewModel = new ViewModelProvider(requireActivity()).get(BudgetViewModel.class);
    }

    private void saveBudget() {
        String userBudget = budgetManager_EDIT_budget.getText().toString();
        if(!userBudget.isEmpty()){
            budgetViewModel.setMutableBudget(Double.parseDouble(userBudget));
        }
        transactToMainActivity();
    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}