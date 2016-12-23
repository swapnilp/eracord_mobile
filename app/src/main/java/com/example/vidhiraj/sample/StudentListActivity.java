package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.view.WindowManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 22/08/2016.
 */
public class StudentListActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private StudentCatalogAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private static ArrayList<StudentData> dailyTeach;
    private int current_page = 1;
    Button load;
    EditText search;
    ProgressDialog pDialog;
    TextView dataAvailability;
    ProgressDialog mProgress;
    String url = ApiKeyConstant.apiUrl + "/api/v1/students";
    protected Handler handler;
    ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.student_catalog);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mRecyclerView = (RecyclerView) findViewById(R.id.sd_recycler_view);
        dataAvailability = (TextView) findViewById(R.id.noSdData);
        scrollview = ((ScrollView) findViewById(R.id.scrollView_st_catalog));
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        // Setting adapter
        setmRAdapter(StudentListActivity.this, "StudentList Page");

        load = (Button) findViewById(R.id.more_student);
        dailyTeach = new ArrayList<StudentData>();
        handler = new Handler();
        search = (EditText) findViewById(R.id.search);
        mProgress.show();
        if (Utils.isConnected(getApplicationContext())) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            load.setVisibility(View.VISIBLE);
                            mProgress.dismiss();
                            JSONArray jsonArray = response.getJSONArray("students");
                            int arrayLength = jsonArray.length();
                            if (arrayLength >= 10) {
                                load.bringToFront();
                                load.setVisibility(View.VISIBLE);
                            }
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orgObj = jsonArray.getJSONObject(i);
                                    StudentData dailyData = new StudentData();
                                    dailyData.stud_name = orgObj.getString("name");
                                    dailyData.stud_class_name = orgObj.getString("class_names");
                                    dailyData.image_url = orgObj.getString("image_url");
                                    try {
                                        dailyData.stud_hostel = orgObj.getInt("hostel_id");
                                    } catch (Exception e) {
                                        dailyData.stud_hostel = 0;
                                    }
                                    dailyTeach.add(dailyData);
                                    load.bringToFront();
                                }
                            } else {
                                dataAvailability.setVisibility(View.VISIBLE);
                                load.setVisibility(View.GONE);
                            }
                        }
                        mRecyclerView.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mAdapter = new StudentCatalogAdapter(dailyTeach, getApplicationContext());
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
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
                                Intent intent = new Intent(StudentListActivity.this, AndroidSpinnerExampleActivity.class);
                                startActivity(intent);
                            } else {
                                mProgress.dismiss();
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
            load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new loadMoreListView().execute();
                }
            });
            addTextListener();
        } else {
            mProgress.dismiss();
            Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StudentListActivity.this, ClassActivity.class));
        finish();

    }


    public void addTextListener() {
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<StudentData> filteredList = new ArrayList<>();

                for (int i = 0; i < dailyTeach.size(); i++) {

                    final String text = dailyTeach.get(i).getStud_class_name().toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(dailyTeach.get(i));
                    }
                }
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(StudentListActivity.this));
                mAdapter = new StudentCatalogAdapter(filteredList, StudentListActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    StudentListActivity.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(final Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    current_page += 1;
                    url = ApiKeyConstant.apiUrl + "/api/v1/students?&page=" + current_page;
                    mProgress.show();
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    mProgress.dismiss();
                                    JSONArray jsonArray = response.getJSONArray("students");
                                    int arrayLength = jsonArray.length();
                                    if (arrayLength >= 10) {
                                        load.bringToFront();
                                        load.setVisibility(View.VISIBLE);
                                    }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject orgObj = jsonArray.getJSONObject(i);
                                        StudentData dailyData = new StudentData();
                                        dailyData.stud_name = orgObj.getString("name");
                                        dailyData.stud_class_name = orgObj.getString("class_names");
                                        dailyData.image_url = orgObj.getString("image_url");
                                        try {
                                            dailyData.stud_hostel = orgObj.getInt("hostel_id");
                                        } catch (Exception e) {
                                            dailyData.stud_hostel = 0;
                                        }
                                        dailyTeach.add(dailyData);
                                        mAdapter.notifyItemInserted(dailyTeach.size());
                                    }

                                    scrollview.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            int x = 0, y = 1000;
                                            scrollview.scrollTo(x, y);
                                        }
                                    });

                                    if (arrayLength < 10) {
                                        load.setVisibility(View.GONE);
                                        mProgress.dismiss();
                                        Toast.makeText(getApplicationContext(), "No more data to load", Toast.LENGTH_LONG).show();
                                    }
                                    mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount());
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
                                        load.bringToFront();
                                        Intent intent = new Intent(StudentListActivity.this, AndroidSpinnerExampleActivity.class);
                                        startActivity(intent);
                                    } else {
                                        load.setVisibility(View.GONE);
                                        mProgress.dismiss();
                                        mRecyclerView.setHasFixedSize(false);
                                        Toast.makeText(getApplicationContext(), "No more data to load", Toast.LENGTH_LONG).show();
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

            return (null);
        }

        protected void onPostExecute(Void unused) {
            pDialog.dismiss();
        }
    }
}

