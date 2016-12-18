package com.example.vidhiraj.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.vidhiraj.sample.AndroidSpinnerExampleActivity.MY_PREFS_NAME;

/**
 * Created by lenovo on 31/08/2016.
 */
public class PresentyCatalog extends AppCompatActivity {

    String TITLES[] = {"Home", "Daily Catalog", "Students", "TimeTable", "Off Classes", "Feedback", "Share app", "Logout"};
    int ICONS[] = {R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos};
    String org = null;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    RecyclerView mDrawerRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mDrawerAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;         // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ActionBarDrawerToggle mDrawerToggle;
    private GoogleApiClient client;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<PresentyData> data = null;
    Button savePresenty;
    TextView dataAvailabiliy, totalPresent, totalAbsent;
    String url_icon = null;
    int countPresent = 0;
    int countAbsent = 0;
    ProgressDialog mProgress;
    StringBuilder buff = new StringBuilder();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presenty_catalog);
        final Intent intent = getIntent();
        final String id = intent.getStringExtra("dtp_id");
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        savePresenty = (Button) findViewById(R.id.updatepresenty);
        dataAvailabiliy = (TextView) findViewById(R.id.noPrData);
        totalPresent = (TextView) findViewById(R.id.totalpresent);
        totalAbsent = (TextView) findViewById(R.id.totalabsent);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String user_email = prefs.getString("email", null);
        org = prefs.getString("specificorg", null);
        url_icon = prefs.getString("org_icon", null);
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(org);
        mDrawerRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mDrawerRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mDrawerAdapter = new EraMyAdapter(PresentyCatalog.this, TITLES, ICONS, org, user_email, url_icon);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mDrawerRecyclerView.setAdapter(mDrawerAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mDrawerRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        String url = ApiKeyConstant.apiUrl + "/api/v1/daily_teachs/" + id + "/get_catlogs";
        mProgress.show();
        if (Utils.isConnected(getApplicationContext())) {
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    data = new ArrayList<>();
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            mProgress.dismiss();
                            savePresenty.setVisibility(View.VISIBLE);
                            JSONArray jsonArray = response.getJSONArray("class_catlogs");
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orgObj = jsonArray.getJSONObject(i);
                                    PresentyData classData = new PresentyData();
                                    classData.setName(orgObj.getString("name"));
                                    classData.setSelected(orgObj.getBoolean("is_present"));
                                    boolean totalcount = orgObj.getBoolean("is_present");
                                    if (totalcount) {
                                        countPresent++;
                                    } else {
                                        countAbsent++;
                                    }
                                    classData.setPointId(orgObj.getInt("id"));
                                    data.add(classData);
                                }
                                totalAbsent.setText(Integer.toString(countAbsent));
                                totalPresent.setText(Integer.toString(countPresent));

                            } else {
                                savePresenty.setVisibility(View.GONE);
                                dataAvailabiliy.setVisibility(View.VISIBLE);
                            }
                        }

                        recyclerView = (RecyclerView) findViewById(R.id.pr_recycler_view);
                        recyclerView.setHasFixedSize(true);
                        adapter = new PresentyAdapter(PresentyCatalog.this, data, totalPresent, totalAbsent);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PresentyCatalog.this));
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
                                Intent intent = new Intent(PresentyCatalog.this, AndroidSpinnerExampleActivity.class);
                                startActivity(intent);
                            } else {
                                mProgress.dismiss();
                                dataAvailabiliy.setVisibility(View.VISIBLE);
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
        savePresenty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sep = "";
                List<PresentyData> stList = ((PresentyAdapter) adapter)
                        .getStudentist();
                for (int i = 0; i < stList.size(); i++) {
                    if (stList.get(i).isSelected() == false) {
                        buff.append(sep);
                        buff.append(stList.get(i).getPointId());
                        sep = ",";
                    }
                }
                JSONObject daily_teaching = new JSONObject();
                JSONObject daily_teaching_point = new JSONObject();

                try {
                    daily_teaching.put("absenty_string", buff);
                    daily_teaching_point.put("daily_teaching_point", daily_teaching);
                } catch (JSONException e) {

                    e.printStackTrace();
                }

                String loginURL = ApiKeyConstant.apiUrl + "/api/v1/daily_teachs/" + id + "/save_catlogs";
                if (Utils.isConnected(getApplicationContext())) {
                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL, daily_teaching_point, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    Intent intent1 = new Intent(PresentyCatalog.this, DailyCatalogActivity.class);
                                    startActivity(intent1);
                                    Toast.makeText(getBaseContext(), "Student Presentee Saved", Toast.LENGTH_LONG).show();
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
                                        Intent intent = new Intent(PresentyCatalog.this, AndroidSpinnerExampleActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getBaseContext(), "Student Presenty Not Saved", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PresentyCatalog.this, DailyCatalogActivity.class));
        finish();
    }
}
