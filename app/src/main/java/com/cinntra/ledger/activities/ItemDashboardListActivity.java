package com.cinntra.ledger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import com.cinntra.ledger.R;
import com.cinntra.ledger.adapters.ItemStockAdapter;
import com.cinntra.ledger.databinding.ActivityItemDashboardListBinding;
import com.cinntra.ledger.databinding.BottomSheetDialogSelectDateBinding;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.model.DataItemDashBoard;
import com.cinntra.ledger.model.ResponseItemDashboard;
import com.cinntra.ledger.webservices.NewApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.pixplicity.easyprefs.library.Prefs;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Response;

public class ItemDashboardListActivity extends AppCompatActivity {
    private ActivityItemDashboardListBinding binding;
    String fromwhere = "";
    // ItemOnDashboardAdapter adapter;
    ItemStockAdapter adapter;
    private String searchTextValue = "";
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker;


    Long startDatelng = (long) 0.0;
    Long endDatelng = (long) 0.0;
    String startDate = Globals.firstDateOfFinancialYear();
    String endDate = Globals.lastDateOfFinancialYear();
    String toolBarname="";
    String zoneFlag = "";

    String zoneCode = "";

    @Override
    public void onBackPressed() {
        if (binding.toolbarItemDashBoard.mainHeaderLay.getVisibility() == View.GONE) {
            binding.toolbarItemDashBoard.searchLay.setVisibility(View.GONE);
            binding.toolbarItemDashBoard.mainHeaderLay.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDashboardListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StockGroupCode = getIntent().getStringExtra("ItemGroupCode");
        fromwhere = getIntent().getStringExtra("fromwhere");
        toolBarname = getIntent().getStringExtra("ItemGroupName");
        zoneCode = getIntent().getStringExtra("zoneCode");
        hideToolbarMenu();

        startDate=Prefs.getString(Globals.FROM_DATE,"");
        endDate=Prefs.getString(Globals.TO_DATE,"");
       /* if (startDate.isEmpty()) {
            from_to_date.setText("All");
        } else {
            from_to_date.setText("" + Globals.convertDateFormatInReadableFormat(startDate) +
                    "" + " To " + Globals.convertDateFormatInReadableFormat(endDate));
        }*/

        setUpSearch();



        binding.toolbarItemDashBoard.headTitle.setText(toolBarname);

        binding.toolbarItemDashBoard.backPress.setOnClickListener(view -> {
            finish();
        });


        binding.loader.setVisibility(View.VISIBLE);

        callApi("");

        binding.rvItemDash.addOnScrollListener(scrollListener);
        binding.toolbarItemDashBoard.relativeCalView.setOnClickListener(view -> {
            showDateBottomSheetDialog(ItemDashboardListActivity.this);
        });


    }

    private void setUpSearch()
    {
        binding.toolbarItemDashBoard.search.setOnClickListener(view -> {
            binding.toolbarItemDashBoard.mainHeaderLay.setVisibility(View.GONE);
            binding.toolbarItemDashBoard.searchLay.setVisibility(View.VISIBLE);

            binding.toolbarItemDashBoard.searchView.setIconifiedByDefault(true);
            binding.toolbarItemDashBoard.searchView.setFocusable(true);
            binding.toolbarItemDashBoard.searchView.setIconified(false);
            binding.toolbarItemDashBoard.searchView.requestFocusFromTouch();
        });
        binding.toolbarItemDashBoard.searchLay.setOnClickListener(view -> {
            binding.toolbarItemDashBoard.searchView.setFocusable(true);
        });

        binding.toolbarItemDashBoard.searchLay.setBackgroundColor(Color.parseColor("#00000000"));
        binding.toolbarItemDashBoard.searchLay.setVisibility(View.VISIBLE);
        binding.toolbarItemDashBoard.searchView.setVisibility(View.VISIBLE);

        binding.toolbarItemDashBoard.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("TAG", "onQueryTextSubmit: ");
                binding.toolbarItemDashBoard.searchView.clearFocus();
                //AllitemsList
                pageNo = 1;
                searchTextValue = query;
                if (!searchTextValue.isEmpty())
                    callApi(searchTextValue);
                //  cashDiscountApiTest(searchTextValue);
                return true;


                //  return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.filter(newText);
                }
                return false;
            }
        });
    }


    private void showDateBottomSheetDialog(Context context) {
        BottomSheetDialogSelectDateBinding bindingDate;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bindingDate = BottomSheetDialogSelectDateBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(bindingDate.getRoot());
        bindingDate.ivCloseBottomSheet.setOnClickListener(view ->
        {
            bottomSheetDialog.dismiss();
        });
        bindingDate.tvCustomDateBottomSheetSelectDate.setOnClickListener(view ->
        {

            bottomSheetDialog.dismiss();
            dateRangeSelector();

        });
        bindingDate.tvTodayDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Calendar.getInstance().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.Date_yyyy_mm_dd(startDatelng);
            endDate = Globals.Date_yyyy_mm_dd(endDatelng);
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");


            bottomSheetDialog.dismiss();
        });

        bindingDate.tvYesterdayDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.yesterDayCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.Date_yyyy_mm_dd(startDatelng);
            endDate = Globals.Date_yyyy_mm_dd(endDatelng);
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisWeekDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.thisWeekCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.thisWeekfirstDayOfMonth();
            endDate = Globals.thisWeekLastDayOfMonth();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });


        bindingDate.tvThisMonthBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDatelng = Globals.thisMonthCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.firstDateOfMonth();
            endDate = Globals.lastDateOfMonth();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");
            bottomSheetDialog.dismiss();

        });
        bindingDate.tvLastMonthDateBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDatelng = Globals.lastMonthCal().getTimeInMillis();
            endDatelng = Globals.thisMonthCal().getTimeInMillis();
            startDate = Globals.lastMonthFirstDate();
            endDate = Globals.lastMonthlastDate();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisQuarterDateBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDatelng = Globals.thisQuarterCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.lastQuarterFirstDate();
            endDate = Globals.lastQuarterlastDate();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisYearDateBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDatelng = Globals.thisyearCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.thisYearFirstDate();
            endDate = Globals.thisYearLastDate();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvLastYearBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDatelng = Globals.lastyearCal().getTimeInMillis();
            endDatelng = Globals.thisyearCal().getTimeInMillis();
            startDate = Globals.lastYearFirstDate();
            endDate = Globals.lastYearLastDate();
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvAllBottomSheetSelectDate.setOnClickListener(view ->
        {
            startDate = "";
            endDate = "";
            binding.loader.setVisibility(View.VISIBLE);
            pageNo = 1;
            callApi("");

            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();

    }

    private void dateRangeSelector() {


        if (startDatelng == 0.0) {
            materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds())).build();
        } else {
            materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(Pair.create(startDatelng, endDatelng)).build();

        }

        materialDatePicker.show(getSupportFragmentManager(), "Tag_Picker");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                binding.loader.setVisibility(View.VISIBLE);
                startDatelng = selection.first;
                endDatelng = selection.second;
                startDate = Globals.Date_yyyy_mm_dd(startDatelng);
                endDate = Globals.Date_yyyy_mm_dd(endDatelng);
                binding.loader.setVisibility(View.VISIBLE);
                pageNo = 1;
                callApi("");


            }
        });


    }

    public void hideToolbarMenu() {
        getSupportActionBar().hide();


        binding.toolbarItemDashBoard.relativeCalView.setVisibility(View.VISIBLE);
        binding.toolbarItemDashBoard.relativeInfoView.setVisibility(View.GONE);
        binding.toolbarItemDashBoard.filterView.setVisibility(View.GONE);
        binding.toolbarItemDashBoard.search.setVisibility(View.VISIBLE);
        binding.toolbarItemDashBoard.newQuatos.setVisibility(View.GONE);
    }


    int pageNo = 1;

    ArrayList<DataItemDashBoard> AllitemsList = new ArrayList<>();
    LinearLayoutManager layoutManager;

    boolean isLoading = false;
    boolean islastPage = false;
    boolean isScrollingpage = false;

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            // layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleitempositon = layoutManager.findFirstVisibleItemPosition(); //first item
            int visibleItemCount = layoutManager.getChildCount(); //total number of visible item
            int totalItemCount = layoutManager.getItemCount();   //total number of item

            boolean isNotLoadingAndNotLastPage = !isLoading && !islastPage;
            boolean isAtLastItem = firstVisibleitempositon + visibleItemCount >= totalItemCount;
            boolean isNotAtBeginning = firstVisibleitempositon >= 0;
            boolean isTotaolMoreThanVisible = totalItemCount >= Globals.QUERY_PAGE_SIZE;
            boolean shouldPaginate =
                    isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem && isTotaolMoreThanVisible
                            && isScrollingpage;

            if (isScrollingpage && (visibleItemCount + firstVisibleitempositon == totalItemCount)) {
                binding.loader.setVisibility(View.VISIBLE);
                //   binding.fragmentCustomer.loaderCustomer.loader.setVisibility(View.VISIBLE);
                pageNo++;
                //    itemOnPageBasis(pageNo);
                callAllPagesApi();

                isScrollingpage = false;
            } else {
                // Log.d(TAG, "onScrolled:not paginate");
                recyclerView.setPadding(0, 0, 0, 0);
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) { //it means we are scrolling
                isScrollingpage = true;
            }
        }
    };

    String StockGroupCode = "";

    private void callApi(String searchValue) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hde = new HashMap<>();

                if (fromwhere.equalsIgnoreCase("from")) {

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("SubGroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                } else if (fromwhere.equalsIgnoreCase("Zone")){

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("Zone", StockGroupCode);
                    hde.put("OrderByAmt", "");
                    hde.put("OrderByName", "");
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }else if (fromwhere.equalsIgnoreCase("zoneStock")){

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("SubGroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }
                else if (fromwhere.equalsIgnoreCase("fromSaleCategory")){

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("SubGroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }
                else {

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("GroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }

                Call<ResponseItemDashboard> call ;


                if (Prefs.getString(Globals.forSalePurchase, "").equalsIgnoreCase(Globals.Purchase)) {
                    call = NewApiClient.getInstance().getApiService().getItemOnDashboardPurchase(hde);
                }else {
                    call = NewApiClient.getInstance().getApiService().getItemOnDashboard(hde);
                }
                try {
                    Response<ResponseItemDashboard> response = call.execute();
                    if (response.isSuccessful()) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 200) {
                                    AllitemsList.clear();
                                    if (response.body().getData().size() > 0) {
                                        binding.noDatafound.setVisibility(View.GONE);
                                        AllitemsList.addAll(response.body().getData());
                                    } else {
                                        binding.noDatafound.setVisibility(View.VISIBLE);
                                    }


                                    binding.loader.setVisibility(View.GONE);
                                    // setData(response.body().getData().get(0));
                                    adapter = new ItemStockAdapter(ItemDashboardListActivity.this, AllitemsList,zoneCode);
                                    adapter.AllData(AllitemsList);
                                    layoutManager = new LinearLayoutManager(ItemDashboardListActivity.this, RecyclerView.VERTICAL, false);
                                    binding.rvItemDash.setLayoutManager(layoutManager);
                                    binding.rvItemDash.setAdapter(adapter);

                                }
                            }
                        });
                        // Handle successful response

                    } else {
                        binding.loader.setVisibility(View.GONE);
                        // Handle failed response
                    }
                } catch (IOException e) {
                    // Handle exception
                }
            }
        }).start();
    }


    private void callAllPagesApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, String> hde = new HashMap<>();
                if (fromwhere.equalsIgnoreCase("from")) {

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchTextValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("SubGroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                } else if (fromwhere.equalsIgnoreCase("Zone")){

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchTextValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("Zone", StockGroupCode);
                    hde.put("OrderByAmt", "");
                    hde.put("OrderByName", "");
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }
                else {

                    hde.put("PageNo", String.valueOf(pageNo));
                    hde.put("MaxSize", String.valueOf(Globals.QUERY_PAGE_SIZE));
                    hde.put("SearchText", searchTextValue);
                    hde.put("FromDate", startDate);
                    hde.put("ToDate", endDate);
                    hde.put("GroupCode", StockGroupCode);
                    hde.put("SalesEmployeeCode", Prefs.getString(Globals.SalesEmployeeCode,""));
                }


                Call<ResponseItemDashboard> call ;


                if (Prefs.getString(Globals.forSalePurchase, "").equalsIgnoreCase(Globals.Purchase)) {
                    call = NewApiClient.getInstance().getApiService().getItemOnDashboardPurchase(hde);
                }else {
                    call = NewApiClient.getInstance().getApiService().getItemOnDashboard(hde);
                }


                try {
                    Response<ResponseItemDashboard> response = call.execute();
                    if (response.isSuccessful()) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 200) {

                                    if (response.body().getData().size() > 0) ;
                                    AllitemsList.addAll(response.body().getData());

                                    adapter.AllData(AllitemsList);
                                    binding.loader.setVisibility(View.GONE);
                                    // setData(response.body().getData().get(0));
                                    adapter.notifyDataSetChanged();

                                }
                            }
                        });
                        // Handle successful response

                    } else {
                        binding.loader.setVisibility(View.GONE);
                        // Handle failed response
                    }
                } catch (IOException e) {
                    // Handle exception
                }
            }
        }).start();
    }


}