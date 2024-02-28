package com.cinntra.ledger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cinntra.ledger.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Bussiness_Partner_Fragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.back_press)
    RelativeLayout back_press;
    @BindView(R.id.head_title)
    TextView head_title;
    @BindView(R.id.tab_1)
    TextView general;
    @BindView(R.id.tab_2)
    TextView address;
    @BindView(R.id.tab_3)
    TextView transaction;
    @BindView(R.id.tab_4)
    TextView dashboard;
    @BindView(R.id.new_quatos)
    RelativeLayout new_quatos;
    @BindView(R.id.add)
    ImageView add;



    public Bussiness_Partner_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Bussiness_Partner_Fragment newInstance(String param1, String param2) {
        Bussiness_Partner_Fragment fragment = new Bussiness_Partner_Fragment();
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
    //Inflate the layout for this fragment
    View v=inflater.inflate(R.layout.fragment_bussiness_partner, container, false);
    ButterKnife.bind(this,v);
    setDefaults();
    return v;
      }

    private void setDefaults() {
        head_title.setText(getString(R.string.bussiness_partner));
        add.setImageDrawable(getResources().getDrawable(R.drawable.edit));
        back_press.setOnClickListener(this);
        general.setOnClickListener(this);
        address.setOnClickListener(this);
        transaction.setOnClickListener(this);
        dashboard.setOnClickListener(this);
        new_quatos.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_press:
                getActivity().onBackPressed();
                break;
            case R.id.tab_1:
                break;
            case R.id.tab_2:
                break;
            case R.id.tab_3:
                break;
            case R.id.tab_4:
                break;
            case R.id.new_quatos:
                Edit_Bussiness_Partner_Fragment  frg = new Edit_Bussiness_Partner_Fragment();
                FragmentTransaction tr  = getFragmentManager().beginTransaction();
                tr.replace(R.id.quatoes_main_container, frg);
                tr.addToBackStack(null);
                tr.commit();
                break;
        }
    }
}