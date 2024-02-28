package com.cinntra.ledger.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.ledger.R;
import com.cinntra.ledger.activities.InvoiceTransactionFullInfo;
import com.cinntra.ledger.model.Customers_Report;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter <BillAdapter.ContactViewHolder>{


    Context context;


    ArrayList<Customers_Report> branchList = new ArrayList<>();
    public BillAdapter(Context context1, ArrayList<Customers_Report> branchList){
        this.branchList.addAll(branchList);
        this.context = context1;

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.customer_report_adapter,parent,false);
        return new ContactViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

     holder.title.setText(branchList.get(position).getCustomerName());
     holder.unit_price.setText(branchList.get(position).getPrice());

    }





    @Override
    public int getItemCount() {
     return branchList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView unit_price,title;
        ImageView edit;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById( R.id.title);
            unit_price= itemView.findViewById( R.id.unit_price);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(context, InvoiceTransactionFullInfo.class));


                }
            });


        }

    }
}
