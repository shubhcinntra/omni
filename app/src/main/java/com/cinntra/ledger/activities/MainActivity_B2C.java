package com.cinntra.ledger.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.ledger.R;
import com.cinntra.ledger.adapters.BPDropdownAdapter;
import com.cinntra.ledger.adapters.ContactPersonAdapter;
import com.cinntra.ledger.adapters.LeadDropDownAdapter;
import com.cinntra.ledger.adapters.RecentAdapter;
import com.cinntra.ledger.adapters.RecentOrderAdapter;
import com.cinntra.ledger.databinding.ActivityMainB2CBinding;
import com.cinntra.ledger.databinding.BottomSheetDialogSelectDateBinding;
import com.cinntra.ledger.databinding.CheckOutExpenseDialogBinding;
import com.cinntra.ledger.databinding.CheckinDialogBinding;
import com.cinntra.ledger.fragments.DashBoardWithSalePurchase;
import com.cinntra.ledger.fragments.Dashboard;
import com.cinntra.ledger.fragments.DashboardFragmentFromActivity;
import com.cinntra.ledger.fragments.ItemsBottomFragment;
import com.cinntra.ledger.fragments.PartyFragment;
import com.cinntra.ledger.fragments.PurchaseDashBoardFragment;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.globals.MainBaseActivity;
import com.cinntra.ledger.model.BusinessPartnerData;
import com.cinntra.ledger.model.ContactPerson;
import com.cinntra.ledger.model.ContactPersonData;
import com.cinntra.ledger.model.CounterResponse;
import com.cinntra.ledger.model.CountryResponse;
import com.cinntra.ledger.model.DashboardCounterData;
import com.cinntra.ledger.model.DashboardCounterResponse;
import com.cinntra.ledger.model.EventResponse;
import com.cinntra.ledger.model.EventValue;
import com.cinntra.ledger.model.ExpenseDataModel;
import com.cinntra.ledger.model.ExpenseResponse;
import com.cinntra.ledger.model.LeadFilter;
import com.cinntra.ledger.model.MapData;
import com.cinntra.ledger.model.MapResponse;
import com.cinntra.ledger.model.QuotationItem;
import com.cinntra.ledger.model.QuotationResponse;
import com.cinntra.ledger.model.ResponseTripCheckIn;
import com.cinntra.ledger.model.ResponseTripCheckOut;
import com.cinntra.ledger.model.SalesEmployeeItem;
import com.cinntra.ledger.model.SalesGraphResponse;
import com.cinntra.ledger.newapimodel.LeadResponse;
import com.cinntra.ledger.newapimodel.LeadValue;
import com.cinntra.ledger.newapimodel.ResponsePayMentDueCounter;
import com.cinntra.ledger.newapimodel.ResponseReceivableGraph;
import com.cinntra.ledger.viewModel.CustomerViewModel;
import com.cinntra.ledger.viewpager.GraphPagerAdapter;
import com.cinntra.ledger.webservices.NewApiClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity_B2C extends AppCompatActivity {

    AlertDialog.Builder builder;
    AlertDialog alertDialog;
    private static final int PICK_FILE_REQUEST_CODE = 11111;
    private static final int PICK_FILE_CHECKOUT_REQUEST_CODE = 14321;
    private static final int REQUEST_IMAGE_CAPTURE = 10111;

    String locationtype;
    LocationManager locationManager;
    double latitude;
    double longitude;
    LeadDropDownAdapter leadDropDownAdapter;
    FusedLocationProviderClient client;

    String bpType = "";
    String bpFullName = "";
    String bpCardCode = "";
    File imageFile;


    private ActivityMainB2CBinding binding;
    RecentAdapter adapter;

    String dateSelectorValue = "";

    // BarChart volumeReportChart;

    ArrayList<LeadValue> leadValueList = new ArrayList<>();

    String bpId = "";
    private String picturePath = "";
    private String checkOUtPicturePath = "";
    private String cameraImagePath = "";


    CheckinDialogBinding checkinDialogBinding;


    private void callDashboardCounter_Receiable() {
        alertDialog.show();

        HashMap obj = new HashMap<String, String>();
        obj.put("Filter", "");
        obj.put("Code", "");
        obj.put("Type", "Gross");
        obj.put("FromDate", startDate);
        obj.put("ToDate", endDate);
        obj.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<DashboardCounterResponse> call = NewApiClient.getInstance().getApiService().getDashBoardCounterForLedger(obj);
        call.enqueue(new Callback<DashboardCounterResponse>() {
            @Override
            public void onResponse(Call<DashboardCounterResponse> call, Response<DashboardCounterResponse> response) {
                if (response.code() == 200) {
                    alertDialog.dismiss();

                    //   binding.contentData.totalCollection.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(response.body().getData().get(0).getDifferenceAmount())));
                    //   binding.contentData.totalPendings.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(response.body().getData().get(0).getTotalPendingSales())));


                } else {
                    alertDialog.dismiss();

                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DashboardCounterResponse> call, Throwable t) {
                alertDialog.dismiss();

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //  callBplistApi(bp_spinner, cp_spinner);
    }

    private void callDashboardCounter() {
        alertDialog.show();

        HashMap<String, String> obj = new HashMap<String, String>();
        obj.put("Filter", "");
        obj.put("Code", "");
        obj.put("Type", "Gross");
        obj.put("FromDate", startDate);
        obj.put("ToDate", endDate);
        obj.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<DashboardCounterResponse> call = NewApiClient.getInstance().getApiService().getDashBoardCounterForLedger(obj);
        call.enqueue(new Callback<DashboardCounterResponse>() {
            @Override
            public void onResponse(Call<DashboardCounterResponse> call, Response<DashboardCounterResponse> response) {
                if (response.code() == 200) {
                    alertDialog.dismiss();
                    setCounter(response.body().getData().get(0));


                    //  lead_spiner.setAdapter(leadAdapter);
                    //  leadAdapter.notifyDataSetChanged();
                } else {
                    alertDialog.dismiss();

                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DashboardCounterResponse> call, Throwable t) {
                alertDialog.dismiss();

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //  callBplistApi(bp_spinner, cp_spinner);
    }

    private void setCounter(DashboardCounterData data) {
        /*binding.contentData.totalAmnt.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(data.getTotalSales())));
        binding.contentData.receivedAmountValue.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(data.getTotalReceivePayment())));
        binding.contentData.totalPendings.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(data.getTotalPendingSales())));
   */
    }


    private void callleadDialogApi(Spinner lead_spiner) {
        LeadFilter lv = new LeadFilter();
        lv.setAssignedTo(Prefs.getString(Globals.MyID, ""));
        lv.setLeadType("All");
        Call<LeadResponse> call = NewApiClient.getInstance().getApiService().getAllLead(lv);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                if (response.code() == 200) {
                    leadValueList.clear();
                    leadValueList.addAll(response.body().getData());
                    List<String> itemNames = new ArrayList<>();
                    List<String> itemCode = new ArrayList<>();
                    for (LeadValue item : leadValueList) {
                        itemNames.add(item.getCompanyName());
                    }

                    for (LeadValue item : leadValueList) {
                        //    itemCode.add(item.getCardCode());
                    }

                    //  LeadDropDownAdapter leadAdapter = new LeadDropDownAdapter(MainActivity_B2C.this, leadValueList);
                    lead_spiner.setAdapter(new ArrayAdapter<>(MainActivity_B2C.this, android.R.layout.simple_spinner_item, itemNames));

                    lead_spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            bpFullName = leadValueList.get(i).getCompanyName();


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    //  lead_spiner.setAdapter(leadAdapter);
                    //  leadAdapter.notifyDataSetChanged();
                } else {

                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //  callBplistApi(bp_spinner, cp_spinner);
    }

    private void callleadApi(Spinner bp_spinner, Spinner cp_spinner) {
        LeadFilter lv = new LeadFilter();
        lv.setAssignedTo(Prefs.getString(Globals.MyID, ""));
        lv.setLeadType("All");
        Call<LeadResponse> call = NewApiClient.getInstance().getApiService().getAllLead(lv);
        call.enqueue(new Callback<LeadResponse>() {
            @Override
            public void onResponse(Call<LeadResponse> call, Response<LeadResponse> response) {
                if (response.code() == 200) {
                    leadValueList.clear();
                    leadValueList.addAll(response.body().getData());
                    LeadDropDownAdapter leadAdapter = new LeadDropDownAdapter(MainActivity_B2C.this, leadValueList);

                    leadDropDownAdapter.notifyDataSetChanged();
                } else {

                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    LeadResponse mError = new LeadResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, LeadResponse.class);
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LeadResponse> call, Throwable t) {

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        callBplistApi(bp_spinner, cp_spinner);
    }

    ArrayList<BusinessPartnerData> AllitemsList = new ArrayList<>();

    private void callBplistApi(Spinner bp_spinner, Spinner cp_spinner) {
    /*    CustomerViewModel model = ViewModelProviders.of(MainActivity_B2C.this).get(CustomerViewModel.class);
        model.getCustomersList(binding.contentData.loader.loader).observe(MainActivity_B2C.this, new Observer<List<BusinessPartnerData>>() {
            @Override
            public void onChanged(@Nullable List<BusinessPartnerData> itemsList) {

                if (itemsList.size() >= 0) {
                    AllitemsList.clear();
                    AllitemsList.addAll(itemsList);
                    List<String> itemNames = new ArrayList<>();
                    List<String> itemCode = new ArrayList<>();
                    for (BusinessPartnerData item : AllitemsList) {
                        itemNames.add(item.getCardName());
                    }

                    for (BusinessPartnerData item : AllitemsList) {
                        itemCode.add(item.getCardCode());
                    }


                    //  bp_spinner.setAdapter(new BPDropdownAdapter(MainActivity_B2C.this, AllitemsList));
                    bp_spinner.setAdapter(new ArrayAdapter<>(MainActivity_B2C.this, android.R.layout.simple_spinner_item, itemNames));
                    bp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            bpId = AllitemsList.get(i).getCardCode();
                            bpCardCode = AllitemsList.get(i).getCardCode();
                            bpFullName = AllitemsList.get(i).getCardName();

                            //  callcontactpersonApi(cp_spinner,filterwithoutprospect(AllitemsList).get(i).getCardCode());
                            callcontactpersonApi(cp_spinner, AllitemsList.get(i).getCardCode());
                            bpReSourceID = AllitemsList.get(i).getCardCode();


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


                }
            }

        });
*/
    }


    double startlat;
    double startlong;
    double distanceMAP;


    private void callApi(double latitude, double longitude, MapData mapData, String address) {
        mapData.setLat(String.valueOf(latitude));
        mapData.setLong(String.valueOf(longitude));
        mapData.setUpdateDate(Globals.getTodaysDatervrsfrmt());
        mapData.setUpdateTime(Globals.getTCurrentTime());
        mapData.setAddress(address);
        mapData.setShape("meeting");
        mapData.setType(locationtype);
        mapData.setEmp_Id(Prefs.getString(Globals.MyID, "1"));
        mapData.setEmp_Name(Prefs.getString(Globals.Employee_Name, ""));
        Call<MapResponse> call = NewApiClient.getInstance().getApiService().sendMaplatlong(mapData);

        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response != null) {
                    if (response.body().getValue().size() > 0) {


                        startlat = Double.parseDouble(response.body().getValue().get(0).getLat());
                        startlong = Double.parseDouble(response.body().getValue().get(0).getLong());

                        distanceMAP = distance(startlat, startlong, latitude, longitude);
                    }

                    if (locationtype.equalsIgnoreCase("Start")) {
                        if (Prefs.getString(Globals.CHECK_IN_STATUS, "CheckOut").equalsIgnoreCase("Stop")) {
                            Prefs.putString(Globals.CHECK_IN_STATUS, "Start");

                            //   binding.slideView.setText(" Check Out");
                        } else {
                            Prefs.putString(Globals.CHECK_IN_STATUS, "Stop");

                            //  binding.slideView.setText(" Check In");
                        }
                        Log.e("success", "success");
                    } else {
                        Prefs.putString(Globals.CHECK_IN_STATUS, "Stop");

                        //   binding.slideView.setText(" Check In");
                        //  openExpenseDialog();
                    }


                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
    }


    private void callExpenseTravelApi(double latitude, double longitude, MapData mapData, String address) {
        mapData.setLat(String.valueOf(latitude));
        mapData.setLong(String.valueOf(longitude));
        mapData.setUpdateDate(Globals.getTodaysDatervrsfrmt());
        mapData.setUpdateTime(Globals.getTCurrentTime());
        mapData.setAddress(address);
        mapData.setShape("travel");
        mapData.setType(locationtype);
        mapData.setEmp_Id(Prefs.getString(Globals.MyID, "1"));
        mapData.setEmp_Name(Prefs.getString(Globals.Employee_Name, ""));
        Call<MapResponse> call = NewApiClient.getInstance().getApiService().sendMaplatlong(mapData);

        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response != null) {
                    if (response.body().getValue().size() > 0) {


                        startlat = Double.parseDouble(response.body().getValue().get(0).getLat());
                        startlong = Double.parseDouble(response.body().getValue().get(0).getLong());

                        distanceMAP = distance(startlat, startlong, latitude, longitude);
                    }

                    if (Prefs.getString(Globals.EXPENSE_STATUS, "").equalsIgnoreCase("Stop")) {
                        Prefs.putString(Globals.EXPENSE_STATUS, "Start");

                        //    binding.slideExpense.setText("Trip Stop");

                    } else {
                        Prefs.putString(Globals.EXPENSE_STATUS, "Stop");

                        // binding.slideExpense.setText("Trip Start");
                    }


                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
    }


    Long startDatelng = (long) 0.0;
    Long endDatelng = (long) 0.0;
    MaterialDatePicker<Pair<Long, Long>> materialDatePicker;


    private void showDateInDashboardBottomSheetDialog(Context context) {
        BottomSheetDialogSelectDateBinding bindingDate;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bindingDate = BottomSheetDialogSelectDateBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(bindingDate.getRoot());
        bindingDate.ivCloseBottomSheet.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
        });
        bindingDate.tvCustomDateBottomSheetSelectDate.setOnClickListener(view -> {

            bottomSheetDialog.dismiss();
            dateRangeSelector();

        });
        bindingDate.tvTodayDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Calendar.getInstance().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.Date_yyyy_mm_dd(startDatelng);
            endDate = Globals.Date_yyyy_mm_dd(endDatelng);
            alertDialog.show();
            // binding.contentData.tvDateText.setText("Today");
            callDashboardCounter();
            callDashboardCounter_Receiable();


            bottomSheetDialog.dismiss();
        });

        bindingDate.tvYesterdayDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.yesterDayCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.Date_yyyy_mm_dd(startDatelng);
            endDate = Globals.Date_yyyy_mm_dd(endDatelng);
            alertDialog.show();
            //  binding.contentData.tvDateText.setText("Yesterday");
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisWeekDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.thisWeekCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.thisWeekfirstDayOfMonth();
            endDate = Globals.thisWeekLastDayOfMonth();
            alertDialog.show();
            //  binding.contentData.tvDateText.setText("This Week");
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });


        bindingDate.tvThisMonthBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.thisMonthCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.firstDateOfMonth();
            endDate = Globals.lastDateOfMonth();
            alertDialog.show();
            //  binding.contentData.tvDateText.setText("This Month");
            callDashboardCounter();
            callDashboardCounter_Receiable();
            bottomSheetDialog.dismiss();

        });
        bindingDate.tvLastMonthDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.lastMonthCal().getTimeInMillis();
            endDatelng = Globals.thisMonthCal().getTimeInMillis();
            startDate = Globals.lastMonthFirstDate();
            endDate = Globals.lastMonthlastDate();
            alertDialog.show();
         /*  binding.contentData.tvDateText.setText("" + Globals.convertDateFormatInReadableFormat(startDate) + " to "
                    + Globals.convertDateFormatInReadableFormat(endDate));*/
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisQuarterDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.thisQuarterCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.lastQuarterFirstDate();
            endDate = Globals.lastQuarterlastDate();
            alertDialog.show();
         /*  binding.contentData.tvDateText.setText("" + Globals.convertDateFormatInReadableFormat(startDate) + " to "
                    + Globals.convertDateFormatInReadableFormat(endDate));*/
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvThisYearDateBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.thisyearCal().getTimeInMillis();
            endDatelng = Calendar.getInstance().getTimeInMillis();
            startDate = Globals.firstDateOfFinancialYear();
            endDate = Globals.lastDateOfFinancialYear();
            alertDialog.show();
         /*   binding.contentData.tvDateText.setText("" + Globals.convertDateFormatInReadableFormat(startDate) + " to "
                    + Globals.convertDateFormatInReadableFormat(endDate));*/
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvLastYearBottomSheetSelectDate.setOnClickListener(view -> {
            startDatelng = Globals.lastyearCal().getTimeInMillis();
            endDatelng = Globals.thisyearCal().getTimeInMillis();
            startDate = Globals.lastYearFirstDate();
            endDate = Globals.lastYearLastDate();
            alertDialog.show();
           /* binding.contentData.tvDateText.setText("" + Globals.convertDateFormatInReadableFormat(startDate) + " to "
                    + Globals.convertDateFormatInReadableFormat(endDate));*/
            callDashboardCounter();
            callDashboardCounter_Receiable();

            bottomSheetDialog.dismiss();
        });
        bindingDate.tvAllBottomSheetSelectDate.setOnClickListener(view -> {
            startDate = "";
            endDate = "";
            alertDialog.show();
            /*  binding.contentData.tvDateText.setText("All");*/
            callDashboardCounter();
            callDashboardCounter_Receiable();

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

                // binding.loader.setVisibility(View.VISIBLE);
                startDatelng = selection.first;
                endDatelng = selection.second;
                startDate = Globals.Date_yyyy_mm_dd(startDatelng);
                endDate = Globals.Date_yyyy_mm_dd(endDatelng);
                alertDialog.show();
           /*     binding.contentData.tvDateText.setText("" + Globals.convertDateFormatInReadableFormat(startDate) + " to "
                        + Globals.convertDateFormatInReadableFormat(endDate));*/
                callDashboardCounter();
                callDashboardCounter_Receiable();


            }
        });


    }

    private void callAllDueCounter() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        jsonObject.addProperty("DueDaysGroup", "-1");

        Call<ResponsePayMentDueCounter> call = NewApiClient.getInstance().getApiService().getPaymentDueCounter(jsonObject);
        call.enqueue(new Callback<ResponsePayMentDueCounter>() {
            @Override
            public void onResponse(Call<ResponsePayMentDueCounter> call, Response<ResponsePayMentDueCounter> response) {
                if (response.code() == 200) {
                    //  Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    if (response.body().getStatus() == 200) {
                        //  Toast.makeText(requireContext(), "success 200", Toast.LENGTH_SHORT).show();
                        /*  binding.contentData.tvOverDueCounter.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(response.body().getTotalPaybal())));
                         */
                    } else {
                        //  Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    alertDialog.dismiss();


                }

            }

            @Override
            public void onFailure(Call<ResponsePayMentDueCounter> call, Throwable t) {
                alertDialog.dismiss();

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callPaymentDueCounter() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        jsonObject.addProperty("DueDaysGroup", "7");

        Call<ResponsePayMentDueCounter> call = NewApiClient.getInstance().getApiService().getPaymentDueCounter(jsonObject);
        call.enqueue(new Callback<ResponsePayMentDueCounter>() {
            @Override
            public void onResponse(Call<ResponsePayMentDueCounter> call, Response<ResponsePayMentDueCounter> response) {
                if (response.code() == 200) {
                    //  Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    if (response.body().getStatus() == 200) {
                        //  Toast.makeText(requireContext(), "success 200", Toast.LENGTH_SHORT).show();
                        /*  binding.contentData.tvAnjaliDuePayment.setText(getResources().getString(R.string.Rs) + " " + Globals.numberToK(String.valueOf(response.body().getTotalPaybal())));
                         */
                    } else {
                        //  Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    alertDialog.dismiss();


                }

            }

            @Override
            public void onFailure(Call<ResponsePayMentDueCounter> call, Throwable t) {
                alertDialog.dismiss();

                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // GraphViewPagerAdapter fakeLiveMatchAdapter;
    GraphPagerAdapter graphPagerAdapter;
    public static View dateSPinner;
    ArrayAdapter<CharSequence> dateSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainB2CBinding.inflate(getLayoutInflater());
        ButterKnife.bind(this);
        setContentView(binding.getRoot());

        Globals.CURRENT_CLASS = getClass().getName();
        //   dateSPinner = binding.contentData.dateSelector;
        leadDropDownAdapter = new LeadDropDownAdapter(MainActivity_B2C.this, leadValueList);
        client = LocationServices.getFusedLocationProviderClient(MainActivity_B2C.this);
        //   Toolbar toolbar = binding.toolbar;
        //    setSupportActionBar(toolbar);
        // CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        //toolBarLayout.setTitle(getTitle());
        //   binding.contentData.loader.loader.setVisibility(View.GONE);
        //   binding.appBar.setVisibility(View.VISIBLE);
        //  FloatingActionButton fab = binding.fab;
        //   volumeReportChart = binding.contentData.anyChartView;
        //   binding.lineartopToolbar.setVisibility(View.GONE);
        //Create an ArrayAdapter using a custom layout for the dropdown items
        dateSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.date_selector, // Replace with your item array resource
                R.layout.spinner_textview_dashboard);

// Set the dropdown view resource
        dateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set the adapter for the Spinner
        ///  binding.contentData.dateSelector.setAdapter(dateSpinnerAdapter);

        callPaymentDueCounter();
        callAllDueCounter();

        builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_dialog_alert).setCancelable(false);


        alertDialog = builder.create();


        if (Prefs.getString(Globals.CHECK_IN_STATUS, "").equalsIgnoreCase("Start")) {

            MainBaseActivity.boolean_permission = true;
        }

     /*   if (MainBaseActivity.boolean_permission) {
            Log.e("start", "start");
            getLatitudeLongitudeForCheckIn();
               // openremarksdialog();
        } else {
            givepermission();
        }*/


        callDashboardCounter();
        callDashboardCounter_Receiable();
        saleGraphApi();


     /*   binding.contentData.linearDate.setOnClickListener(bind -> {
            showDateInDashboardBottomSheetDialog(this);
        });*/


     /*   if (Prefs.getString(Globals.CHECK_IN_STATUS, "").equalsIgnoreCase("Start")) {
            binding.toggleCheckIN.setChecked(true);
            binding.headingcheckIn.setText("Check Out");
            binding.slideView.setText(" Check Out");
            MainBaseActivity.boolean_permission = true;
        } else {
            binding.toggleCheckIN.setChecked(false);
            binding.headingcheckIn.setText("Check In");
            binding.slideView.setText(" Check In");
        }

        if (Prefs.getString(Globals.EXPENSE_STATUS, "").equalsIgnoreCase("Start")) {
            binding.slideExpense.setText(" Start");
        } else {
            binding.slideExpense.setText(" Stop");
        }*/


      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

   /*     binding.datePicker.setVisibility(View.GONE);
        binding.datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dateRangeSelector();
            }
        });*/


       /* binding.contentData.pendingOrderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Prefs.getString(Globals.Role, "").trim().equalsIgnoreCase("admin") || Prefs.getString(Globals.Role, "").trim().equalsIgnoreCase("Director") || Prefs.getString(Globals.Role, "").trim().equalsIgnoreCase("Accounts")) {
                    startActivity(new Intent(MainActivity_B2C.this, PendingOrders.class));
                } else {

                    Toast.makeText(MainActivity_B2C.this, "Under Maintenance", Toast.LENGTH_SHORT).show();
                }
            }
        });*/


/*        binding.contentData.receivedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("ForReports", "ReceiptLedger");
                startActivity(new Intent(MainActivity_B2C.this, Reports.class));

            }
        });


        binding.contentData.salesAmountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("ForReports", "MainActivity_B2C_Ledger");
                startActivity(new Intent(MainActivity_B2C.this, Sale_Inovice_Reports.class));

            }
        });
        binding.contentData.paymentCollectionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("ForReports", "Receivable");
                startActivity(new Intent(MainActivity_B2C.this, Reports.class));

            }
        });

        binding.contentData.newtypeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("ForReports", "payment");
                startActivity(new Intent(MainActivity_B2C.this, Reports.class));

            }
        });

        binding.contentData.overDuecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putString("ForReports", "overDue");
                startActivity(new Intent(MainActivity_B2C.this, Reports.class));

            }
        });

        binding.contentData.dateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dateSelectorValue = binding.contentData.dateSelector.getSelectedItem().toString().trim();
                //selectDate(dateSelectorValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        dashboardcounter();

        callCountryApi();

      /*  binding.proImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity_B2C.this, ProfileActivity.class);
                startActivity(i);
            }
        });*/


        BottomNavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setSelectedItemId(R.id.home);
        //navigationView.setBackground(null);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.home: {
                        Prefs.putString(Globals.forSalePurchase, Globals.Sale);
                        fragment = new DashBoardWithSalePurchase();
                        break;
                    }

                    case R.id.partyBottomMenu:
                        fragment = new PartyFragment();
                        break;
                    case R.id.reportBottomMenu:
                        // fragment = new Graph_();
                        Prefs.putString(Globals.forSalePurchase, Globals.Sale);
                        fragment = new DashboardFragmentFromActivity();
                        break;

                    case R.id.itemListDashboardMenu:
                        fragment = new ItemsBottomFragment();
                        break;
                    case R.id.settings:
                        // fragment = new MoreViewsFragmnet();
                        Prefs.putString(Globals.forSalePurchase, Globals.Purchase);
                        fragment = new PurchaseDashBoardFragment();

                }
                return loadFragment(fragment);
            }
        });

        navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // Prevent reselection by doing nothing when an item is reselected
            }
        });

    /*    binding.quickCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openpopup();
//                startActivity(new Intent(MainActivity_B2C.this,Reports.class));
            }
        });*/


        opengraph();

        Fragment fragment = new DashboardFragmentFromActivity();
        // loadFragment(fragment);

        loadFragment(new DashBoardWithSalePurchase());

    }

    boolean bottomNav = false;

    private void setupChartViewPgaer() {
        graphPagerAdapter = new GraphPagerAdapter(this);
        //  binding.contentData.viewPagerChart.setAdapter(graphPagerAdapter);
        //   binding.contentData.tabLayout.setupWithViewPager(binding.contentData.viewPagerChart, true);
       /* binding.contentData.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    binding.contentData.topHeadingGraph.setText("Sales");
                } else if (tab.getPosition() == 1) {
                    binding.contentData.topHeadingGraph.setText("Receipt");
                } else if (tab.getPosition() == 2) {
                    binding.contentData.topHeadingGraph.setText("Receivables");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/
        // binding.contentData.ta
    }

    String sourceType = "";
    String selectsourceType = "";
    String leadReSourceID = "0";
    String prospectbpReSourceID = "";
    String bpReSourceID = "0";
    String bpContactPerson = "";
    String prospectContactPerson = "";
    Dialog dialog;
    String expenseString = "";
    String stringModeTypeOfTransport = "";
    String costSt = "";
    String distanceSt = "";
    String expenseRemarkSt = "";
    String attchmentName = "";
    String attachentUri = "";
    String attachmentcheckOut = "";
    EditText attachmentName;
    EditText etNewAttachemnt;


    boolean toggleDefaultChecker = false;

    private void opencheckinDialog() {
        MapData mapData = new MapData();

        String userType = Prefs.getString(Globals.Role, "admin");
        int transportType = -1;

        checkinDialogBinding = CheckinDialogBinding.inflate(getLayoutInflater());


        dialog = new Dialog(MainActivity_B2C.this);
        // dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setTitle("FollowUp FeedBack");
        dialog.setContentView(checkinDialogBinding.getRoot());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        Button add = dialog.findViewById(R.id.add);
        Button btnChooseFileCheckIn = dialog.findViewById(R.id.btnChooseFIleCheckIN);
        etNewAttachemnt = dialog.findViewById(R.id.etAttachmentsNameCHeckIN);
        Button createLead = dialog.findViewById(R.id.btnCreateLead);
        LinearLayout lead_view = dialog.findViewById(R.id.lead_view);
        LinearLayout lead_view_shubh = dialog.findViewById(R.id.lead_view_shubh);
        LinearLayout linearBusinessTab = dialog.findViewById(R.id.linearBusiness);
        LinearLayout linearExpenseTab = dialog.findViewById(R.id.linearExpense);
        RadioGroup radioGroup = dialog.findViewById(R.id.rgChekIn);
        RadioButton rbBusiness = dialog.findViewById(R.id.rbBusiness);
        RadioButton rbExpense = dialog.findViewById(R.id.rbExpense);
        LinearLayout newBusinessView = dialog.findViewById(R.id.newBusinessView);
        LinearLayout prospect_view = dialog.findViewById(R.id.prospect_view);
        LinearLayout exist_view = dialog.findViewById(R.id.exist_view);
        LinearLayout tendor_view = dialog.findViewById(R.id.linearTendor);
        LinearLayout lead_add = dialog.findViewById(R.id.lead_add);
        EditText comment_value = dialog.findViewById(R.id.comment_value);
        EditText cost = dialog.findViewById(R.id.etCostOfExpense);

        Spinner bp_spinner = dialog.findViewById(R.id.bp_spinner);
        Spinner spinnerModeTransport = dialog.findViewById(R.id.spinnerModeOfTransport);
        Spinner lead_spinner = dialog.findViewById(R.id.lead_spinner);
        Spinner cp_spinner = dialog.findViewById(R.id.cp_spinner);
        Spinner prospectcp_spinner = dialog.findViewById(R.id.prospectcp_spinner);
        Spinner type_spinner = dialog.findViewById(R.id.type_spinner);
        Spinner select_spinner = dialog.findViewById(R.id.select_spinner);
        Spinner prospectbp_spinner = dialog.findViewById(R.id.prospectbp_spinner);

        Spinner expenseType = dialog.findViewById(R.id.spinnerTypeOfExpense);
        EditText expenseRemark = dialog.findViewById(R.id.etExpenseRemark);
        attachmentName = dialog.findViewById(R.id.etAttachmentsName);
        EditText distance = dialog.findViewById(R.id.etDistanceOfExpense);

        TextView contactPerson = dialog.findViewById(R.id.cp_text);

        Button btnChooseFile = dialog.findViewById(R.id.btnChooseFIle);
        lead_view_shubh.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> expenseAdapter;
        ArrayAdapter<CharSequence> modeOfTransportAdapter;


        lead_view_shubh.setVisibility(View.GONE);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                toggleDefaultChecker = true;
                //  binding.toggleCheckIN.setChecked(false);
            }
        });

        if (userType.equalsIgnoreCase("Executive")) {
            transportType = R.array.executive_mode_of_transport;
        } else if (userType.equalsIgnoreCase("Senior Executive") || userType.equalsIgnoreCase("Assistant Manager")) {
            transportType = R.array.senior_executive_and_assistent_Manager_mode_of_transport;
        } else if (userType.equalsIgnoreCase("Manager") || userType.equalsIgnoreCase("Sr. Manager")) {
            transportType = R.array.manager_and_senior_manager_mode_of_transport;

        } else if (userType.equalsIgnoreCase("Assistant General Manager")) {
            transportType = R.array.assistent_general_manager_mode_of_transport;


        } else if (userType.equalsIgnoreCase("General Manager") || userType.equalsIgnoreCase("Vice President")) {
            transportType = R.array.general_manager_and_vice_president_mode_of_transport;

        } else {
            transportType = R.array.executive_mode_of_transport;
        }

        modeOfTransportAdapter = ArrayAdapter.createFromResource(MainActivity_B2C.this, transportType, android.R.layout.simple_spinner_item);
        modeOfTransportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModeTransport.setAdapter(modeOfTransportAdapter);


        expenseAdapter = ArrayAdapter.createFromResource(MainActivity_B2C.this, R.array.type_of_expense_list, android.R.layout.simple_spinner_item);
        expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseType.setAdapter(expenseAdapter);

        getLatitudeLongitudeForCheckIn();


        btnChooseFileCheckIn.setOnClickListener(view -> {
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });

        checkinDialogBinding.etAttachmentsNameCHeckIN.setText(cameraImagePath);

        checkinDialogBinding.btnOpenCamera.setOnClickListener(view -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbBusiness:
                        linearBusinessTab.setVisibility(View.VISIBLE);
                        linearExpenseTab.setVisibility(View.GONE);

                        break;
                    case R.id.rbExpense:
                        linearBusinessTab.setVisibility(View.GONE);
                        linearExpenseTab.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        btnChooseFile.setOnClickListener(view -> {
// Create an intent to open the file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Set the file type to be picked

// Start the activity and wait for the user to pick a file
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });

        expenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expenseString = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                expenseString = expenseAdapter.getItem(0).toString();

            }
        });


        spinnerModeTransport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stringModeTypeOfTransport = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                stringModeTypeOfTransport = modeOfTransportAdapter.getItem(0).toString();

            }
        });


        lead_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLead addLead = new AddLead();
                startActivity(new Intent(MainActivity_B2C.this, addLead.getClass()));
            }
        });


        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bpType = adapterView.getItemAtPosition(i).toString();
                switch (i) {
                    case 0:
                        exist_view.setVisibility(view.VISIBLE);
                        bp_spinner.setVisibility(View.VISIBLE);
                        select_spinner.setVisibility(View.GONE);
                        lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.GONE);
                        createLead.setVisibility(View.GONE);
                        callBplistApi(bp_spinner, cp_spinner);
                        break;

                    case 1:
                        exist_view.setVisibility(view.GONE);
                        bp_spinner.setVisibility(View.GONE);
                        select_spinner.setVisibility(View.GONE);
                        //lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.VISIBLE);
                        lead_spinner.setVisibility(View.VISIBLE);
                        createLead.setVisibility(View.GONE);
                        callleadDialogApi(lead_spinner);
//                        callleadApi(bp_spinner, cp_spinner);

                        break;

                    case 2:
                    case 3:
                        exist_view.setVisibility(view.GONE);
                        bp_spinner.setVisibility(View.GONE);
                        select_spinner.setVisibility(View.GONE);
                        lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.GONE);
                        createLead.setVisibility(View.VISIBLE);

                        break;
                    case 5:
                        exist_view.setVisibility(view.GONE);
                        bp_spinner.setVisibility(View.GONE);
                        select_spinner.setVisibility(View.GONE);
                        lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.GONE);
                        createLead.setVisibility(View.GONE);
                        break;

                    case 4:
                        exist_view.setVisibility(view.GONE);
                        bp_spinner.setVisibility(View.GONE);
                        select_spinner.setVisibility(View.GONE);
                        lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        createLead.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.VISIBLE);
                        lead_spinner.setVisibility(View.VISIBLE);
                        callleadDialogApi(lead_spinner);

                        break;


                    case 6:
                        exist_view.setVisibility(view.GONE);
                        bp_spinner.setVisibility(View.GONE);
                        select_spinner.setVisibility(View.GONE);
                        lead_spinner.setVisibility(View.GONE);
                        cp_spinner.setVisibility(View.GONE);
                        newBusinessView.setVisibility(View.GONE);
                        contactPerson.setVisibility(View.GONE);
                        tendor_view.setVisibility(View.GONE);
                        lead_view_shubh.setVisibility(View.GONE);
                        createLead.setVisibility(View.GONE);

                        break;


                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                bpType = adapterView.getItemAtPosition(0).toString();
            }
        });


        createLead.setOnClickListener(view -> {
            AddLead addLead = new AddLead();
            startActivity(new Intent(MainActivity_B2C.this, addLead.getClass()));
        });

        prospectbp_spinner.setAdapter(new BPDropdownAdapter(MainActivity_B2C.this, filterprospect(AllitemsList)));

        lead_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                leadReSourceID = leadValueList.get(i).getId().toString();
                callleadDialogApi(lead_spinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        prospectbp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                callcontactpersonApi(prospectcp_spinner, filterprospect(AllitemsList).get(i).getCardCode());
                prospectbpReSourceID = filterprospect(AllitemsList).get(i).getCardCode();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                bpContactPerson = ContactEmployeesList.get(i).getFirstName();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        prospectcp_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                prospectContactPerson = ContactEmployeesList.get(i).getFirstName();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        select_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    selectsourceType = "Lead";
                    //   lead_view.setVisibility(View.VISIBLE);
                    lead_spinner.setVisibility(View.VISIBLE);
                    prospect_view.setVisibility(View.GONE);
                    prospectbp_spinner.setVisibility(View.GONE);
                    prospectcp_spinner.setVisibility(View.GONE);
                    callleadApi(bp_spinner, cp_spinner);

                } else {
                    selectsourceType = "Prospect";
                    lead_view.setVisibility(View.GONE);
                    lead_spinner.setVisibility(View.GONE);
                    prospect_view.setVisibility(View.VISIBLE);
                    prospectbp_spinner.setVisibility(View.VISIBLE);
                    prospectcp_spinner.setVisibility(View.VISIBLE);
                    callBplistApi(bp_spinner, cp_spinner);
                    // callBplistApi();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rbExpense.isChecked()) {
                    //   Toast.makeText(MainActivity_B2C.this, "expense", Toast.LENGTH_SHORT).show();
                    if (validation(cost, distance, attachmentName, expenseRemark, checkOutExpenseDialogBinding.etTripName)) {
                        mapData.setExpenseType(expenseString);
                        mapData.setExpenseCost(costSt);
                        mapData.setExpenseDistance(distanceSt);
                        mapData.setExpenseRemark(expenseRemarkSt);
                        mapData.setSourceType("");
                        mapData.setResourceId("");
                        mapData.setContactPerson("");
                        mapData.setExpenseAttach(attachentUri);
                        mapData.setAttach(attachentUri);
                        mapData.setRemark("");
                        mapData.setExpenseRemark(expenseRemarkSt);

                        getmyCurrentLocation(mapData);
                        dialog.dismiss();
                    }

//                    if (cost.getText().toString().isEmpty()){
//                        cost.setError("Can't Be Empty");
//                    }else if (distance.getText().toString().isEmpty()){
//                        distance.setError("Can't Be Empty");
//                    }
//                    else if (expenseRemark.getText().toString().isEmpty()){
//                        expenseRemark.setError("Can't Be Empty");
//                    }
//                    else {
//                        Toast.makeText(MainActivity_B2C.this, "inside expense", Toast.LENGTH_SHORT).show();
//
//                        mapData.setExpenseType(expenseString);
//                        mapData.setExpenseCost(costSt);
//                        mapData.setExpenseDistance(distanceSt);
//                        mapData.setExpenseRemark(expenseRemarkSt);
//                        mapData.setSourceType("");
//                        mapData.setResourceId("");
//                        mapData.setContactPerson("");
//                        mapData.setExpenseAttach("");
//                        getmyCurrentLocation(mapData);
//                        dialog.dismiss();
//                    }


                } else {
                    if (validateCheckIn(etNewAttachemnt, comment_value)) {
                        if (select_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setSourceType(selectsourceType);
                        } else {
                            mapData.setSourceType(sourceType);
                        }

                        if (lead_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setResourceId(leadReSourceID);
                        } else if (bp_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setResourceId(bpReSourceID);
                        } else if (prospectbp_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setResourceId(prospectbpReSourceID);
                        }

                        if (cp_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setContactPerson(bpContactPerson);
                        } else if (prospectbp_spinner.getVisibility() == View.VISIBLE) {
                            mapData.setContactPerson(prospectContactPerson);


                        } else {
                            mapData.setContactPerson("");
                        }


                        File imageFile = new File(picturePath);
                        String curr_date = Globals.curntDate;

                        Log.e("filePath>>>>>", "onCreate: " + picturePath);
                        Log.e("fileNAme>>>>>", "onCreate: " + imageFile.getName());
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("CheckInAttach", imageFile.getName(), requestBody);
                        RequestBody bptype = RequestBody.create(MediaType.parse("multipart/form-data"), bpType);
                        RequestBody bpName = RequestBody.create(MediaType.parse("multipart/form-data"), bpFullName);
                        RequestBody cardCode = RequestBody.create(MediaType.parse("multipart/form-data"), bpCardCode);
                        RequestBody salesPersonCode = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.SalesEmployeeCode, "1"));
                        RequestBody modeOfTransport = RequestBody.create(MediaType.parse("multipart/form-data"), stringModeTypeOfTransport);
                        RequestBody checkInDate = RequestBody.create(MediaType.parse("multipart/form-data"), Globals.getTodaysDatervrsfrmt());
                        RequestBody CheckInTime = RequestBody.create(MediaType.parse("multipart/form-data"), Globals.getTCurrentTime());
                        RequestBody checkInLat = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latAtCheckInMultipart));
                        RequestBody checkInLong = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longAtCheckInMultipart));
                        RequestBody checkinRemark = RequestBody.create(MediaType.parse("multipart/form-data"), comment_value.getText().toString());


                        Prefs.putString(Globals.BP_TYPE_CHECK_IN, bpType);
                        Prefs.putString(Globals.BP_NAME_CHECK_IN, bpFullName);
                        Prefs.putDouble(Globals.START_LAT, latAtCheckInMultipart);
                        Prefs.putDouble(Globals.START_LONG, longAtCheckInMultipart);
                        Prefs.putString(Globals.START_DATE, Globals.getTodaysDate());
                        Prefs.putString(Globals.MODE_OF_TRANSPORT, stringModeTypeOfTransport);

                        callCheckInApi(imagePart, bptype, bpName, cardCode, salesPersonCode, modeOfTransport, checkInDate, CheckInTime, checkInLat, checkInLong, checkinRemark);

                    } else {
                        Toasty.warning(MainActivity_B2C.this, "Please Fill document Carefully").show();
                    }
                }


            }
        });
        lead_view_shubh.setVisibility(View.GONE);
        dialog.show();
    }

    CheckOutExpenseDialogBinding checkOutExpenseDialogBinding;

    private void openChekOutDialog() {

        checkOutExpenseDialogBinding = CheckOutExpenseDialogBinding.inflate(getLayoutInflater());


        dialog = new Dialog(MainActivity_B2C.this);
        // dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setTitle("FollowUp FeedBack");
        dialog.setContentView(checkOutExpenseDialogBinding.getRoot());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        distanceMAP = distance(Prefs.getDouble(Globals.START_LAT, 0.0), Prefs.getDouble(Globals.START_LONG, 0.0), latAtCheckInMultipart, longAtCheckInMultipart);
        Log.e("TAG", "openChekOutDialog: " + Prefs.getDouble(Globals.START_LAT, 0.0) + Prefs.getDouble(Globals.START_LONG, 0.0) + latAtCheckInMultipart + longAtCheckInMultipart);

        checkOutExpenseDialogBinding.etCustomerType.setText(Prefs.getString(Globals.BP_TYPE_CHECK_IN, ""));
        checkOutExpenseDialogBinding.etCustomerName.setText(Prefs.getString(Globals.BP_NAME_CHECK_IN, ""));
        checkOutExpenseDialogBinding.etModeOfTransport.setText(Prefs.getString(Globals.MODE_OF_TRANSPORT, ""));


        DecimalFormat df = new DecimalFormat("#.##");

        Double inMetre = distanceMAP * 1000;
        String formattedValue = df.format(distanceMAP);
        //    editTextKm.setText();
        checkOutExpenseDialogBinding.etDistanceAuto.setText(formattedValue + " KM");

        checkOutExpenseDialogBinding.btnChooseFIle.setOnClickListener(view -> {
            Intent intent = new Intent();

            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_FILE_CHECKOUT_REQUEST_CODE);
        });

        checkOutExpenseDialogBinding.etAttachmentsNameOut.setText(attachmentcheckOut);
        checkOutExpenseDialogBinding.etTripId.setText(Prefs.getString(Globals.TRIP_ID, "1"));
        getLatitudeLongitudeForCheckIn();


        checkOutExpenseDialogBinding.add.setOnClickListener(view -> {
            if (validation(checkOutExpenseDialogBinding.etCostOfExpense, checkOutExpenseDialogBinding.etDistanceKm, checkOutExpenseDialogBinding.etAttachmentsNameOut, checkOutExpenseDialogBinding.commentValue, checkOutExpenseDialogBinding.etTripName)) {
                imageFile = new File(checkOUtPicturePath);
                String curr_date = Globals.curntDate;

                Log.e("filePath>>>>>", "onCreate: " + checkOUtPicturePath);
                Log.e("fileNAme>>>>>", "onCreate: " + imageFile.getName());

                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                MultipartBody.Part imagePart = MultipartBody.Part.createFormData("CheckOutAttach", imageFile.getName(), requestBody);
                RequestBody bptype = RequestBody.create(MediaType.parse("multipart/form-data"), bpType);
                RequestBody distanceAuto = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(distanceMAP));
                RequestBody bpName = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.etDistanceKm.getText().toString());
                RequestBody cardCode = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.TRIP_ID, "1"));
                RequestBody salesPersonCode = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.SalesEmployeeCode, "1"));
                RequestBody modeOfTransport = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.etCostOfExpense.getText().toString());
                RequestBody checkInDate = RequestBody.create(MediaType.parse("multipart/form-data"), Globals.getTodaysDatervrsfrmt());
                RequestBody CheckInTime = RequestBody.create(MediaType.parse("multipart/form-data"), Globals.getTCurrentTime());
                RequestBody checkInLat = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latAtCheckInMultipart));
                RequestBody checkInLong = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longAtCheckInMultipart));
                RequestBody checkinRemark = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.commentValue.getText().toString());


//            callCheckOutApi(imagePart, bptype, bpName, cardCode, salesPersonCode,
//                    checkInDate, CheckInTime, checkInLat, checkInLong, checkinRemark);

                callCheckOutApi(imagePart, distanceAuto, bpName, modeOfTransport, salesPersonCode, cardCode, checkInDate, CheckInTime, checkInLat, checkInLong, checkinRemark);
                dialog.dismiss();

            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }

        });

        dialog.show();


    }


    private boolean validation(EditText companyname, EditText full_name, EditText attachFile, EditText remarkExpense, EditText tripName) {

        if (companyname.length() == 0) {
            companyname.requestFocus();
            companyname.setError("Enter Cost");
            Toasty.warning(MainActivity_B2C.this, "Enter Cost ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (full_name.length() == 0) {
            full_name.requestFocus();
            full_name.setError("Enter Distance");
            Toasty.warning(MainActivity_B2C.this, "Enter Distance", Toast.LENGTH_SHORT).show();

            return false;
        } else if (attachFile.length() == 0) {
            attachFile.requestFocus();
            attachFile.setError("Choose File");
            Toasty.warning(MainActivity_B2C.this, "Choose File", Toast.LENGTH_SHORT).show();

            return false;
        } else if (remarkExpense.length() == 0) {
            attachFile.requestFocus();
            attachFile.setError("Enter Remark");
            Toasty.warning(MainActivity_B2C.this, "Enter Remark", Toast.LENGTH_SHORT).show();

            return false;
        } else if (tripName.length() == 0) {
            tripName.requestFocus();
            tripName.setError("Enter Trip Name");
            Toasty.warning(MainActivity_B2C.this, "Enter Trip Name", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;

    }

    private boolean validateCheckIn(EditText attachment, EditText remarks) {

        if (attachment.length() == 0) {
            attachment.requestFocus();
            attachment.setError("Please Attach Document");
            Toasty.warning(MainActivity_B2C.this, "Please Attach Document ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (remarks.length() == 0) {
            remarks.requestFocus();
            remarks.setError("Enter Remarks");
            Toasty.warning(MainActivity_B2C.this, "Enter Remarks", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;

    }


    private boolean validationExpense(EditText companyname, EditText full_name) {

        if (companyname.length() == 0) {
            companyname.requestFocus();
            companyname.setError("Enter Cost");
            Toasty.warning(MainActivity_B2C.this, "Enter Cost ", Toast.LENGTH_SHORT).show();
            return false;
        } else if (full_name.length() == 0) {
            full_name.requestFocus();
            full_name.setError("Enter Trip");
            Toasty.warning(MainActivity_B2C.this, "Enter Trip Name", Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;

    }


    @SuppressLint("MissingPermission")
    private void getmyCurrentLocation(MapData mapData) {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) MainActivity_B2C.this.getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {

                            e.printStackTrace();
                        }


                        String address = addresses != null ? addresses.get(0).getAddressLine(0) : ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                           /* String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();*/


                        callApi(location.getLatitude(), location.getLongitude(), mapData, address);


                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1 = locationResult.getLastLocation();

                                Geocoder geocoder;
                                List<Address> addresses = null;
                                geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                                callApi(location1.getLatitude(), location1.getLongitude(), mapData, address);
                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    private void callCheckInApi(MultipartBody.Part imagePart, RequestBody bptype, RequestBody bpFullName, RequestBody cardCode, RequestBody salesPersonCode, RequestBody modeOfTransport, RequestBody checkInDate, RequestBody CheckInTime, RequestBody checkInLat, RequestBody checkInLong, RequestBody checkinRemark) {
        Call<ResponseTripCheckIn> call = NewApiClient.getInstance().getApiService().tripCheckIn(imagePart, bptype, bpFullName, cardCode, salesPersonCode, modeOfTransport, checkInDate, CheckInTime, checkInLat, checkInLong, checkinRemark);

        call.enqueue(new Callback<ResponseTripCheckIn>() {
            @Override
            public void onResponse(Call<ResponseTripCheckIn> call, Response<ResponseTripCheckIn> response) {
                if (response != null) {

                    if (response.code() == 200) {

                        dialog.dismiss();
                        Prefs.putString(Globals.TRIP_ID, response.body().getData().get(0).getId());

                        Toast.makeText(MainActivity_B2C.this, "Success", Toast.LENGTH_SHORT).show();
                        if (locationtype.equalsIgnoreCase("Start")) {
                            if (Prefs.getString(Globals.CHECK_IN_STATUS, "CheckOut").equalsIgnoreCase("Stop")) {
                                Prefs.putString(Globals.CHECK_IN_STATUS, "Start");
                                toggleDefaultChecker = false;
                             /*   binding.toggleCheckIN.setChecked(true);
                                binding.headingcheckIn.setText("Check Out");
                                binding.slideView.setText(" Check Out");*/
                            } else {
                             /*   binding.toggleCheckIN.setChecked(false);
                                binding.headingcheckIn.setText("Check In");
                                binding.slideView.setText(" Check In");*/
                            }
                            toggleDefaultChecker = false;
                     /*       binding.toggleCheckIN.setChecked(true);
                            binding.headingcheckIn.setText("Check Out");
                            binding.slideView.setText("Check Out");*/
                            Log.e("success", "success");

                            picturePath = "";
                            attchmentName = "";
                        } else {
                         /*   binding.toggleCheckIN.setChecked(false);
                            binding.headingcheckIn.setText("Check In");
                            binding.slideView.setText(" Check In");*/
                            //  openExpenseDialog();
                        }


                    } else if (response.code() == 201) {

                        Toast.makeText(MainActivity_B2C.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity_B2C.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseTripCheckIn> call, Throwable t) {
                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FAILURE======>", "onFailure: " + t.getMessage());
            }
        });
    }


    private void callCheckOutApi(MultipartBody.Part imagePart, RequestBody totaldistanceAuto, RequestBody totalDistanceManual, RequestBody totalExpenses, RequestBody salesPersonCode, RequestBody id, RequestBody checkOutDate, RequestBody CheckOutTime, RequestBody checkOutLat, RequestBody checkOutLong, RequestBody checkOutRemark) {
        Call<ResponseTripCheckOut> call = NewApiClient.getInstance().getApiService().tripCheckOut(imagePart, totaldistanceAuto, totalDistanceManual, totalExpenses, salesPersonCode, id, checkOutDate, CheckOutTime, checkOutLat, checkOutLong, checkOutRemark);

        call.enqueue(new Callback<ResponseTripCheckOut>() {
            @Override
            public void onResponse(Call<ResponseTripCheckOut> call, Response<ResponseTripCheckOut> response) {
                if (response != null) {

                    if (response.code() == 200) {
                        Calendar calendar = Calendar.getInstance();

                        // Create date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                        // Format date and time
                        String date = dateFormat.format(calendar.getTime());
                        String time = timeFormat.format(calendar.getTime());

                        Log.e("filePath>>>>>", "onCreate: " + picturePath);
                        Log.e("fileNAme>>>>>", "onCreate: " + imageFile.getName());
                        costSt = checkOutExpenseDialogBinding.etCostOfExpense.getText().toString();
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("Attach", imageFile.getName(), requestBody);
                        RequestBody tripName = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.etTripId.getText().toString());
                        RequestBody cost = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.etCostOfExpense.getText().toString());
                        RequestBody typeOfExpense = RequestBody.create(MediaType.parse("multipart/form-data"), "Travel");
                        RequestBody travelDistance = RequestBody.create(MediaType.parse("multipart/form-data"), " ");
                        RequestBody endLat = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(latAtCheckInMultipart));
                        RequestBody endLong = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(longAtCheckInMultipart));
                        RequestBody startlat = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        RequestBody startlong = RequestBody.create(MediaType.parse("multipart/form-data"), " ");
                        RequestBody createDate = RequestBody.create(MediaType.parse("multipart/form-data"), date);
                        RequestBody createTime = RequestBody.create(MediaType.parse("multipart/form-data"), time);
                        RequestBody updateDate = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        RequestBody updateTime = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.TRIP_ID, ""));
                        RequestBody createBy = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.SalesEmployeeCode, ""));
                        RequestBody employeeId = RequestBody.create(MediaType.parse("multipart/form-data"), Prefs.getString(Globals.EmployeeID, ""));
                        RequestBody expenseFrom = RequestBody.create(MediaType.parse("multipart/form-data"), date);
                        RequestBody expenseTo = RequestBody.create(MediaType.parse("multipart/form-data"), date);
                        RequestBody remark = RequestBody.create(MediaType.parse("multipart/form-data"), checkOutExpenseDialogBinding.commentValue.getText().toString());

                        Call<ExpenseResponse> callExp = NewApiClient.getInstance().getApiService().expense_create_multipart(imagePart, id, tripName, typeOfExpense, expenseFrom, expenseTo, cost, createDate, createTime, createBy, updateDate, updateTime, remark, employeeId, startlat, startlong, endLat, endLong, travelDistance, id);
                        callExp.enqueue(new Callback<ExpenseResponse>() {
                            @Override
                            public void onResponse(Call<ExpenseResponse> call, Response<ExpenseResponse> response) {
                                if (response != null && response.code() == 200) {
                                    attachmentcheckOut = "";
                                    Toast.makeText(MainActivity_B2C.this, "Expense Added", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();

//                                if (Prefs.getString(Globals.CheckMode, "CheckOut").equalsIgnoreCase("CheckOut")) {
//                                    Prefs.putString(Globals.CheckMode, "CheckIn");
//
//                                    binding.slideView.setText("Slide to Check Out");
//                                } else {
//                                    Prefs.putString(Globals.CheckMode, "CheckOut");
//
//                                    binding.slideView.setText("Slide to Check In");
//                                }
//                                Log.e("success", "success");


                                }
                            }

                            @Override
                            public void onFailure(Call<ExpenseResponse> call, Throwable t) {

                            }
                        });


                        picturePath = "";
                        attchmentName = "";


                        Toast.makeText(MainActivity_B2C.this, "Success", Toast.LENGTH_SHORT).show();
                        if (locationtype.equalsIgnoreCase("Stop")) {
                            if (Prefs.getString(Globals.CHECK_IN_STATUS, "CheckOut").equalsIgnoreCase("Start")) {
                                Prefs.putString(Globals.CHECK_IN_STATUS, "Stop");
                            /*    binding.toggleCheckIN.setChecked(false);
                                binding.headingcheckIn.setText("Check In");

                                binding.slideView.setText(" Check In");*/
                            } else {
                           /*     binding.toggleCheckIN.setChecked(true);
                                binding.headingcheckIn.setText("Check Out");
                                binding.slideView.setText(" Check Out");*/
                            }
                  /*          binding.toggleCheckIN.setChecked(false);
                            binding.headingcheckIn.setText("Check In");
                            binding.slideView.setText("Check In");*/
                            Log.e("success", "success");
                        } else {
                          /*  binding.toggleCheckIN.setChecked(true);
                            binding.headingcheckIn.setText("Check Out");
                            binding.slideView.setText(" Check Out");*/
                            //  openExpenseDialog();
                        }


                    } else if (response.code() == 201) {
                        Toast.makeText(MainActivity_B2C.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity_B2C.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }

//                    if (response.body().getValue().size() > 0) {
//
//
//                        startlat = Double.parseDouble(response.body().getValue().get(0).getLat());
//                        startlong = Double.parseDouble(response.body().getValue().get(0).getLong());
//
//                        distanceMAP = distance(startlat, startlong, latitude, longitude);
//                    }
//
//                    if (locationtype.equalsIgnoreCase("Start")) {
//                        if (Prefs.getString(Globals.CHECK_IN_STATUS, "CheckOut").equalsIgnoreCase("Stop")) {
//                            Prefs.putString(Globals.CHECK_IN_STATUS, "Start");
//
//                            binding.slideView.setText(" Check Out");
//                        } else {
//                            Prefs.putString(Globals.CHECK_IN_STATUS, "Stop");
//
//                            binding.slideView.setText(" Check In");
//                        }
//                        Log.e("success", "success");
//                    } else {
//                        Prefs.putString(Globals.CHECK_IN_STATUS, "Stop");
//
//                        binding.slideView.setText(" Check In");
//                        //  openExpenseDialog();
//                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseTripCheckOut> call, Throwable t) {

            }
        });
    }


    double latAtCheckInMultipart;
    double longAtCheckInMultipart;

    @SuppressLint("MissingPermission")
    private void getLatitudeLongitudeForCheckIn() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) MainActivity_B2C.this.getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {

                            e.printStackTrace();
                        }


                        String address = addresses != null ? addresses.get(0).getAddressLine(0) : ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                           /* String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();*/
