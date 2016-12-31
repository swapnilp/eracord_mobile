package com.example.vidhiraj.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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
 * Created by vidhiraj on 12-08-2016.
 */
public class ClassActivity extends BaseActivity {
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<ClassData> data = null;
    private static ArrayList<DailyTeachData> dailyTeach = null;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean mIsRefreshing = false;
    TextView dataAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_class);
        super.onCreate(savedInstanceState);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.st_swipeRefreshLayout);
        dataAvailability = (TextView) findViewById(R.id.noStData);
        fetchClassData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utils.isConnected(getApplicationContext())) {
                    mIsRefreshing = true;
                    fetchClassData();
                } else {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 5000);

                }
            }
        });

        setmRAdapter(ClassActivity.this, "Class Page");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ClassActivity.this, TimeTableActivity.class));
        finish();

    }

    public void fetchClassData() {
        String loginURL = ApiKeyConstant.apiUrl + "/api/v1/time_table_classes.json";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, loginURL, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                data = new ArrayList<ClassData>();
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        swipeRefreshLayout.setRefreshing(false);
                        JSONArray jsonArray = response.getJSONArray("time_table_classes");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject orgObj = jsonArray.getJSONObject(i);
                                ClassData classData = new ClassData();
                                classData.name = orgObj.getString("class_name");
                                classData.subject = orgObj.getString("subject");
                                if(orgObj.getString("division") != null && !orgObj.getString("division").isEmpty() && orgObj.getString("division") != "null") {
                                    classData.division = orgObj.getString("division");
                                }
                                classData.image = R.drawable.class_circle;
                                String id = orgObj.getString("id");
                                classData.id = Integer.parseInt(id);
                                data.add(classData);
                            }
                        } else {
                            dataAvailability.setVisibility(View.VISIBLE);
                        }
                    }
                    recyclerView = (RecyclerView) findViewById(R.id.st_recycler_view);
                    recyclerView.setHasFixedSize(false);
                    recyclerView.setNestedScrollingEnabled(false);
                    adapter = new ClassAdapter(ClassActivity.this, data);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ClassActivity.this));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
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
                            Intent intent = new Intent(ClassActivity.this, AndroidSpinnerExampleActivity.class);
                            startActivity(intent);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
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
}