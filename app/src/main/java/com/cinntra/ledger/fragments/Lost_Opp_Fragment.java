package com.cinntra.ledger.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cinntra.ledger.R;
import com.cinntra.ledger.activities.AddOpportunityActivity;
import com.cinntra.ledger.activities.OpportunitiesActivity;
import com.cinntra.ledger.adapters.Lost_Opportunities_Adapter;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.model.OpportunityItem;
import com.cinntra.ledger.viewModel.Opportunities_ViewModel;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class Lost_Opp_Fragment extends Fragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.loader)
    ProgressBar loader;
    SearchView searchView;
    int currentPage = 0;
    Context mContext;

    public Lost_Opp_Fragment() {
   //Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Lost_Opp_Fragment newInstance(String param1, String param2) {
        Lost_Opp_Fragment fragment = new Lost_Opp_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_open_opp, container, false);
        ButterKnife.bind(this,v);
        mContext =getActivity();
        if(Globals.checkInternet(getActivity()))
            callApi(loader);

        return v;
    }


    private Lost_Opportunities_Adapter adapter;
    private void callApi(ProgressBar loader)
          {
    Opportunities_ViewModel model = ViewModelProviders.of(this).get(Opportunities_ViewModel.class);
    model.getOpprtunities(loader,"Lost",currentPage).observe(getActivity(), new Observer<List<OpportunityItem>>() {
      @Override
    public void onChanged(@Nullable List<OpportunityItem> itemsList) {
          currentPage++;
        if(itemsList == null || itemsList.size() == 0){
            Globals.setmessage(getActivity());
        }else{
            adapter = new Lost_Opportunities_Adapter(getActivity(),itemsList);
            recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
            recyclerview.setAdapter(adapter);
        }
            }
        });
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.main_menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = new SearchView(((OpportunitiesActivity) mContext).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null)
                    adapter.filter(newText);

                if(newText.equalsIgnoreCase("")||newText.isEmpty())
                {
                    searchView.setIconified(true);
                    searchView.setIconified(true);
                }
                return false;
            }
        });

        searchView.setOnClickListener(new View.OnClickListener()
          {
        @Override
        public void onClick(View v) {
             }
           }
        );


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {

            case R.id.plus:
                startActivity(new Intent(getContext(), AddOpportunityActivity.class));
                break;
            case R.id.all:
                if(adapter!=null)
                    adapter.AllData();

                break;
            case R.id.my:

                break;
            case R.id.my_team:
                if(adapter!=null)
                    adapter.Favfilter("Yes");
                break;
            case  R.id.valid:

                break;
            case R.id.newest:
                if(adapter!=null)
                    adapter.Typefilter("New Business");
                break;
            case R.id.oldest:
                if(adapter!=null)
                    adapter.Typefilter("Existing Business");
                break;
        }
        return true;
    }

}