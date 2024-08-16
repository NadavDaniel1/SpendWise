package com.example.spendwise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendwise.Adapters.TransactionAdapter;
import com.example.spendwise.Data.BudgetViewModel;
import com.example.spendwise.Fragments.BudgetFragment;
import com.example.spendwise.Model.Transaction;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private MaterialTextView main_user_details;
    private MaterialButton main_BTN_logout;
    private FloatingActionButton main_FAB_add;
    private RecyclerView main_recycle_view;
    private MaterialTextView main_LBL_price;
    private MaterialButton main_BTN_setBudget;

    MaterialTextView main_LBL_price_expense;
    private List<Transaction> transactionList;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private TransactionAdapter transactionAdapter;
    private BudgetViewModel budgetViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNavigationOfBackButton();

        findViews();
        initViews();
        onActivityResult();

    }

    private void setNavigationOfBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {
                            // Finish the activity and exit
                            MainActivity.this.finish();
                            System.exit(0);

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the UI state
        outState.putString("Budget", main_LBL_price.getText().toString());
        outState.putString("Expense", main_LBL_price_expense.getText().toString());
        int scrollPosition = ((GridLayoutManager) main_recycle_view.getLayoutManager())
                .findFirstVisibleItemPosition();
        outState.putInt("recycler_scroll_position", scrollPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the UI state
        main_LBL_price.setText(savedInstanceState.getString("budget"));
        main_LBL_price_expense.setText(savedInstanceState.getString("expense"));
        int scrollPosition = savedInstanceState.getInt("recycler_scroll_position");
        main_recycle_view.scrollToPosition(scrollPosition);
    }

    private void findViews() {
        main_user_details = findViewById(R.id.main_user_details);
        main_BTN_logout = findViewById(R.id.main_BTN_logout);
        main_FAB_add = findViewById(R.id.main_FAB_add);
        main_recycle_view = findViewById(R.id.main_recycle_view);
        main_LBL_price = findViewById(R.id.main_LBL_price);
        main_BTN_setBudget = findViewById(R.id.main_BTN_setBudget);
        main_LBL_price_expense = findViewById(R.id.main_LBL_price_expense);
    }

    private void initViews() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        setSignIn();
        // Ensures that budgetViewModel is correctly instantiated and tied to the activity's lifecycle,
        // Allowing it to persist during configuration changes.
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
        observeBudget();
        main_BTN_setBudget.setOnClickListener(v-> setUserBudget());
        setRecycleView();
        main_FAB_add.setOnClickListener(v -> transactToUploadActivity());
    }

    // Follow the changes in the budget and updates the prices whenever the budget changes.
    private void observeBudget() {
        budgetViewModel.getBudget()
                .observe(this, budget -> main_LBL_price.setText(String.valueOf(budget)));
        budgetViewModel.getExpense()
                .observe(this , expenses -> main_LBL_price_expense.setText(String.valueOf(expenses)));

    }

    private void setUserBudget() {
            hideMainViews();
            transactToBudgetFragment();
    }

    private void transactToBudgetFragment() {
        BudgetFragment budgetManagerFragment = new BudgetFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_FRAME_budget_manager, budgetManagerFragment)
                .addToBackStack(null) // This allows the user to go back
                .commit();
    }

    // Hide other views as necessary when move to BudgetFragment
    private void hideMainViews() {
        main_LBL_price.setVisibility(View.GONE);
        main_FAB_add.setVisibility(View.GONE);
        main_recycle_view.setVisibility(View.GONE);
        main_BTN_setBudget.setVisibility(View.GONE);
    }

    // Define the recycle view and add it transactions after they upload by the user.
    private void setRecycleView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        main_recycle_view.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        saveTransactionInFirebase(dialog);
        addTransactionToTransactionList(dialog);
    }

    private void addTransactionToTransactionList(AlertDialog dialog){
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(MainActivity.this, transactionList);
        main_recycle_view.setAdapter(transactionAdapter);
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addTransaction(dialog, snapshot);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });
    }

    private void addTransaction(AlertDialog dialog , DataSnapshot snapshot){
        transactionList.clear();
        for(DataSnapshot itemSnapshot: snapshot.getChildren()){
            Transaction transaction = itemSnapshot.getValue(Transaction.class);
            transactionList.add(transaction);
        }
        transactionAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    private void saveTransactionInFirebase(AlertDialog dialog){
        String userId = FirebaseAuth.getInstance().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("SpendWise/" + userId + "/Transactions");
        dialog.show();
    }

    private void transactToUploadActivity() {
        Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
        startActivity(intent);
        finish();
    }

    private void onActivityResult(){
        transactionAdapter.notifyDataSetChanged();
    }

    private void setSignIn(){
        if (user == null) {
            transactToLoginActivity();
        } else{
            main_user_details.setText(user.getEmail());
            main_user_details.setTextSize(20f);
            main_BTN_logout.setOnClickListener(v -> signOut());
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        transactToLoginActivity();
    }

    private void transactToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
}