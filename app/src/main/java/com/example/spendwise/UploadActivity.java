package com.example.spendwise;


import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.ViewModelProvider;
import com.example.spendwise.Data.BudgetViewModel;
import com.example.spendwise.Data.DataManager;
import com.example.spendwise.Model.Transaction;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class UploadActivity extends AppCompatActivity {

    private AppCompatImageView upload_IMG_upload;
    private AppCompatEditText upload_edittext_name;
    private AppCompatEditText upload_edittext_amount;
    private AppCompatEditText upload_edittext_date;
    private MaterialButton upload_BTN_save;
    private AppCompatImageView upload_BTN_close;
    private BudgetViewModel budgetViewModel;
    private String imageURL;
    private Uri uri;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        setNavigationOfBackButton();

        findViews();
        initViews();

    }

    private void setNavigationOfBackButton(){
        // Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to MainActivity
                Intent intent = new Intent(UploadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null) {
            dialog.dismiss();
        }
    }

    private void findViews() {
        upload_IMG_upload = findViewById(R.id.upload_IMG_upload);
        upload_edittext_name = findViewById(R.id.upload_edittext_name);
        upload_edittext_amount = findViewById(R.id.upload_edittext_amount);
        upload_edittext_date = findViewById(R.id.upload_edittext_date);
        upload_BTN_save = findViewById(R.id.upload_BTN_save);
        upload_BTN_close = findViewById(R.id.upload_BTN_close);
    }

    private void initViews() {
        upload_IMG_upload.setOnClickListener(v -> showIconSelectionDialog());
        upload_BTN_save.setOnClickListener(v -> saveData());
        upload_BTN_close.setOnClickListener(v -> transactToMainActivity());
        // Ensures that budgetViewModel is correctly instantiated and tied to the activity's lifecycle,
        // Allowing it to persist during configuration changes.
        budgetViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
    }

    private void updateTheBudget(double newAmount) {
        double remainingBudget = budgetViewModel.calculateRemainingBudget(newAmount);
        // Update the ViewModel's budget with the new value
        budgetViewModel.setMutableBudget(remainingBudget);
        budgetViewModel.setMutableExpense(newAmount);
    }

    private void transactToMainActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Save transaction details in Realtime Database of Firebase , and save the icon in FireBase-Storage
    private void saveData() {
        if(uri == null){
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("SpendWise/" + userId + "/Transactions")
                .child(Objects.requireNonNull(uri.getLastPathSegment()));
        Log.d("Uri Get Last Path Segment", "uri.getLastPathSegment() = " + uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();
        Log.d("Save Data", "dialog finish");
        // Add a reference to the progress dialog's TextView
        MaterialTextView dialogMessage = dialog.findViewById(R.id.progress_message);
        Log.d("Set Image URI", "URI: " + uri.toString());
        uploadingImageToFirebaseStorage(storageReference, dialogMessage);
        transactToMainActivity();
    }

    private void  uploadingImageToFirebaseStorage(StorageReference sr, MaterialTextView dialogMessage){
        sr.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

            uriTask.addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    tryUploadAfterTaskSuccess(dialogMessage, uriTask);
                }
                else {
                    transactToMainActivity();
                }
            });
        }).addOnFailureListener(e ->
        { // upload dialog message on failure task
            dialogMessage.setText("Upload failed! Please try again.");
            dialog.setCancelable(true);// Allow the user to dismiss the dialog manually
        });
    }

    private void tryUploadAfterTaskSuccess(MaterialTextView dialogMessage,Task<Uri> uriTask){
        Uri urlImage = uriTask.getResult();
        imageURL = urlImage.toString();
        Log.d("Starting Upload Data", "Upload Data");
        CompletableFuture<Void> future = uploadData();
        Log.d("Upload Data", "Upload Data");
        future.whenComplete((a, throwable) -> runOnUiThread(() -> {
            if (dialog.isShowing()) {
                if(throwable == null) {
                    dialogMessage.setText("Upload successful");
                }
                else {
                    dialogMessage.setText(throwable.getMessage());
                }
                new Handler().postDelayed(dialog::dismiss, 1000);
            }
        }));
    }

    // Upload data to Realtime Database and update the budget.
    public  CompletableFuture<Void> uploadData(){
        CompletableFuture<Void> future = new CompletableFuture<>();
        String name = upload_edittext_name.getText().toString();
        String amount = upload_edittext_amount.getText().toString();
        String date = upload_edittext_date.getText().toString();
        if(name.isEmpty() || amount.isEmpty()   || date.isEmpty()){
            Toast.makeText(UploadActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            future.completeExceptionally(new IllegalArgumentException("Please fill in all fields"));
        }
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Transaction transaction = new Transaction(name, amount, date, imageURL);
        updateTheBudget(Double.parseDouble(amount));
        String userId = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("SpendWise/" + userId + "/Transactions").child(currentDate)
                .setValue(transaction).addOnCompleteListener(task-> {
                    if (task.isSuccessful()){
                        future.complete(null);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }

    //Creates a custom matrix for icons used by the user to select an image for the transaction
    private void showIconSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        GridView gridView = new GridView(this);
        gridView.setNumColumns(4);
        gridView.setVerticalSpacing(50);
        gridView.setHorizontalSpacing(50);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>
                (this, android.R.layout.simple_list_item_1, DataManager.getIconList()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                AppCompatImageView imageView = new AppCompatImageView(getContext());
                imageView.setImageResource(getItem(position));
                imageView.setPadding(50, 50, 50, 50);
                imageView.setAdjustViewBounds(true);
                return imageView;
            }
        };
        createIconDialog(gridView,builder,adapter);
    }

    private void createIconDialog(GridView gridView,AlertDialog.Builder builder, ArrayAdapter<Integer> adapter){
        gridView.setAdapter(adapter);
        builder.setView(gridView);
        AlertDialog iconDialog = builder.create();
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedIcon = DataManager.getIconList().get(position);
            upload_IMG_upload.setImageResource(selectedIcon);
            uri = Uri.parse("android.resource://" + getPackageName() + "/" + selectedIcon);
            if(uri == null) {
                uri = getDefaultImageUri(); // Handle icon as default image
            }
            iconDialog.dismiss();
        });
        iconDialog.show();
    }

    private Uri getDefaultImageUri() {
       return Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.credit_card);
    }
}



