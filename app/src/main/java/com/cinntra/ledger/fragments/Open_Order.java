package com.cinntra.ledger.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.baoyz.widget.PullRefreshLayout;
import com.cinntra.ledger.R;
import com.cinntra.ledger.activities.AddOrderAct;
import com.cinntra.ledger.activities.OrderActivity;
import com.cinntra.ledger.adapters.Open_Order_Adapter;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.model.QuotationItem;
import com.cinntra.ledger.viewModel.QuotationList_ViewModel;
import com.pixplicity.easyprefs.library.Prefs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Open_Order extends Fragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.loader)
    ProgressBar loader;
    @BindView(R.id.no_datafound)
    ImageView no_datafound;
    @BindView(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    int currentpage = 0;
    boolean recallApi = true;
    ArrayList<QuotationItem> AllItemList;
    LinearLayoutManager layoutManager;

    public Open_Order() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Open_Order newInstance(String param1, String param2) {
        Open_Order fragment = new Open_Order();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    Open_Order_Adapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View v=inflater.inflate(R.layout.fragment_open_order, container, false);
    ButterKnife.bind(this,v);
        Prefs.putString("FromLedger","Main");
    AllItemList = new ArrayList<>();
    layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);

    if(Globals.checkInternet(getActivity())) {
        {
            loader.setVisibility(View.VISIBLE);
            callApi(loader);
        }

    }

        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(Globals.checkInternet(getActivity())){

                    callApi(loader);
                }
                else
                    swipeRefreshLayout.setRefreshing(false);

            }
        });
        /*recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
               }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(layoutManager.findLastCompletelyVisibleItemPosition() == AllItemList.size()-3 && recallApi)
                {
                    callApi(loader);
                }
            }
        });*/
    return v;
    }



    private void callApi(ProgressBar loader)
          {
     loader.setVisibility(View.VISIBLE);
     QuotationList_ViewModel model = ViewModelProviders.of(this).get(QuotationList_ViewModel.class);
     model.getOrders(loader).observe(getActivity(), new Observer<List<QuotationItem>>() {
     @Override
     public void onChanged(@Nullable List<QuotationItem> itemsList) {



        AllItemList.clear();
         assert itemsList != null;
         AllItemList.addAll(itemsList);
        adapter = new Open_Order_Adapter(getContext(),AllItemList);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();


         if(itemsList.size()<=0)
             no_datafound.setVisibility(View.VISIBLE);
         else
             no_datafound.setVisibility(View.GONE);
         swipeRefreshLayout.setRefreshing(false);
    }


        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {


        //menu.clear();
        inflater.inflate(R.menu.order_filter, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.search);
        MenuItem itemPlus = menu.findItem(R.id.plus);
        itemPlus.setVisible(false);
        SearchView searchView = new SearchView(((OrderActivity) getContext()).getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Search Orders");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                Log.e("OrderW==>",newText.toString());
                if(adapter!=null)
                    adapter.filter(newText);
                return true;
            }
        });




    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
           {
        switch(item.getItemId())
        {
            case R.id.plus:
                Prefs.putString(Globals.FromQuotation,"Order");
                startActivity(new Intent(getContext(), AddOrderAct.class));
                break;
            case R.id.all:
                if(adapter!=null)
                    adapter.allData();
                break;
            case R.id.customer:
                if(adapter!=null)
                    adapter.Customerfilter();
                break;
            case R.id.posting:

                break;
            case  R.id.valid:
                if(adapter!= null)
                    adapter.ValidDate();
                break;
            case R.id.order:
                LocalDate dateObj2 = LocalDate.parse(Globals.curntDate);
                LocalDate afterdate2 = dateObj2.minusDays(8);
                adapter.PostingDate(afterdate2, dateObj2);
                break;
        }
        return true;
    }




}