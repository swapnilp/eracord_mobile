package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.widget.DatePicker;
import android.widget.EditText;

public class DailyTeachingActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    List<Integer> chapter_array = new ArrayList<Integer>();
    String classid;
    Button createCatalog;
    LinearLayout linearpoints;
    Integer chapter_id = null;
    RecyclerView m_points_RecyclerView;
    ProgressDialog mProgress;
    int day, month, year;
    ActionBarDrawerToggle mDrawerToggle;
    EditText dateText;
    String selected_date;

    private DatePicker datePicker;
    private Calendar calendar;

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    selected_date = arg1 + "-" + (arg2 + 1) + "-" + arg3;
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_daily_teaching);
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        /* DateWidget */
        dateText = (EditText) findViewById(R.id.edit_date_picker);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        Intent intent = getIntent();
        classid = intent.getStringExtra("teach_id");
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("teachId", classid);
        selected_date = day + "-" + (month) + "-" + year;
        Log.e("getchap", String.valueOf(classid));
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        createCatalog = (Button) findViewById(R.id.buttonCreate);

        // Setting adapter
        setmRAdapter(DailyTeachingActivity.this, "DailyTeaching Page");

        String loginURL = ApiKeyConstant.apiUrl + "/api/v1/time_table_classes/" + classid + "/get_chapters.json";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, loginURL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    List<String> categories = new ArrayList<String>();
                    if (success) {
                        JSONArray jsonArray = response.getJSONArray("chapters");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject chapterObj = jsonArray.getJSONObject(i);
                            chapter_array.add(chapterObj.getInt("id"));
                            categories.add(chapterObj.getString("name"));
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, categories);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(dataAdapter);
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
                            Intent intent = new Intent(DailyTeachingActivity.this, AndroidSpinnerExampleActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "Something went wrong...", Toast.LENGTH_LONG).show();
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

        createCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder buff = new StringBuilder();
                String sep = "";
                List<PointsData> stList = ((PointsAdapter) mAdapter)
                        .getStudentist();
                for (int i = 0; i < stList.size(); i++) {
                    PointsData singleStudent = stList.get(i);
                    if (singleStudent.isSelected() == true) {
                        buff.append(sep);
                        buff.append(singleStudent.getPointId());
                        sep = ",";
                    }
                }
                JSONObject daily_teaching_point = new JSONObject();
                JSONObject userObj = new JSONObject();
                try {
                    daily_teaching_point.put("chapter_id", chapter_id);
                    daily_teaching_point.put("chapters_point_id", buff);
                    daily_teaching_point.put("date", selected_date);
                    userObj.put("daily_teaching_point", daily_teaching_point);
                    Log.e("daily_teach", String.valueOf(daily_teaching_point));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String loginURL = ApiKeyConstant.apiUrl + "/api/v1/time_table_classes/" + classid + "/daily_teachs";
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL, userObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                int id = response.getInt("dtp_id");
                                String dtp_id = String.valueOf(id);
                                Intent intent1 = new Intent(DailyTeachingActivity.this, PresentyCatalog.class);
                                intent1.putExtra("dtp_id", dtp_id);
                                startActivity(intent1);
                            }
                        } catch (JSONException e) {
                            String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == 401) {
                                    Intent intent = new Intent(DailyTeachingActivity.this, AndroidSpinnerExampleActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getBaseContext(), "Daily Catalog Not Saved", Toast.LENGTH_LONG).show();
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
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DailyTeachingActivity.this, TimeTableActivity.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = getIntent();
        classid = intent.getStringExtra("teachId");
        for (int j = position; j <= position; j++) {
            chapter_id = chapter_array.get(j);
        }
        String loginURL = ApiKeyConstant.apiUrl + "/api/v1/time_table_classes/" + classid + "/chapters/" + chapter_id + "/get_points.json";
        mProgress.show();
        if (Utils.isConnected(getApplicationContext())) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, loginURL, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        linearpoints = (LinearLayout) findViewById(R.id.pointslinear);
                        boolean success = response.getBoolean("success");
                        List<PointsData> pointsList = new ArrayList<PointsData>();
                        if (success) {
                            mProgress.dismiss();
                            JSONArray jsonArray = response.getJSONArray("points");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject pointsObj = jsonArray.getJSONObject(i);
                                PointsData points = new PointsData(pointsObj.getString("name"), false, pointsObj.getInt("id"));
                                pointsList.add(points);
                            }
                        }
                        m_points_RecyclerView = (RecyclerView) findViewById(R.id.point_recycler_view);
                        m_points_RecyclerView.setHasFixedSize(false);
                        m_points_RecyclerView.setNestedScrollingEnabled(false);
                        m_points_RecyclerView.setLayoutManager(new LinearLayoutManager(DailyTeachingActivity.this));
                        mAdapter = new PointsAdapter(pointsList);
                        m_points_RecyclerView.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();
                    }

                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(), "Something went wrong...", Toast.LENGTH_LONG).show();
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, R.style.DialogTheme,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        dateText.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}
