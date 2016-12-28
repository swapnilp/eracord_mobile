package com.example.vidhiraj.sample;import android.app.ProgressDialog;import android.content.Context;import android.content.Intent;import android.os.AsyncTask;import android.os.Bundle;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;import android.util.Log;import android.view.View;import android.widget.ScrollView;import android.widget.Button;import android.widget.TextView;import android.widget.Toast;import com.android.volley.AuthFailureError;import com.android.volley.NetworkResponse;import com.android.volley.Request;import com.android.volley.Response;import com.android.volley.VolleyError;import com.android.volley.toolbox.JsonObjectRequest;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import java.util.ArrayList;import java.util.HashMap;import java.util.Map;public class DailyCatalogActivity extends BaseActivity {    private static DailyCatalogAdapter adapter;    private static RecyclerView recyclerView;    private LinearLayoutManager mLayoutManager;    private static ArrayList<DailyTeachData> dailyTeach = null;    int current_page = 1;    Button load;    ProgressDialog pDialog, mProgress;    TextView dataAvailability;    String url_icon;    String url = ApiKeyConstant.apiUrl + "/api/v1/daily_teachs";    ScrollView scrollview;    @Override    protected void onCreate(Bundle savedInstanceState) {        setContentView(R.layout.daily_fill_catalog);        super.onCreate(savedInstanceState);        recyclerView = (RecyclerView) findViewById(R.id.dailyT_recycler_view);        scrollview = ((ScrollView) findViewById(R.id.scrollView_dailyTeach));        dataAvailability = (TextView) findViewById(R.id.noDailyTeachData);        mProgress = new ProgressDialog(this);        mProgress.setTitle("Processing...");        mProgress.setMessage("Please wait...");        mProgress.setCancelable(false);        mProgress.setIndeterminate(true);        // Setting adapter        setmRAdapter(DailyCatalogActivity.this, "DailyCatalog Page");        load = (Button) findViewById(R.id.loadmore_dailyteach);        dailyTeach = new ArrayList<>();        mProgress.show();        if(Utils.isConnected(getApplicationContext())) {            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {                @Override                public void onResponse(JSONObject response) {                    try {                        boolean success = response.getBoolean("success");                        if (success) {                            mProgress.dismiss();                            JSONArray jsonArray = response.getJSONArray("daily_teaching_points");                            int arrayLength = jsonArray.length();                            if (arrayLength >= 10) {                                load.bringToFront();                                load.setVisibility(View.VISIBLE);                            }                            if (jsonArray.length() != 0) {                                for (int i = 0; i < jsonArray.length(); i++) {                                    JSONObject orgObj = jsonArray.getJSONObject(i);                                    DailyTeachData dailyData = new DailyTeachData();                                    dailyData.standard = orgObj.getString("jkci_class");                                    dailyData.chapter = orgObj.getString("chapter");                                    dailyData.date = orgObj.getString("date");                                    dailyData.points = orgObj.getString("points");                                    dailyData.id = orgObj.getInt("id");                                    dailyTeach.add(dailyData);                                    load.bringToFront();                                }                            } else {                                dataAvailability.setVisibility(View.VISIBLE);                                load.setVisibility(View.GONE);                            }                        }                        recyclerView.setHasFixedSize(true);                        mLayoutManager = new LinearLayoutManager(getApplicationContext());                        recyclerView.setLayoutManager(mLayoutManager);                        adapter = new DailyCatalogAdapter(dailyTeach, getApplicationContext());                        recyclerView.setAdapter(adapter);                        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());                    } catch (JSONException e) {                        String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();                        Log.e("sdcard-err2:", err);                    }                }            },                    new Response.ErrorListener() {                        @Override                        public void onErrorResponse(VolleyError error) {                            NetworkResponse networkResponse = error.networkResponse;                            if (networkResponse != null && networkResponse.statusCode == 401) {                                Intent intent = new Intent(DailyCatalogActivity.this, AndroidSpinnerExampleActivity.class);                                startActivity(intent);                            } else {                                mProgress.dismiss();                            }                        }                    }) {                @Override                public Map<String, String> getHeaders() throws AuthFailureError {                    HashMap<String, String> headers = new HashMap<String, String>();                    headers.put("Content-Type", "application/json; charset=utf-8");                    headers.put("Authorization", ApiKeyConstant.authToken);                    return headers;                }            };            VolleyControl.getInstance().addToRequestQueue(jsonObjReq);            load.setOnClickListener(new View.OnClickListener() {                @Override                public void onClick(View v) {                    new loadMoreListView().execute();                }            });        }        else {            mProgress.dismiss();            Toast.makeText(getBaseContext(),"Check Internet Connection", Toast.LENGTH_LONG).show();        }    }    @Override    public void onBackPressed() {        super.onBackPressed();        startActivity(new Intent(DailyCatalogActivity.this, TimeTableActivity.class));        finish();    }    private class loadMoreListView extends AsyncTask<Void, Void, Void> {        @Override        protected void onPreExecute() {            pDialog = new ProgressDialog(                    DailyCatalogActivity.this);            pDialog.setMessage("Please wait..");            pDialog.setIndeterminate(true);            pDialog.setCancelable(false);            pDialog.show();        }        protected Void doInBackground(final Void... unused) {            runOnUiThread(new Runnable() {                public void run() {                    current_page += 1;                    String url = ApiKeyConstant.apiUrl + "/api/v1/daily_teachs?page=" + current_page;                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {                        @Override                        public void onResponse(JSONObject response) {                            try {                                boolean success = response.getBoolean("success");                                if (success) {                                    JSONArray jsonArray = response.getJSONArray("daily_teaching_points");                                    int arrayLength = jsonArray.length();                                    if (arrayLength >= 10) {                                        load.bringToFront();                                        load.setVisibility(View.VISIBLE);                                    }                                    for (int i = 0; i < jsonArray.length(); i++) {                                        JSONObject orgObj = jsonArray.getJSONObject(i);                                        DailyTeachData dailyData = new DailyTeachData();                                        dailyData.standard = orgObj.getString("jkci_class");                                        dailyData.chapter = orgObj.getString("chapter");                                        dailyData.date = orgObj.getString("date");                                        dailyData.points = orgObj.getString("points");                                        dailyData.id = orgObj.getInt("id");                                        dailyTeach.add(dailyData);                                        adapter.notifyItemInserted(dailyTeach.size());                                    }                                    scrollview.post(new Runnable() {                                        @Override                                        public void run() {                                            int x=0,y=1000;                                            scrollview.scrollTo(x, y);                                        }                                    });                                    if (arrayLength < 10) {                                        load.setVisibility(View.GONE);                                        mProgress.dismiss();                                        Toast.makeText(getApplicationContext(), "No more data to load", Toast.LENGTH_LONG).show();                                    }                                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());                                }                            } catch (JSONException e) {                                String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();                            }                        }                    },                            new Response.ErrorListener() {                                @Override                                public void onErrorResponse(VolleyError error) {                                    NetworkResponse networkResponse = error.networkResponse;                                    if (networkResponse != null && networkResponse.statusCode == 401) {                                        load.bringToFront();                                        Intent intent = new Intent(DailyCatalogActivity.this, AndroidSpinnerExampleActivity.class);                                        startActivity(intent);                                    } else {                                        load.setVisibility(View.GONE);                                        Toast.makeText(getApplicationContext(), "No More Data to laod", Toast.LENGTH_LONG).show();                                    }                                }                            }) {                        @Override                        public Map<String, String> getHeaders() throws AuthFailureError {                            HashMap<String, String> headers = new HashMap<String, String>();                            headers.put("Content-Type", "application/json; charset=utf-8");                            headers.put("Authorization", ApiKeyConstant.authToken);                            return headers;                        }                    };                    VolleyControl.getInstance().addToRequestQueue(jsonObjReq);                }            });            return (null);        }        protected void onPostExecute(Void unused) {            pDialog.dismiss();        }    }}