//                                callCheckInApi(imagePart, bptype, bpFullName,
//                                        cardCode, salesPersonCode, modeOfTransport,
//                                        checkInDate, CheckInTime,
//                                        location.getLatitude(), location.getLongitude(), checkinRemark);


//                                callApi(location
//                                        .getLatitude(), location
//                                        .getLongitude(), mapData, address);
                        latAtCheckInMultipart = location.getLatitude();
                        longAtCheckInMultipart = location.getLongitude();
                        Log.e("LAT AND LONG>>>>>", "onComplete: " + location.getLatitude() + "" + location.getLongitude());

                        //    Toast.makeText(MainActivity_B2C.this, "" + location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_SHORT).show();


                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1 = locationResult.getLastLocation();

                                Geocoder geocoder;
                                List<Address> addresses = null;
                                geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
//                                        callApi(location1
//                                                .getLatitude(), location1
//                                                .getLongitude(), mapData, address);
                                latAtCheckInMultipart = location1.getLatitude();
                                longAtCheckInMultipart = location1.getLongitude();

                                //   Toast.makeText(MainActivity_B2C.this, "" + location1.getLatitude() + "" + location1.getLongitude(), Toast.LENGTH_SHORT).show();

                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    @SuppressLint("MissingPermission")
    private void getmyCurrentLocationForExpense(MapData mapData) {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) MainActivity_B2C.this.getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {
                        // When location result is not
                        // null set latitude
                        Geocoder geocoder;
                        List<Address> addresses = null;
                        geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {

                            e.printStackTrace();
                        }


                        String address = addresses != null ? addresses.get(0).getAddressLine(0) : ""; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                           /* String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();*/


                        callExpenseTravelApi(location.getLatitude(), location.getLongitude(), mapData, address);


                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);

                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                // Initialize
                                // location
                                Location location1 = locationResult.getLastLocation();

                                Geocoder geocoder;
                                List<Address> addresses = null;
                                geocoder = new Geocoder(MainActivity_B2C.this, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                                callExpenseTravelApi(location1.getLatitude(), location1.getLongitude(), mapData, address);
                            }
                        };

                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    private List<BusinessPartnerData> filterprospect(ArrayList<BusinessPartnerData> allitemsList) {
        List<BusinessPartnerData> bde = new ArrayList<>();
        for (BusinessPartnerData bd : allitemsList) {
//if(bd.getUType().get(0).getType().equalsIgnoreCase("Prospect")){
//bde.add(bd);
//            }
        }
        return bde;
    }

    private ArrayList<ContactPersonData> ContactEmployeesList = new ArrayList<>();

    private void callcontactpersonApi(Spinner cp_spinner, String cardCode) {
        ContactPersonData contactPersonData = new ContactPersonData();
        contactPersonData.setCardCode(cardCode);
        //  binding.contentData.loader.loader.setVisibility(View.VISIBLE);
        Call<ContactPerson> call = NewApiClient.getInstance().getApiService().contactemplist(contactPersonData);
        call.enqueue(new Callback<ContactPerson>() {
            @Override
            public void onResponse(Call<ContactPerson> call, Response<ContactPerson> response) {
                //  binding.contentData.loader.loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        ContactEmployeesList.clear();
                        ContactEmployeesList.addAll(response.body().getData());
                        cp_spinner.setAdapter(new ContactPersonAdapter(MainActivity_B2C.this, ContactEmployeesList));
                    }


                } else {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(MainActivity_B2C.this, mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContactPerson> call, Throwable t) {
                // binding.contentData.loader.loader.setVisibility(View.GONE);
                Toast.makeText(MainActivity_B2C.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void openChooseExpenseDialog() {
        MapData mapData = new MapData();

        final Dialog dialog = new Dialog(MainActivity_B2C.this);
        // dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setTitle("FollowUp FeedBack");
        dialog.setContentView(R.layout.dialog_expense_type);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        Button btnSelectType = dialog.findViewById(R.id.btnSelectedExpense);
        RadioGroup radioGroup = dialog.findViewById(R.id.rgExpenseType);

        mapData.setRemark("");
        mapData.setExpenseType("");
        mapData.setExpenseCost("");
        mapData.setExpenseDistance("");
        mapData.setExpenseRemark("");
        mapData.setSourceType("");
        mapData.setResourceId("");
        mapData.setContactPerson("");
        mapData.setExpenseAttach("");
        mapData.setAttach("");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbHotel:
                        openExpenseDialog();
                        dialog.dismiss();

                        break;

                    case R.id.rbTravel:

                        getmyCurrentLocationForExpense(mapData);
                        Toast.makeText(MainActivity_B2C.this, "Your Trip has been started", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        break;

                    case R.id.rbCab:

                        getmyCurrentLocationForExpense(mapData);
                        Toast.makeText(MainActivity_B2C.this, "Your Trip has been started", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                        break;

                    case R.id.rbTransport:

                        getmyCurrentLocationForExpense(mapData);
                        Toast.makeText(MainActivity_B2C.this, "Your Trip has been started", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                        break;

                    case R.id.rbPublic:

                        openExpenseDialog();
                        dialog.dismiss();

                        break;


                }
            }
        });
        btnSelectType.setOnClickListener(view -> {

        });
        dialog.show();

    }


    private void openExpenseDialog() {


        final Dialog dialog = new Dialog(MainActivity_B2C.this);
        // dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        // dialog.setTitle("FollowUp FeedBack");
        dialog.setContentView(R.layout.expense_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Button add = dialog.findViewById(R.id.add);
        EditText comment_value = dialog.findViewById(R.id.comment_value);


        Spinner expenseType = dialog.findViewById(R.id.spinnerTypeOfExpense);
        //    EditText expenseRemark = dialog.findViewById(R.id.comment_value);
        EditText cost = dialog.findViewById(R.id.etCostOfExpense);
        attachmentName = dialog.findViewById(R.id.etAttachmentsName);
        EditText distance = dialog.findViewById(R.id.etDistanceOfExpense);

        LinearLayout expenseFrom = dialog.findViewById(R.id.postingDate);
        EditText editTextExpenseFrom = dialog.findViewById(R.id.posting_value);
        EditText editTextExpenseTo = dialog.findViewById(R.id.expense_to_date);
        EditText editTextKm = dialog.findViewById(R.id.etDistanceKm);
        LinearLayout expenseToDate = dialog.findViewById(R.id.expenseToDate);
        Button btnChooseFile = dialog.findViewById(R.id.btnChooseFIle);
        ArrayAdapter<CharSequence> expenseAdapter;

        DecimalFormat df = new DecimalFormat("#.###");

        Double inMetre = distanceMAP * 1000;
        String formattedValue = df.format(inMetre);
        editTextKm.setText(formattedValue + " M");


        expenseAdapter = ArrayAdapter.createFromResource(MainActivity_B2C.this, R.array.type_of_expense_list, android.R.layout.simple_spinner_item);
        expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseType.setAdapter(expenseAdapter);

        Calendar calendar = Calendar.getInstance();

        // Create date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Format date and time
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());


        btnChooseFile.setOnClickListener(view -> {
// Create an intent to open the file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Set the file type to be picked

// Start the activity and wait for the user to pick a file
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });


        expenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                expenseString = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                expenseString = expenseAdapter.getItem(0).toString();

            }
        });


        editTextExpenseFrom.setOnClickListener(view -> {

            Globals.selectDate(MainActivity_B2C.this, editTextExpenseFrom);
        });

        editTextExpenseTo.setOnClickListener(view -> {

            Globals.selectDate(MainActivity_B2C.this, editTextExpenseTo);
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationExpense(cost, distance)) {
                    costSt = cost.getText().toString();
                    distanceSt = distance.getText().toString();
                    ExpenseDataModel expense = new ExpenseDataModel();
                    expense.setAttach(attachentUri);
                    expense.setRemarks("");
                    expense.setTripName(distanceSt);
                    expense.setTypeOfExpense(expenseString);
                    expense.setTravelDistance(String.valueOf(distanceMAP));
                    expense.setStartLat(String.valueOf(startlat));
                    expense.setStartLong(String.valueOf(startlong));
                    expense.setEndLat("");
                    expense.setEndLong("");
                    expense.setExpenseFrom(editTextExpenseFrom.getText().toString());
                    expense.setExpenseTo(editTextExpenseTo.getText().toString());

                    expense.setTotalAmount(Integer.valueOf(costSt));
                    expense.setCreateDate(date);
                    expense.setCreateTime(time);
                    expense.setCreatedBy(Prefs.getString(Globals.SalesEmployeeCode, ""));
                    expense.setEmployeeId(Prefs.getString(Globals.EmployeeID, ""));

                    Call<ExpenseResponse> callExp = NewApiClient.getInstance().getApiService().expense_create(expense);
                    callExp.enqueue(new Callback<ExpenseResponse>() {
                        @Override
                        public void onResponse(Call<ExpenseResponse> call, Response<ExpenseResponse> response) {
                            if (response != null && response.code() == 200) {

                                Toast.makeText(MainActivity_B2C.this, "Expense Added", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();


                            }
                        }

                        @Override
                        public void onFailure(Call<ExpenseResponse> call, Throwable t) {

                        }
                    });
                }


            }
        });


        dialog.show();

    }


    String startDate = Globals.firstDateOfFinancialYear();
    String endDate = Globals.lastDateOfFinancialYear();


    public static final double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    public static double distance(double startLat, double startLong, double endLat, double endLong) {

        double latDistance = Math.toRadians(endLat - startLat);
        double lonDistance = Math.toRadians(endLong - startLong);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endLat)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return AVERAGE_RADIUS_OF_EARTH_KM * c;
    }


    private void opengraph() {

        List<String> dates = new ArrayList<>();
        dates.add("2020-01-25");
        dates.add("2020-02-25");
        dates.add("2020-03-25");
        dates.add("2020-04-25");
        dates.add("2020-05-25");
        dates.add("2020-06-25");
        dates.add("2020-07-25");
        dates.add("2020-08-25");
        dates.add("2020-09-25");
        dates.add("2020-10-25");
        dates.add("2020-11-25");
        dates.add("2020-12-25");
        List<Double> allAmounts = new ArrayList<>();
        allAmounts.add(1.2);
        allAmounts.add(2.9);
        allAmounts.add(3.4);
        allAmounts.add(2.2);
        allAmounts.add(1.5);
        allAmounts.add(3.8);
        allAmounts.add(4.2);
        allAmounts.add(5.7);
        allAmounts.add(4.5);
        allAmounts.add(2.2);
        allAmounts.add(1.1);
        allAmounts.add(0.9);

        //   binding.contentData.anyChartView.setTouchEnabled(true);
        //   binding.contentData.anyChartView.setPinchZoom(true);
      /*  XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(dates));
        renderData(dates, allAmounts);*/


    }

    public void renderData(List<String> dates, List<Double> allAmounts) {

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("1");
        xAxisLabel.add("7");
        xAxisLabel.add("14");
        xAxisLabel.add("21");
        xAxisLabel.add("28");
        xAxisLabel.add("30");

       /* XAxis xAxis = volumeReportChart.getXAxis();
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        //  xAxis.enableGridDashedLine(2f, 7f, 0f);
        xAxis.setAxisMaximum(13f);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        xAxis.setLabelCount(12, true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(7f);
        xAxis.setLabelRotationAngle(315f);

        xAxis.setValueFormatter(new ClaimsXAxisValueFormatter(dates));

        xAxis.setCenterAxisLabels(true);


        xAxis.setDrawLimitLinesBehindData(false);*/



     /*   LimitLine ll1 = new LimitLine(4f);
        ll1.setLineColor(getResources().getColor(R.color.safron_barChart));
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll1.setTextSize(10f);*/

        LimitLine ll2 = new LimitLine(35f, "");
        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTextColor(getResources().getColor(R.color.white));
        ll2.setLineColor(getResources().getColor(R.color.safron_barChart));

        //  xAxis.removeAllLimitLines();
//        xAxis.addLimitLine(ll1);
//        xAxis.addLimitLine(ll2);

        //   volumeReportChart.getAxisRight().setEnabled(false);
//
        //setData()-- allAmounts is data to display;
        setDataForWeeksWise(allAmounts);


    }

    private void givepermission() {
        Dexter.withActivity(MainActivity_B2C.this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    // do you work now
                    MainBaseActivity.boolean_permission = true;
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // permission is denied permenantly, navigate user to app settings
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }

        }).onSameThread().check();
    }

    private void setDataForWeeksWise(List<Double> amounts) {

        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(1, amounts.get(0).floatValue()));
        values.add(new BarEntry(2, amounts.get(1).floatValue()));
        values.add(new BarEntry(3, amounts.get(2).floatValue()));
        values.add(new BarEntry(4, amounts.get(3).floatValue()));
        values.add(new BarEntry(5, amounts.get(4).floatValue()));
        values.add(new BarEntry(6, amounts.get(5).floatValue()));
        values.add(new BarEntry(7, amounts.get(6).floatValue()));
        values.add(new BarEntry(8, amounts.get(7).floatValue()));
        values.add(new BarEntry(9, amounts.get(8).floatValue()));
        values.add(new BarEntry(10, amounts.get(9).floatValue()));
        values.add(new BarEntry(11, amounts.get(10).floatValue()));
        values.add(new BarEntry(12, amounts.get(11).floatValue()));


        BarDataSet set1;
  /*      if (volumeReportChart.getData() != null && volumeReportChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) volumeReportChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            volumeReportChart.getData().notifyDataChanged();
            volumeReportChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Total volume");

            //  set1.setDrawCircles(true);

            set1.setColor(getResources().getColor(R.color.yellow));
            //  set1.setCircleColor(getResources().getColor(R.color.yellow));
            //   set1.setLineWidth(2f);//line size
            //  set1.setCircleRadius(5f);
            set1.setValueTextColor(getResources().getColor(R.color.white));
            //    set1.setDrawCircleHole(true);
            set1.setValueTextSize(10f);
            //    set1.setDrawFilled(true);

            set1.setFormLineWidth(5f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(5.f);
            //   set1.setFillColor(Color.WHITE);
            set1.setDrawValues(true);
            //   set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData();

            volumeReportChart.setData(data);
            volumeReportChart.getAxisLeft().setDrawLabels(false);
            volumeReportChart.getAxisRight().setDrawLabels(false);
            //  volumeReportChart.getXAxis().setDrawLabels(false);
            volumeReportChart.getDescription().setEnabled(false);
            volumeReportChart.setDrawGridBackground(false);
            volumeReportChart.getLegend().setEnabled(false);
            volumeReportChart.animate();
        }*/


    }

    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void dashboardcounter() {

        SalesEmployeeItem salesEmployeeItem = new SalesEmployeeItem();
        salesEmployeeItem.setSalesEmployeeCode(Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<CounterResponse> call = NewApiClient.getInstance().getApiService().dashboardcounter(salesEmployeeItem);
        call.enqueue(new Callback<CounterResponse>() {
            @Override
            public void onResponse(Call<CounterResponse> call, Response<CounterResponse> response) {
                if (response != null) {


                }
            }

            @Override
            public void onFailure(Call<CounterResponse> call, Throwable t) {

            }
        });
    }


    private void callCountryApi() {
        Call<CountryResponse> call = NewApiClient.getInstance().getApiService().getCountryList();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.code() == 200) {
                    if (response.body().getData().size() > 0) {
                        Dashboard.countrylist.clear();
                        Dashboard.countrylist.addAll(response.body().getData());
                    }

                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                Toast.makeText(MainActivity_B2C.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        callrecentactivityapi();
        callrecent_5_order();
    }

    List<EventValue> recentlist = new ArrayList<>();
    List<QuotationItem> recenOrdertlist = new ArrayList<>();

    private void callrecentactivityapi() {
        SalesEmployeeItem si = new SalesEmployeeItem();
        Log.e("TAG", "callrecentactivityapi: " + Prefs.getString(Globals.EmployeeID, ""));
        si.setEmp(Prefs.getString(Globals.EmployeeID, ""));
        // Call<EventResponse> call = NewApiClient.getInstance().getApiService().getcalendardata(si);
        Call<EventResponse> call = NewApiClient.getInstance().getApiService().getrecentactivity(si);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.code() == 200) {
                    recentlist.clear();
                    recentlist.addAll(response.body().getData());
                    if (response.body().getData().size() == 0) {
                        EventValue eventValue = new EventValue();
                        eventValue.setTitle("No Activity");
                        eventValue.setCreateDate(" None ");
                        eventValue.setComment("No recent activity created.");
                        recentlist.add(eventValue);

                    }
                    adapter = new RecentAdapter(MainActivity_B2C.this, recentlist);
               /*     binding.contentData.cominguprecyvlerview.setLayoutManager(new LinearLayoutManager(MainActivity_B2C.this, RecyclerView.HORIZONTAL, false));
                    binding.contentData.recentrecyvlerview.setLayoutManager(new LinearLayoutManager(MainActivity_B2C.this, RecyclerView.HORIZONTAL, false));
                    binding.contentData.cominguprecyvlerview.setAdapter(adapter);
                    binding.contentData.recentrecyvlerview.setAdapter(adapter);*/

                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(MainActivity_B2C.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callrecent_5_order() {
        HashMap<String, String> hde = new HashMap<>();
        hde.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));
        Call<QuotationResponse> call = NewApiClient.getInstance().getApiService().top5order(hde);
        call.enqueue(new Callback<QuotationResponse>() {
            @Override
            public void onResponse(Call<QuotationResponse> call, Response<QuotationResponse> response) {
                if (response.code() == 200) {
                    recenOrdertlist.clear();
                    recenOrdertlist.addAll(response.body().getValue());
                    if (response.body().getValue().size() == 0) {
                        EventValue eventValue = new EventValue();
                        eventValue.setTitle("No Activity");
                        eventValue.setCreateDate(" None ");
                        eventValue.setComment("No recent activity created.");

                    }
                    RecentOrderAdapter adapter = new RecentOrderAdapter(MainActivity_B2C.this, recenOrdertlist);
                    //  binding.contentData.recentrecyvlerview.setLayoutManager(new LinearLayoutManager(MainActivity_B2C.this, RecyclerView.HORIZONTAL, false));
                    // binding.contentData.recentrecyvlerview.setAdapter(adapter);

                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onFailure(Call<QuotationResponse> call, Throwable t) {
                Toast.makeText(MainActivity_B2C.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)

                .commit();
        return true;
    }


    private void openpopup() {
// .addItem(new PowerMenuItem("New BP",R.drawable.ic_newbp, false)) // add an item.

        PowerMenu powerMenu = new PowerMenu.Builder(this).addItem(new PowerMenuItem("New Lead", R.drawable.ic_newlead, false)) // aad an item list.
                // .addItem(new PowerMenuItem("New Opportunity", R.drawable.ic_newopp, false)) // aad an item list.
                .addItem(new PowerMenuItem("New Contact", R.drawable.ic_newcontact, false)) // aad an item list.
                //  .addItem(new PowerMenuItem("New Quotation", R.drawable.ic_newtask, false)) // aad an item list.
                // .addItem(new PowerMenuItem("New Order", R.drawable.ic_new_event, false)) // aad an item list.
                .addItem(new PowerMenuItem("New Campaign", R.drawable.ic_note, false)) // aad an item list.
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black)).setTextGravity(Gravity.START).setTextSize(12).setTextTypeface(Typeface.createFromAsset(getAssets(), "poppins_regular.ttf")).setSelectedTextColor(Color.BLACK).setWidth(Globals.pxFromDp(this, 220f)).setMenuColor(Color.WHITE).setSelectedMenuColor(ContextCompat.getColor(this, R.color.colorPrimary)).build();
        //   powerMenu.showAsDropDown(binding.quickCreate);


        OnMenuItemClickListener<PowerMenuItem> onMenuItemClickListener = new OnMenuItemClickListener<PowerMenuItem>() {
            @Override
            public void onItemClick(int position, PowerMenuItem item) {
                powerMenu.setSelectedPosition(position); // change selected item

                switch (position) {

                    case 0:
                        Prefs.putString(Globals.BussinessPageType, "DashBoard");
                        startActivity(new Intent(MainActivity_B2C.this, AddLead.class));
                        break;
                    case 1:
                        Prefs.putString(Globals.SelectOpportnity, "Dashboard");
                        startActivity(new Intent(MainActivity_B2C.this, AddOpportunityActivity.class));
                        break;
                    case 2:
                        Intent intent = new Intent(MainActivity_B2C.this, AddContact.class);
                        Prefs.putString(Globals.AddContactPerson, "Dashboard");
                        startActivity(intent);
                        break;
                    case 3:
                        Prefs.putString(Globals.QuotationListing, "null");
                        Prefs.putBoolean(Globals.SelectQuotation, true);
                        Intent i = new Intent(MainActivity_B2C.this, AddQuotationAct.class);
                        startActivity(i);
                        break;
                    case 4:
                        Prefs.putString(Globals.FromQuotation, "Order");
                        startActivity(new Intent(MainActivity_B2C.this, AddOrderAct.class));
                        break;
                    case 5:
                        startActivity(new Intent(MainActivity_B2C.this, AddCampaign.class));
                        break;

                }

                powerMenu.dismiss();
            }
        };
        powerMenu.setOnMenuItemClickListener(onMenuItemClickListener);
    }


    public class ClaimsXAxisValueFormatter extends ValueFormatter {

        List<String> datesList;

        public ClaimsXAxisValueFormatter(List<String> arrayOfDates) {
            this.datesList = arrayOfDates;
        }


        @Override
        public String getAxisLabel(float value, AxisBase axis) {
/*
Depends on the position number on the X axis, we need to display the label, Here, this is the logic to convert the float value to integer so that I can get the value from array based on that integer and can convert it to the required value here, month and date as value. This is required for my data to show properly, you can customize according to your needs.
*/
            Integer position = Math.round(value);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM");

            if (value > 1 && value < 2) {
                position = 0;
            } else if (value > 2 && value < 3) {
                position = 1;
            } else if (value > 3 && value < 4) {
                position = 2;
            } else if (value > 4 && value < 5) {
                position = 3;
            } else if (value > 5 && value < 6) {
                position = 4;
            } else if (value > 6 && value < 7) {
                position = 5;
            } else if (value > 7 && value < 8) {
                position = 6;
            } else if (value > 8 && value < 9) {
                position = 7;
            } else if (value > 9 && value <= 10) {
                position = 8;
            }
            if (position < datesList.size())
                return sdf.format(new Date((Globals.getDateInMilliSeconds(datesList.get(position), "yyyy-MM-dd"))));
            return "";
        }
    }


    private static final int LOCATION_PERMISSION_CODE = 100;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }


    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                getAddressFromLocation(latitude, longitude);
            }
        } else {
            showGPSDisabledAlert();
        }
    }

    private void showGPSDisabledAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?").setCancelable(false).setPositiveButton("Enable GPS", (dialog, id) -> {
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        });
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);
            String city = address.getLocality();
            String state = address.getAdminArea();
            String country = address.getCountryName();
            String postalCode = address.getPostalCode();
            String knownName = address.getFeatureName();
            // Do something with the address information
            //Toast.makeText(this, addressLine, Toast.LENGTH_SHORT).show();
            callMapApi(latitude, longitude, locationtype, addressLine);


        }
    }


    private void callMapApi(double latitude, double longitude, String type, String address) {
        Log.d("TAG", "callMapApi: ");
        MapData mapData = new MapData();
        mapData.setEmp_Id(Prefs.getString(Globals.EmployeeID, ""));
        mapData.setEmp_Name(Prefs.getString(Globals.Employee_Name, ""));
        mapData.setLat(String.valueOf(latitude));
        mapData.setLong(String.valueOf(longitude));
        mapData.setUpdateDate(Globals.getTodaysDatervrsfrmt());
        mapData.setUpdateTime(Globals.getTCurrentTime());
        mapData.setAddress(address);
        mapData.setShape("meeting");
        mapData.setType(type);
        mapData.setRemark("");
        mapData.setResourceId("");
        mapData.setContactPerson("");
        mapData.setSourceType("");
        Call<MapResponse> call = NewApiClient.getInstance().getApiService().sendMaplatlong(mapData);
        call.enqueue(new Callback<MapResponse>() {
            @Override
            public void onResponse(Call<MapResponse> call, Response<MapResponse> response) {
                if (response != null) {
                    try {
                        if (response.isSuccessful()) {
                            Log.e("success", "success-Bhupi");
                            Log.d("response", "success-shubh");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<MapResponse> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the URI of the selected file
            Uri uri = data.getData();
            attachentUri = uri.toString();
            attchmentName = getFileName(uri);
            attachmentName.setText(attchmentName);
            etNewAttachemnt.setText(attchmentName);
            picturePath = getRealPathFromURI(uri);
            //  Toast.makeText(this, picturePath, Toast.LENGTH_SHORT).show();


            // Use the URI to access the file
            // ...
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (data.getData() != null) {
                Uri uri = data.getData();
                //  attachmentcheckOut=getFileName(uri);
                attachmentcheckOut = getRealPathFromURI(uri);
                checkinDialogBinding.etAttachmentsNameCHeckIN.setText(attachmentcheckOut);
            }

        } else if (requestCode == PICK_FILE_CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            //  attachmentcheckOut=getFileName(uri);
            attachmentcheckOut = getRealPathFromURIchekOut(uri);
            checkOutExpenseDialogBinding.etAttachmentsNameOut.setText(attachmentcheckOut);


        }
    }

    public String getRealPathFromURI(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            picturePath = filePath;
            result = filePath;

        }
        return result;

    }


    public String getRealPathFromURIchekOut(Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            checkOUtPicturePath = filePath;
            result = filePath;
            //  Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();

            // Do something with the file path, such as displaying it in a TextView
            // TextView filePathTextView = findViewById(R.id.file_path_text_view);
            //  filePathTextView.setText(filePath);
        }
        return result;

    }


    // Helper method to get the file name from the URI
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    /*********************** Graphs APIs**************************/
    public static List<BarEntry> Salesentries = new ArrayList<>();
    public static List<BarEntry> Receiptentries = new ArrayList<>();
    public static List<BarEntry> Receivableentries = new ArrayList<>();
    public static List<String> ReceivableentriesForMarker = new ArrayList<>();
    public static List<String> ReceivableentriesXaxis = new ArrayList<>();

    /********************** Sales Graph*************************/

    private void saleGraphApi() {

        HashMap obj = new HashMap<String, String>();
        obj.put("FromDate", "2023-04-01");
        obj.put("ToDate", "2024-03-31");
        obj.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));

        Call<SalesGraphResponse> call = NewApiClient.getInstance().getApiService().salesGraph(obj);
        call.enqueue(new Callback<SalesGraphResponse>() {
            @Override
            public void onResponse(Call<SalesGraphResponse> call, Response<SalesGraphResponse> response) {
                if (response != null) {
                    if (response.body().status == 200) {
                        if (response.body() != null && response.body().data.size() > 0)
                            Salesentries.clear();

                        for (int i = 0; i < response.body().data.size(); i++) {
                            Salesentries.add(new BarEntry(i, Float.parseFloat(response.body().data.get(i).getMonthlySales())));
                        }
                        // opengraph();

                    }

                    ReceiptGraphApi();
                }
            }

            @Override
            public void onFailure(Call<SalesGraphResponse> call, Throwable t) {

            }
        });
    }

    /********************** Receipt Graph*************************/

    private void ReceiptGraphApi() {

        HashMap obj = new HashMap<String, String>();
        obj.put("FromDate", "2023-04-01");
        obj.put("ToDate", "2024-03-31");
        obj.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));

        Call<SalesGraphResponse> call = NewApiClient.getInstance().getApiService().receiptGraph(obj);
        call.enqueue(new Callback<SalesGraphResponse>() {
            @Override
            public void onResponse(Call<SalesGraphResponse> call, Response<SalesGraphResponse> response) {
                if (response != null) {
                    if (response.body().status == 200) {
                        if (response.body() != null && response.body().data.size() > 0)
                            Receiptentries.clear();

                        for (int i = 0; i < response.body().data.size(); i++) {
                            Receiptentries.add(new BarEntry(i, Float.parseFloat(response.body().data.get(i).getMonthlySales())));
                        }
                        // opengraph();


                    }

                    ReceivableGraphApi();
                }
            }

            @Override
            public void onFailure(Call<SalesGraphResponse> call, Throwable t) {

            }
        });
    }

    /********************** Receivable Graph*************************/

    private void ReceivableGraphApi() {

       /* {
            "FromDate":"2023-01-01",
                "ToDate":"2023-12-31",
                "SalesPersonCode":-1
        }*/


        HashMap obj = new HashMap<String, String>();
        obj.put("FromDate", Globals.firstDateOfFinancialYear());
        obj.put("ToDate", Globals.lastDateOfFinancialYear());
        obj.put("SalesPersonCode", Prefs.getString(Globals.SalesEmployeeCode, ""));

        Call<ResponseReceivableGraph> call = NewApiClient.getInstance().getApiService().receivableDueMonthGraph(obj);
        call.enqueue(new Callback<ResponseReceivableGraph>() {
            @Override
            public void onResponse(Call<ResponseReceivableGraph> call, Response<ResponseReceivableGraph> response) {
                if (response != null) {
                    if (response.body().status == 200) {
                        if (response.body() != null && response.body().data.size() > 0)
                            Receivableentries.clear();
                        ReceivableentriesXaxis.clear();
                        ReceivableentriesForMarker.clear();
                        for (int i = 0; i < response.body().data.size(); i++) {
                            ArrayList<String> daysGroup = new ArrayList<>();
                           /* if (response.body().data.get(i).getOverDueDaysGroup().equalsIgnoreCase("")) {

                            }*/
                            ReceivableentriesXaxis.add(response.body().getData().get(i).getOverDueDaysGroup());
                            Receivableentries.add(new BarEntry(i, (float) response.body().getData().get(i).getTotalDue()));
                            ReceivableentriesForMarker.add(String.valueOf(Globals.convertToLakhAndCroreFromString(response.body().data.get(i).getTotalDue())));

                        }
                        // opengraph();

                    }

                    setupChartViewPgaer();
                }
            }

            @Override
            public void onFailure(Call<ResponseReceivableGraph> call, Throwable t) {

            }
        });
    }

}
