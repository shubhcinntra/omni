package com.cinntra.ledger.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.ledger.R;
import com.cinntra.ledger.adapters.SelectedEmployeeForEventAdapter;
import com.cinntra.ledger.globals.Globals;
import com.cinntra.ledger.model.EventResponse;
import com.cinntra.ledger.model.EventValue;
import com.cinntra.ledger.model.QuotationResponse;
import com.cinntra.ledger.model.SaleEmployeeResponse;
import com.cinntra.ledger.model.SalesEmployeeItem;
import com.cinntra.ledger.receivers.NotificationPublisher;
import com.cinntra.ledger.webservices.NewApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEventDialogue extends DialogFragment implements View.OnClickListener, SelectedEmployeeForEventAdapter.OnItemClickListener {

    @BindView(R.id.head_title)
    TextView head_title;
    @BindView(R.id.back_press)
    RelativeLayout back_press;
    TextView pass_date;
    @BindView(R.id.from_value)
    EditText from_date_value;
    @BindView(R.id.related_document_value)
    EditText related_document_value;
    @BindView(R.id.description_text)
    EditText description_text;
    @BindView(R.id.host_text)
    AutoCompleteTextView host_text;
    @BindView(R.id.title_text)
    EditText title_text;
    @BindView(R.id.add_location_text)
    EditText add_location_text;
    @BindView(R.id.participant_value)
    AutoCompleteTextView participant_value;
    @BindView(R.id.colorSpinner_view)
    LinearLayout colorSpinner_view;
    @BindView(R.id.spinnerview)
    LinearLayout spinnerview;
    @BindView(R.id.to_view)
    RelativeLayout to_view;
    @BindView(R.id.from_view)
    RelativeLayout from_view;
    @BindView(R.id.to_value)
    EditText to_date_value;
    @BindView(R.id.submit_button)
    Button submit;
    @BindView(R.id.upload_button)
    Button upload_button;
    @BindView(R.id.simple_switch)
    Switch simple_switch;
    @BindView(R.id.spinner)
    Spinner preority_spinner;
    @BindView(R.id.color_spinner)
    Spinner colorspin;
    @BindView(R.id.time_view)
    RelativeLayout time_view;
    @BindView(R.id.time_value)
    TextView time_value;
    @BindView(R.id.add)
    ImageView add;
    @BindView(R.id.loader)
    ProgressBar loader;
//    @BindView(R.id.autoCompleteTextView)
//    AutoCompleteTextView autoCompleteTextView;

    @BindView(R.id.rvChipItem)
    RecyclerView rvChipItem;

    @BindView(R.id.rvHost)
    RecyclerView rvHost;


    String priority = "";
    String allday = "";
    String repeated = "";
    int t1hr, t1min;
    EventPrerioritySpinner eventPrerioritySpinner;
    EventTextSpinner eventTextSpinner;


    ArrayList<String> categories = new ArrayList<>();
    ArrayList<Integer> circleimage = new ArrayList<>();
    final Calendar myCalendar = Calendar.getInstance();

    private AlarmManager alarmManager;
    private Calendar myTime;

    ArrayList<String> selectedDataList;
    ArrayList<String> selectedhostDataList;
    ArrayList<SalesEmployeeItem> yourSuggestionsList;

    SelectedEmployeeForEventAdapter adapterEmp,adapterhost;

    LinearLayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;

    public AddEventDialogue() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddEventDialogue newInstance(String title) {
        AddEventDialogue frag = new AddEventDialogue();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container);
        ButterKnife.bind(this, v);
        setDefaults();

        simple_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    allday = "All day";
                } else {
                    allday = "One day";
                }
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        head_title.setText("New Event");
        back_press.setOnClickListener(this);
        submit.setOnClickListener(this);

        selectedDataList = new ArrayList<>();
        selectedhostDataList = new ArrayList<>();
        yourSuggestionsList = new ArrayList<>();
        getAllEmployee();

