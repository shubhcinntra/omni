package com.cinntra.ledger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cinntra.ledger.R;
import com.cinntra.ledger.adapters.Item_Adapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Item_Fragment extends Fragment {

     @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    public Item_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Item_Fragment newInstance(String param1, String param2) {
        Item_Fragment fragment = new Item_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_item_add, container, false);
        ButterKnife.bind(this,v);
        setDefaults();
        return v;
    }

    private void setDefaults() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        recyclerview.setAdapter(new Item_Adapter(getActivity()));
    }
}