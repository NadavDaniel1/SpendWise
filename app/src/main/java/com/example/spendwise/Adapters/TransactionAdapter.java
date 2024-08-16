package com.example.spendwise.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spendwise.DetailActivity;
import com.example.spendwise.Model.Transaction;
import com.example.spendwise.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item , parent , false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Glide.with(context).load(transactionList.get(position).getImage()).into(holder.recImage);
        holder.recName.setText(transactionList.get(position).getName());
        holder.recAmount.setText(transactionList.get(position).getAmount());
        holder.recDate.setText(transactionList.get(position).getDate());

        holder.recCard.setOnClickListener(v -> transactToDetailActivity(holder));

    }

    private void transactToDetailActivity(@NonNull TransactionViewHolder holder) {
        Intent intent = new Intent(context , DetailActivity.class);
        intent.putExtra("Image" , transactionList.get(holder.getAdapterPosition()).getImage());
        intent.putExtra("Date" , transactionList.get(holder.getAdapterPosition()).getDate());
        intent.putExtra("Category" , transactionList.get(holder.getAdapterPosition()).getName());
        intent.putExtra("Amount" , transactionList.get(holder.getAdapterPosition()).getAmount());

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}

class TransactionViewHolder extends RecyclerView.ViewHolder{

    AppCompatImageView recImage;
    MaterialTextView recName , recAmount , recDate;
    CardView recCard;

    public TransactionViewHolder(@NonNull View itemView) {
        super(itemView);

        findViews(itemView);

    }

    private void findViews(View v) {

        recImage = v.findViewById(R.id.recImage);
        recName = v.findViewById(R.id.recName);
        recAmount = v.findViewById(R.id.recAmount);
        recDate = v.findViewById(R.id.recDate);
        recCard = v.findViewById(R.id.recCard);

    }
}