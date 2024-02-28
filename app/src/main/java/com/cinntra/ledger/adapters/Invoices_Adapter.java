package com.cinntra.ledger.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.cinntra.ledger.R;
import com.cinntra.ledger.activities.WebViewToPdf;
import com.cinntra.ledger.model.InvoiceNewData;
import com.cinntra.ledger.globals.Globals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class Invoices_Adapter extends RecyclerView.Adapter<Invoices_Adapter.ViewHolder> {
    Context context;
    List<InvoiceNewData> arrayList;
    public Invoices_Adapter(Context context, List<InvoiceNewData> arrayList)
     {
    this.context   = context;
    this.arrayList = arrayList;
    this.tempList  = new ArrayList<InvoiceNewData>();
    this.tempList.addAll(arrayList);
      }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
      {
    View rootView = LayoutInflater.from(context).inflate(R.layout.invoice_new_screen,parent,false);
    return new ViewHolder(rootView);
     }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
      {
          InvoiceNewData obj = getItem(position);
     holder.title.setText(obj.getCardName());
     holder.doc_date.setText(obj.getDocDate());
     holder.amount.setText(Globals.getAmmount(obj.getDocCurrency(),obj.getDocTotal()));
      holder.status.setText(Globals.viewStatus(obj.getDocumentStatus()));

          holder.amount.setText(Globals.getAmmount(obj.getDocCurrency(),obj.getDocTotal()));
          if(Globals.viewStatus(obj.getDocumentStatus()) == "Open"){
              holder.status.setText("Unpaid");
              holder.status.setBackgroundResource(R.drawable.saffron_rounded);
          }else{
              holder.status.setText("Paid");
              holder.status.setBackgroundResource(R.drawable.openroundedgreen);
          }

      }

    @Override
    public int getItemCount()
     {
    return arrayList.size();
     }
     public InvoiceNewData getItem(int position)
     {
   return arrayList.get(position);
     }




    class  ViewHolder extends RecyclerView.ViewHolder {
        TextView title,doc_date,amount,status;
        ImageView preview_file;
     public ViewHolder(@NonNull View itemView)
           {

      super(itemView);
      title    = itemView.findViewById(R.id.title);
      doc_date = itemView.findViewById(R.id.doc_date);
      amount   = itemView.findViewById(R.id.amount);
      status   = itemView.findViewById(R.id.status);
      preview_file   = itemView.findViewById(R.id.preview_file);

      itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
           /*   Log.e("Test=>","Click");
              Bundle b = new Bundle();
              b.putSerializable(Globals.QuotationItem,arrayList.get(getAdapterPosition()));
             // Quotation_Update_Fragment fragment = new Quotation_Update_Fragment();
              Invoice_Detail_Fragment fragment = new Invoice_Detail_Fragment();
              fragment.setArguments(b);
              FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
              transaction.replace(R.id.main_container, fragment);
              transaction.addToBackStack("Test");
              transaction.commit();*/
              Intent i = new Intent(context, WebViewToPdf.class);
              i.putExtra("PDfFrom","Invoice");
              i.putExtra("PdfID",arrayList.get(getAdapterPosition()).getId());
              context.startActivity(i);
          }
      });
               preview_file.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                      /* ShowDocument fragment = new ShowDocument();
                       FragmentTransaction transaction =  ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                       transaction.replace(R.id.main_container, fragment);
                       transaction.addToBackStack(null);
                       transaction.commit();*/
                       Intent i = new Intent(context, WebViewToPdf.class);
                       i.putExtra("PDfFrom","Invoice");
                       i.putExtra("PdfID",arrayList.get(getAdapterPosition()).getId());
                       context.startActivity(i);
                   }
               });


            }
    }

    List<InvoiceNewData>  tempList =null ;
    public void filter(String charText)
          {
        charText = charText.toLowerCase(Locale.getDefault());
          arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(tempList);
        } else {
            for (InvoiceNewData st : tempList) {
                if(st.getCardName()!=null&&!st.getCardName().isEmpty()) {
                    if (st.getCardName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        arrayList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ValidDate() {
        arrayList.clear();
        arrayList.addAll(tempList);
        Collections.sort(arrayList, new Comparator<InvoiceNewData>() {
            @Override
            public int compare(InvoiceNewData o1, InvoiceNewData o2) {
                return o1.getDocDueDate().compareTo(o2.getDocDueDate());
            }
        });
        notifyDataSetChanged();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void PostingDate(LocalDate afterdate, LocalDate dateObj)
      {
        arrayList.clear();
        for (InvoiceNewData st : tempList) {

            if(st.getDocDueDate()!=null&&!st.getDocDueDate().isEmpty()) {
                String sDate1 = st.getDocDate();
                LocalDate date1=LocalDate.parse(sDate1);
                if(date1.isBefore(afterdate) && date1.isAfter(dateObj) ){
                    Toast.makeText(context,"Updated",Toast.LENGTH_LONG).show();
                    arrayList.add(st);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void AllData()
      {
        arrayList.clear();
        arrayList.addAll(tempList);
        notifyDataSetChanged();
    }
    public void Customername()
      {
          arrayList.clear();
        arrayList.addAll(tempList);
        Collections.sort(arrayList, new Comparator<InvoiceNewData>() {
            @Override
            public int compare(InvoiceNewData o1, InvoiceNewData o2) {
                return o1.getCardName().compareTo(o2.getCardName());
            }
        });
        notifyDataSetChanged();
    }

    public void filterCustomer(String charText)
    {
        tempList = new ArrayList<>();
        tempList.addAll(arrayList);
        charText = charText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if (charText.length() == 0) {
            arrayList.addAll(tempList);
        } else {
            for (InvoiceNewData st : tempList) {
                if(st.getCardCode()!=null&&!st.getCardCode().isEmpty()) {
                    if (st.getCardCode().toLowerCase(Locale.getDefault()).trim().equalsIgnoreCase(charText.trim())) {
                        arrayList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }




}
