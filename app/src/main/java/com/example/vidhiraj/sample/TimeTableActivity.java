package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Lenovo on 11/30/2016.
 */

public class TimeTableActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    RecyclerView.Adapter mAdapter;
    RecyclerView mRecyclerView;
    ProgressDialog mProgress;
    ArrayList<String> weekDays;
    String weekDay;
    List<TimeTableData> timeTableDatas = new ArrayList<>();
    static JSONObject timetableObj = null;
    JSONArray timeTableArray;
    JSONObject jsonArr = new JSONObject();
    TextView timetableData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.timetable_activity);
        super.onCreate(savedInstanceState);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        timetableData = (TextView) findViewById(R.id.time_nodata);

        // Setting adapter
        setmRAdapter(TimeTableActivity.this, "TimeTable Page");

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
        mProgress.show();
        if (Utils.isConnected(getApplicationContext())) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, timetableURL, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            mProgress.dismiss();
                            timetableObj = response.getJSONObject("timetable");
                            setJsonArray(timetableObj);
                            timeTableArray = timetableObj.getJSONArray(weekDay);
                            if (timeTableArray.length() == 0) {
                                timetableData.setVisibility(View.VISIBLE);
                            } else {
                                timetableData.setVisibility(View.GONE);
                                for (int i = 0; i < timeTableArray.length(); i++) {
                                    JSONObject dayDataObj = timeTableArray.getJSONObject(i);
                                    TimeTableData timeTable = new TimeTableData(dayDataObj.getInt("id"),
                                            dayDataObj.getString("name"),
                                            dayDataObj.getString("subject"),
                                            dayDataObj.getString("class_room"),
                                            dayDataObj.getString("start_time"),
                                            dayDataObj.getString("end_time"));
                                    timeTableDatas.add(timeTable);
                                }

                                mRecyclerView = (RecyclerView) findViewById(R.id.tt_recycler_view);
                                mRecyclerView.setHasFixedSize(false);
                                mRecyclerView.setNestedScrollingEnabled(false);
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
                            mProgress.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == 401) {
                                Intent intent = new Intent(TimeTableActivity.this, AndroidSpinnerExampleActivity.class);
                                startActivity(intent);
                            } else {
                                mProgress.dismiss();
                                Toast.makeText(getBaseContext(), "Please Try Again", Toast.LENGTH_LONG).show();

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
        } else {
            mProgress.dismiss();
            Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void setJsonArray(JSONObject jsonObject) {
        jsonArr = jsonObject;
    }

    public JSONObject getJsonArray() {
        return jsonArr;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String timeday = null;
        for (int j = position; j <= position; j++) {
            timeday = weekDays.get(j);
            Log.e("for chap_id", String.valueOf(timeday));
        }
        try {
            timeTableDatas = new ArrayList<>();
            timetableObj = getJsonArray();
            if (timetableObj.has(timeday)) {
                timetableData.setVisibility(View.GONE);
                timeTableArray = timetableObj.getJSONArray(timeday);
                Log.e("obj", String.valueOf(timetableObj));
                Log.e("timearray is", String.valueOf(timeTableArray));
                for (int i = 0; i < timeTableArray.length(); i++) {
                    JSONObject dayDataObj = timeTableArray.getJSONObject(i);
                    TimeTableData timeTable = new TimeTableData(dayDataObj.getInt("id"),
                            dayDataObj.getString("class_name"),
                            dayDataObj.getString("subject"),
                            dayDataObj.getString("sub_class_name"),
                            dayDataObj.getString("start_time"),
                            dayDataObj.getString("end_time"));
                    timeTableDatas.add(timeTable);
                }
            } else {
                timetableData.setVisibility(View.VISIBLE);
            }
            mRecyclerView = (RecyclerView) findViewById(R.id.tt_recycler_view);
            mRecyclerView.setHasFixedSize(false);
            mRecyclerView.setNestedScrollingEnabled(false);
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
    public void onBackPressed() {
        if (super.Drawer.isDrawerOpen(GravityCompat.START)) {
            super.Drawer.closeDrawer(GravityCompat.START);
        } else {
            super.Drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