//        yourSuggestionsList.add("SHubham");
//        yourSuggestionsList.add("Ankit");
//        yourSuggestionsList.add("ankur");
//        yourSuggestionsList.add("bhuppi");


    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_press)
            getDialog().dismiss();
        else if (v.getId() == R.id.submit_button) {
            String title = title_text.getText().toString().trim();
            String fromDate = from_date_value.getText().toString().trim();
            String toDate = to_date_value.getText().toString().trim();
            String location = add_location_text.getText().toString().trim();
            String host = host_text.getText().toString().trim();
            String partcipant = participant_value.getText().toString().trim();
            String desc = description_text.getText().toString().trim();
            String related = related_document_value.getText().toString().trim();
            String time = time_value.getText().toString().trim();

            if (validation(title, fromDate, toDate, location, host, partcipant, desc, related, time)) {
                EventValue eventValue = new EventValue();
                eventValue.setOppId(0);
                eventValue.setTitle(title);
                eventValue.setDescription(desc);
                eventValue.setFrom(fromDate);
                eventValue.setTo(toDate);
                eventValue.setEmp(Integer.parseInt(Prefs.getString(Globals.EmployeeID, "")));
                eventValue.setCreateTime(Globals.getTCurrentTime());
                eventValue.setCreateDate(Globals.getTodaysDatervrsfrmt());
                eventValue.setType("Event");
                eventValue.setSourceType("");
                eventValue.setParticipants(partcipant);
                eventValue.setComment("");
                eventValue.setSubject("");
                eventValue.setTime(Globals.getTCurrentTime());
                eventValue.setDocument("");
                eventValue.setRelatedTo(related);
                eventValue.setLocation(location);
                eventValue.setHost(host);
                eventValue.setAllday(allday);
                eventValue.setName("");
                eventValue.setLeadType("Event");
                eventValue.setProgressStatus("WIP");
                eventValue.setPriority(priority);
                eventValue.setRepeated(repeated);


                if (Globals.checkInternet(getContext())) {
                    loader.setVisibility(View.VISIBLE);
                    callApi(eventValue);
                }
            }

        }
    }

    private void callApi(EventValue eventValue) {

        Call<EventResponse> call = NewApiClient.getInstance().getApiService().createnewevent(eventValue);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.code() == 200) {
                    loader.setVisibility(View.GONE);

                    Toasty.success(getContext(), "Add Successfully", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();


                } else {
                    loader.setVisibility(View.GONE);

                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s = response.errorBody().string();
                        mError = gson.fromJson(s, QuotationResponse.class);
                        Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Toasty.error(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation(String title, String fromDate, String toDate, String location,
                               String host, String partcipant, String desc, String related, String time) {
        if (title.isEmpty()) {
            title_text.setError(getResources().getString(R.string.title_error));
            return false;
        } else if (fromDate.isEmpty()) {
            from_date_value.setError(getResources().getString(R.string.fromdate_error));
            return false;
        } else if (toDate.isEmpty()) {
            to_date_value.setError(getResources().getString(R.string.todate_error));
            return false;
        } else if (location.isEmpty()) {
            add_location_text.setError(getResources().getString(R.string.location_error));
            return false;
        } else if (host.isEmpty()) {
            host_text.setError(getResources().getString(R.string.host_error));
            return false;
        } else if (partcipant.isEmpty()) {
            participant_value.setError(getResources().getString(R.string.participant_error));
            return false;
        } else if (desc.isEmpty()) {
            description_text.setError(getResources().getString(R.string.description_error));
            return false;
        } else if (time.isEmpty()) {
            time_value.setError(getResources().getString(R.string.time_error));
            return false;
        }
        return true;
    }

    private void setDefaults() {
        add.setVisibility(View.GONE);
        circleimage.add(R.drawable.red_dot);
        circleimage.add(R.drawable.ic_green_dot);
        circleimage.add(R.drawable.yellow_dot);

        categories.add("Daily");

        categories.add("Weekly");
        categories.add("Monthly");


        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(pass_date, myCalendar);
        };

        colorspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    priority = "high";
                } else if (position == 1) {
                    priority = "medium";
                } else {
                    priority = "low";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority = "high";
            }
        });

        eventPrerioritySpinner = new EventPrerioritySpinner(getActivity(), circleimage);
        colorspin.setAdapter(eventPrerioritySpinner);
        colorspin.setDropDownVerticalOffset(120);

        eventTextSpinner = new EventTextSpinner(getActivity(), categories);
        preority_spinner.setAdapter(eventTextSpinner);
        preority_spinner.setDropDownVerticalOffset(120);

        preority_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeated = preority_spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                repeated = categories.get(0);
            }
        });

        from_date_value.setOnClickListener(v -> {
           /* from_date_value.getText().toString();
            if (!from_date_value.getText().toString().isEmpty()) {
                String value = from_date_value.getText().toString();
                String[] dd = value.split("-");
                int y = Integer.parseInt(dd[2]);
                int m = Integer.parseInt(dd[1])-1;
                int d = Integer.parseInt(dd[0]);
                myCalendar.set(Calendar.DAY_OF_MONTH, y);
                myCalendar.set(Calendar.MONTH, m);
                myCalendar.set(Calendar.YEAR, d);
                new DatePickerDialog(getActivity(), date, d, m, y).show();
            } else {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }*/
            Globals.selectDate(getContext(), from_date_value);


        });

        to_date_value.setOnClickListener(v -> {
            /*if (to_date_value.getText().toString() != null && !to_date_value.getText().toString().isEmpty()) {
                String value = to_date_value.getText().toString();
                String[] dd = value.split("-");
                int y = Integer.parseInt(dd[2]);
                int m = Integer.parseInt(dd[1])-1;
                int d = Integer.parseInt(dd[0]);
                myCalendar.set(Calendar.DAY_OF_MONTH, y);
                myCalendar.set(Calendar.MONTH, m);
                myCalendar.set(Calendar.YEAR, d);
                new DatePickerDialog(getActivity(), date, d, m, y).show();

            } else {
                new DatePickerDialog(getActivity(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }*/
            Globals.selectDate(getContext(), to_date_value);


        });


        time_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        t1hr = hourOfDay;
                        t1min = minute;
                        myTime = Calendar.getInstance();
