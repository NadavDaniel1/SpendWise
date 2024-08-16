package com.example.spendwise.Data;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// This class using a state holder for budget and expenses that follow it's changes and update the UI
public class BudgetViewModel extends ViewModel {
    //Data holder class that can be observed within a given lifecycle (like activities or fragments).
    // The MutableLiveData class is used to store data that can be modified, which in this case is the mutableBudget.
    private MutableLiveData<Double> mutableBudget = new MutableLiveData<>();
    //This is a wrapper around MutableLiveData that is immutable.
    // It is used to expose mutableBudget to the rest of the application so that it can be observed, but not directly modified.
    // Only the ViewModel can modify the mutableBudget.
    private LiveData<Double> budget = mutableBudget;
    private MutableLiveData<Double> mutableExpense = new MutableLiveData<>();
    private LiveData<Double> expense = mutableExpense;

    public BudgetViewModel(){
        loadBudgetFromFireBase();
        loadExpensesFromFireBase();
    }

    // Load budget value from Realtime Database Firebase (if the value exist, otherwise set the value to 0.0)
    private void loadBudgetFromFireBase() {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference budgetRef = FirebaseDatabase.getInstance()
                .getReference("SpendWise/" + userId).child("Budget");

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && snapshot.getValue() != "") {
                    Double savedBudget = snapshot.getValue(Double.class);
                    mutableBudget.setValue(savedBudget);
                }
                else{
                    mutableBudget.setValue(0.0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetViewModel.Class:FirebaseError", "Failed to load data", error.toException());
            }
        });
    }

    // Load expense value from Realtime Database Firebase (if the value exist, otherwise set the value to 0.0)
    private void loadExpensesFromFireBase() {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference expensesRef = FirebaseDatabase.getInstance()
                .getReference("SpendWise/" + userId).child("Expenses");

        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null && snapshot.getValue() != "") {
                    Double savedExpenses = snapshot.getValue(Double.class);
                    mutableExpense.setValue(savedExpenses);
                }
                else{
                    mutableExpense.setValue(0.0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BudgetViewModel.Class:FirebaseError", "Failed to load data", error.toException());
            }
        });
    }

    public LiveData<Double> getBudget() {
        return budget;
    }

    public LiveData<Double> getExpense() {
        return expense;
    }

    // Updating mutableExpense value after each transaction that added, and save it in Firebase
    public void setMutableExpense(Double newExpense) {
        Double currentExpense = expense.getValue();
        if (currentExpense == null)
            currentExpense = 0.0;
        currentExpense += newExpense;
        mutableExpense.setValue(currentExpense);
        //Update the expenses in the Firebase
        String userId = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("SpendWise/" + userId)
                .child("Expenses").setValue(currentExpense);
    }

    // Update mutableBudget each time that the user chooses to set new budget, and save it in Firebase
    public void setMutableBudget(Double newBudget) {
        mutableBudget.setValue(newBudget);
        //Update theBudget in the Firebase
        String userId = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("SpendWise/" + userId)
                .child("Budget").setValue(newBudget);
    }

    // update the budget after each expense.
    public Double calculateRemainingBudget(Double totalExpenses){
        Double currentBudget = budget.getValue();
        if(currentBudget == null){
            currentBudget = 0.0;
        }
        return currentBudget - totalExpenses;
    }
}
