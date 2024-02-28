package com.cinntra.ledger.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.ledger.R;
import com.cinntra.ledger.activities.CustomersWiseLedger;

public class ZonesAdapter extends RecyclerView.Adapter <ZonesAdapter.ContactViewHolder>{


    Context context;


    String zones[];
    public ZonesAdapter(Context context1, String zones[]){
        this.zones=zones;
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

     holder.title.setText(zones[position]);


    }





    @Override
    public int getItemCount() {
     return zones.length;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView unit_price,title;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            title= itemView.findViewById( R.id.title);
            unit_price= itemView.findViewById( R.id.unit_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
             public void onClick(View v) {
               String currentZone = zones[getAdapterPosition()];
               if(getAdapterPosition()==0)
                   currentZone = "";
              Intent i = new Intent(context, CustomersWiseLedger.class);
              i.putExtra("Zones",currentZone);
              context.startActivity(i);

                }
            });


        }

    }
}