//                        myTime.set(0,0,0,t1hr,t1min);
                        myTime.set(Calendar.HOUR_OF_DAY, t1hr);
                        myTime.set(Calendar.MINUTE, t1min);
                        myTime.set(Calendar.SECOND, 0);
                        myTime.set(Calendar.MILLISECOND, 0);
                        time_value.setText(DateFormat.format("hh:mm aa", myTime));
                        setAlarm();
                    }
                }, 12, 0, false
                );
                timePickerDialog.updateTime(t1hr, t1min);
                timePickerDialog.setMessage(time_value.getHint().toString());
                timePickerDialog.show();

            }

        });


    }

    private void updateLabel(TextView pass_date, Calendar myCalendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        pass_date.setText(sdf.format(myCalendar.getTime()));
    }


    private void setAlarm() {

        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getContext(), NotificationPublisher.class);
        i.putExtra("value", getActivity().getResources().getString(R.string.meeting_notification));
        i.putExtra("title", getActivity().getResources().getString(R.string.meeting));
        final int id = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), id, i, PendingIntent.FLAG_MUTABLE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, myTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }


    @Override
    public void onItemClick(String item) {
        selectedDataList.remove(item);
        adapterEmp.notifyDataSetChanged();

    }


    private void getAllEmployee() {

        Call<SaleEmployeeResponse> call = NewApiClient.getInstance().getApiService().getAllEmployee();
        call.enqueue(new Callback<SaleEmployeeResponse>() {
            @Override
            public void onResponse(Call<SaleEmployeeResponse> call, Response<SaleEmployeeResponse> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getValue().size() > 0) {
                            yourSuggestionsList.clear();
                            yourSuggestionsList.addAll(response.body().getValue());
                            List<String> itemNames = new ArrayList<>();
                            //  List<String> itemCode = new ArrayList<>();
                            for (SalesEmployeeItem item : yourSuggestionsList) {
                                itemNames.add(item.getSalesEmployeeName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemNames);

                            participant_value.setAdapter(adapter);

                            host_text.setAdapter(adapter);

                            host_text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedData = (String) parent.getItemAtPosition(position);

                                    if (selectedData.isEmpty()){
                                        rvHost.setVisibility(View.GONE);
                                    }else {
                                        rvHost.setVisibility(View.VISIBLE);
                                    }


                                    if (!selectedData.isEmpty() && !selectedhostDataList.contains(selectedData)) {
                                        //   rvChipItem.setVisibility(View.VISIBLE);
                                        selectedhostDataList.add(selectedData);
                                        adapter.notifyDataSetChanged();
                                        Log.e("selected", "onItemClick: " + selectedDataList.size());

                                        gridLayoutManager = new GridLayoutManager(requireActivity(),3);
                                        adapterhost = new SelectedEmployeeForEventAdapter(requireContext(), selectedhostDataList);
                                        rvHost.setLayoutManager(gridLayoutManager);
                                        rvHost.setAdapter(adapterhost);
                                        adapterhost.notifyDataSetChanged();
                                        adapter.notifyDataSetChanged();


                                    }

                                    // Clear the AutoCompleteTextView after selecting the item
                                    host_text.getText().clear();
                                }
                            });


                            participant_value.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedData = (String) parent.getItemAtPosition(position);

                                    if (selectedData.isEmpty()){
                                        rvChipItem.setVisibility(View.GONE);
                                    }else {
                                        rvChipItem.setVisibility(View.VISIBLE);
                                    }


                                    if (!selectedData.isEmpty() && !selectedDataList.contains(selectedData)) {
                                     //   rvChipItem.setVisibility(View.VISIBLE);
                                        selectedDataList.add(selectedData);
                                        adapter.notifyDataSetChanged();
                                        Log.e("selected", "onItemClick: " + selectedDataList.size());

                                        gridLayoutManager = new GridLayoutManager(requireActivity(),3);
                                        adapterEmp = new SelectedEmployeeForEventAdapter(requireContext(), selectedDataList);
                                        rvChipItem.setLayoutManager(gridLayoutManager);
                                        rvChipItem.setAdapter(adapterEmp);
                                        adapterEmp.notifyDataSetChanged();
                                        adapter.notifyDataSetChanged();


                                    }

                                    // Clear the AutoCompleteTextView after selecting the item
                                    participant_value.getText().clear();
                                }
                            });

                        }
//                        AllItemList.clear();
//                        AllItemList.addAll(response.body().getCustomerLedgerRes());
//                        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
//                        SoldItem_Adapter adapter = new SoldItem_Adapter(getContext(), AllItemList, LedgerCutomerDetails.nameCustomer);
//                        recyclerview.setLayoutManager(layoutManager);
//                        recyclerview.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();

                    }

                }
            }

            @Override
            public void onFailure(Call<SaleEmployeeResponse> call, Throwable t) {

            }
        });
    }


}
