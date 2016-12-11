package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.vidhiraj.sample.AndroidSpinnerExampleActivity.MY_PREFS_NAME;

/**
 * Created by Lenovo on 11/30/2016.
 */

public class TimeTableActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    List<Integer> chapter_array = new ArrayList<Integer>();
    String classid;
    Button createCatalog, cancelCatalog;
    LinearLayout linearpoints;
    Integer chapter_id = null;
    RecyclerView mRecyclerView;
    ProgressDialog mProgress;
    TextView date_selected;
    int day, month, year;
    ArrayList<String> weekDays;
    String TITLES[] = {"Home", "Daily Catalog", "Students", "TimeTable", "Off Classes", "Logout"};
    int ICONS[] = {R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos};
    String org = null;
    int PROFILE = R.drawable.ic_photos;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    RecyclerView mDrawerRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mDrawerAdapter;
    // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManagers;
    private LinearLayoutManager mLayoutManager;
    // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ActionBarDrawerToggle mDrawerToggle;
    String url_icon;
    String weekDay;
    private GoogleApiClient client;
    List<TimeTableData> timeTableDatas = new ArrayList<>();
    static JSONObject timetableObj = null;
    JSONArray timeTableArray;
    JSONObject arrrrr = new JSONObject();
    TextView timetableData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String user_email = prefs.getString("email", null);
        org = prefs.getString("specificorg", null);
        url_icon = prefs.getString("org_icon", null);
        timetableData = (TextView) findViewById(R.id.time_nodata);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(org);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new EraMyAdapter(TimeTableActivity.this, TITLES, ICONS, org, user_email, url_icon);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        weekDays = new ArrayList<>();
        weekDays.add("Monday");
        weekDays.add("Tuesday");
        weekDays.add("Wednesday");
        weekDays.add("Thursday");
        weekDays.add("Friday");
        weekDays.add("Saturday");
        weekDays.add("Sunday");
        SimpleDateFormat ffff = new SimpleDateFormat("EEEE");
        Date d = new Date();
        weekDay = ffff.format(d);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, weekDays);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        int selectedDay = dataAdapter.getPosition(weekDay);
        spinner.setSelection(selectedDay);


        String timetableURL = ApiKeyConstant.apiUrl + "/api/v1/time_tables/get_time_tables.json";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, timetableURL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        Log.e("ssss", "sss");
                        timetableObj = response.getJSONObject("timetable");
                        setJsonArray(timetableObj);
                        timeTableArray = timetableObj.getJSONArray(weekDay);
                        if (timeTableArray.length() == 0) {
                            timetableData.setVisibility(View.VISIBLE);
                        } else {
                            timetableData.setVisibility(View.GONE);
                            Log.e("timearray is", String.valueOf(timeTableArray));
                            for (int i = 0; i < timeTableArray.length(); i++) {
                                JSONObject dayDataObj = timeTableArray.getJSONObject(i);
                                TimeTableData timeTable = new TimeTableData(dayDataObj.getString("name"), dayDataObj.getString("start_time"), dayDataObj.getString("end_time"), dayDataObj.getString("class_room"), dayDataObj.getString("subject"));
                                timeTableDatas.add(timeTable);
                            }


                            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
                            mRecyclerView.setHasFixedSize(true);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(TimeTableActivity.this));
                            mAdapter = new TimeTableAdapter(timeTableDatas);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.invalidate();
                        }
                    }

                } catch (JSONException e) {
                    String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();
                    Log.e("sdcard-err2:", err);
                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.statusCode == 401) {
                            Intent intent = new Intent(TimeTableActivity.this, AndroidSpinnerExampleActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e("Volley", "Error");
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", ApiKeyConstant.authToken);
                return headers;
            }
        };
        VolleyControl.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void setJsonArray(JSONObject jsonObject) {
        arrrrr = jsonObject;
    }

    public JSONObject getJsonArray() {
        return arrrrr;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String timedayyyy = null;
        for (int j = position; j <= position; j++) {
            timedayyyy = weekDays.get(j);
            Log.e("for chap_id", String.valueOf(timedayyyy));
        }
        try {
            timeTableDatas = new ArrayList<>();
            timetableObj = getJsonArray();
            if (timetableObj.has(timedayyyy)) {
                timetableData.setVisibility(View.GONE);
                timeTableArray = timetableObj.getJSONArray(timedayyyy);
                Log.e("obj", String.valueOf(timetableObj));
                Log.e("timearray is", String.valueOf(timeTableArray));
                for (int i = 0; i < timeTableArray.length(); i++) {
                    JSONObject dayDataObj = timeTableArray.getJSONObject(i);
                    TimeTableData timeTable = new TimeTableData(dayDataObj.getString("class_name"), dayDataObj.getString("start_time"), dayDataObj.getString("end_time"), dayDataObj.getString("sub_class_name"), dayDataObj.getString("subject"));
                    timeTableDatas.add(timeTable);
                }
            }
            else {
                timetableData.setVisibility(View.VISIBLE);
            }
            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(TimeTableActivity.this));
            mAdapter = new TimeTableAdapter(timeTableDatas);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
