package com.cinntra.ledger.adapters;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cinntra.ledger.R;
import com.cinntra.ledger.fragments.Inventory_Detail_Fragment;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.model.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class EnventoryItemsAdapter extends RecyclerView.Adapter<EnventoryItemsAdapter.ViewHolder> {
    private String SelectedTaxSlab = "";
    private int currentItemPo = 0;
    Context context;
    ArrayList<Inventory> InventoryList;

    public EnventoryItemsAdapter(Context context, ArrayList<Inventory> InventoryList)
       {
    this.context      = context;
    this.InventoryList = InventoryList;
    this.tempList  = new ArrayList<Inventory>();
    this.tempList.addAll(InventoryList);

      }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.inventory_item_new,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
      {
   final Inventory obj=getItem(position);
   holder.customerName.setText(obj.getItemName());
          holder.cardNumber.setText(obj.getItemCode());
          holder.price.setText("List Price : " + Globals.changeDecemal(obj.getPrice()));
          holder.quantity.setText(""+Globals.changeDecemal(obj.getQuantity()));

          if(Integer.parseInt(Globals.changeDecemal(obj.getQuantity())) > 0){
              holder.out_of_stock.setText("In Stock");
              holder.out_of_stock.setTextColor(ContextCompat.getColor(context,R.color.green));
          }else{
              holder.out_of_stock.setText("Out of Stock");
              holder.out_of_stock.setTextColor(ContextCompat.getColor(context,R.color.red));
          }

      }

    @Override
    public int getItemCount() {
    return InventoryList.size();
    }
    public Inventory getItem(int position)
      {
   return InventoryList.get(position);
      }




    class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName,cardNumber,price,quantity,out_of_stock;
        public ViewHolder(@NonNull View itemView) {
        super(itemView);
        customerName = itemView.findViewById(R.id.itemName);
        cardNumber   = itemView.findViewById(R.id.cardcode);
        price        = itemView.findViewById(R.id.price);
        quantity = itemView.findViewById(R.id.code);
        out_of_stock = itemView.findViewById(R.id.stock_manage);
        itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
         currentItemPo = getAdapterPosition();


             Bundle b = new Bundle();
             b.putSerializable("inventory_data",InventoryList.get(getAdapterPosition()));
             FragmentActivity activity = (FragmentActivity)(context);
             FragmentManager fm = activity.getSupportFragmentManager();
             Inventory_Detail_Fragment alertDialog = new Inventory_Detail_Fragment();
             alertDialog.setArguments(b);
             alertDialog.show(fm, "fragment_alert");

            /*
          ItemsList.get(currentItemPo).setItemUnitPrice(ItemsList.get(currentItemPo).getItemPrices().get(CreateContact.PriceListNum-1).getPrice());
          ItemsList.get(currentItemPo).setItemTaxCode(SelectedTaxSlab);
           */


              }
          });

        }
    }

    List<Inventory> tempList =null ;
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        InventoryList.clear();
        if (charText.length() == 0) {
            InventoryList.addAll(tempList);
        } else {
            for (Inventory st : tempList) {
                if(st.getItemName()!=null&&!st.getItemName().isEmpty()) {
                    if (st.getItemName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        InventoryList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }
    public void AllData()
    {
        InventoryList.clear();
        InventoryList.addAll(tempList);
        notifyDataSetChanged();
    }


    public void Alphabetical() {
        InventoryList.clear();
        InventoryList.addAll(tempList);
        Collections.sort(InventoryList, new Comparator<Inventory>() {
            @Override
            public int compare(Inventory o1, Inventory o2) {
                return o1.getItemName().compareTo(o2.getItemName());
            }
        });

        notifyDataSetChanged();
    }


}
