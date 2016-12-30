package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
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
import java.util.Map;

/**
 * Created by Lenovo on 12/11/2016.
 */

public class OffClassesActivity extends BaseActivity {
    private static OffClassesAdapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<OffClassesData> dailyTeach = null;
    String org = null;
    int counter = 9;
    Button load;
    ProgressDialog pDialog, mProgress;
    TextView dataAvailability;
    int current_page = 1;
    String url = ApiKeyConstant.apiUrl + "/api/v1/off_classes.json";
    ScrollView scrollview;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.off_class_activity);
        super.onCreate(savedInstanceState);
        recyclerView = (RecyclerView) findViewById(R.id.off_recycler_view);
        scrollview = ((ScrollView) findViewById(R.id.scrollView_offclass));
        load = (Button) findViewById(R.id.loadmore_offclasses);
        dataAvailability = (TextView) findViewById(R.id.noOffData);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        // Setting adapter
        setmRAdapter(OffClassesActivity.this, "Offclasses Page");

        dailyTeach = new ArrayList<>();
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
                            JSONArray jsonArray = response.getJSONArray("off_classes");
                            int arrayLength = jsonArray.length();
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orgObj = jsonArray.getJSONObject(i);
                                    OffClassesData offClassData = new OffClassesData();
                                    offClassData.name = orgObj.getString("name");
                                    offClassData.date = orgObj.getString("date");
                                    offClassData.teacher_name = orgObj.getString("teacher_name");
                                    dailyTeach.add(offClassData);
                                    load.bringToFront();
                                }
                            } else {
                                dataAvailability.setVisibility(View.VISIBLE);
                                load.setVisibility(View.GONE);
                            }
                        }
                        if (getResources().getConfiguration().orientation == 2) {
                            counter = 2;
                        }
                        recyclerView.setHasFixedSize(false);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        adapter = new OffClassesAdapter(getApplicationContext(), dailyTeach);
                        recyclerView.setAdapter(adapter);
                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    } catch (JSONException e) {
                        String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            mProgress.dismiss();
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == 401) {
                                Intent intent = new Intent(OffClassesActivity.this, AndroidSpinnerExampleActivity.class);
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
        } else {
            mProgress.dismiss();
            Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OffClassesActivity.this, TimeTableActivity.class));
        finish();
    }

    private class loadMoreListView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(
                    OffClassesActivity.this);
            pDialog.setMessage("Please wait..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Void doInBackground(final Void... unused) {
            runOnUiThread(new Runnable() {
                public void run() {
                    current_page += 1;
                    String url = ApiKeyConstant.apiUrl + "/api/v1/off_classes.json?page=" + current_page;
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    mProgress.dismiss();
                                    JSONArray jsonArray = response.getJSONArray("off_classes");
                                    int arrayLength = jsonArray.length();
                                    if (jsonArray.length() != 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject orgObj = jsonArray.getJSONObject(i);
                                            OffClassesData offClassData = new OffClassesData();
                                            offClassData.name = orgObj.getString("name");
                                            offClassData.date = orgObj.getString("date");
                                            offClassData.teacher_name = orgObj.getString("teacher_name");
                                            dailyTeach.add(offClassData);
                                            adapter.notifyItemInserted(dailyTeach.size());
                                            load.bringToFront();
                                        }
                                    } else {
                                        load.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "No More Data to laod", Toast.LENGTH_LONG).show();
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

                                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
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
                                        Intent intent = new Intent(OffClassesActivity.this, AndroidSpinnerExampleActivity.class);
                                        startActivity(intent);
                                        load.bringToFront();
                                    } else {
                                        load.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "No More Data to laod", Toast.LENGTH_LONG).show();
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
