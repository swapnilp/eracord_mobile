package com.eracordapp.teacher.sample;

import android.app.ProgressDialog;
import android.view.WindowManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by teacher on 10-08-2016.
 */
public class LoginPinActivity extends AppCompatActivity implements View.OnClickListener {
    Button login;
    String device_id;
    String email;
    Integer mpin;
    PinEntryView uniqueUserPin, uniqueConfirmUserPin;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    ProgressDialog mProgress;
    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_layout);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        uniqueUserPin = (PinEntryView) findViewById(R.id.userpin);
        uniqueConfirmUserPin = (PinEntryView) findViewById(R.id.confirmuserpin);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        ApiKeyConstant.authToken = intent.getStringExtra("authorization_token");
        email = intent.getStringExtra("email");
        login = (Button) findViewById(R.id.loginuser);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginuser:
                try {
                    validateCheck();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void validateCheck() throws JSONException {
        if (Utils.isConnected(getApplicationContext())) {
            if (!validate()) {
                onLoginFailed();
                return;
            } else {
                Log.e("success", "done");
                final String mpinString = uniqueConfirmUserPin.getText().toString();
                mpin = Integer.parseInt(mpinString);
                device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                JSONObject jo = new JSONObject();
                jo.put("device_id", device_id);
                jo.put("email", email);
                jo.put("mpin", mpin);
                JSONObject userObj = new JSONObject();
                userObj.put("authorization_token", ApiKeyConstant.authToken);
                userObj.put("user", jo);
                String loginURL = ApiKeyConstant.apiUrl + "/users/mobile_sign_up";
                mProgress.show();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL, userObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                mProgress.dismiss();
                                Cursor cursor = getContentResolver().query(User.CONTENT_URI, null, null, null, null);
                                if (cursor.getCount() != 0) {
                                    UserDB userDB = new UserDB(getApplicationContext());
                                    SQLiteDatabase db = userDB.getWritableDatabase();
                                    db.execSQL("DELETE FROM " + UserDB.DATABASE_TABLE);
                                    ContentValues values = new ContentValues();
                                    values.put(UserDB.KEY_EMAIL, email);
                                    getContentResolver().insert(User.CONTENT_URI, values);
                                } else {
                                    ContentValues values = new ContentValues();
                                    values.put(UserDB.KEY_EMAIL, email);
                                    getContentResolver().insert(User.CONTENT_URI, values);
                                }
                                Intent intent = new Intent(LoginPinActivity.this, AndroidSpinnerExampleActivity.class);
                                intent.putExtra("email", email);
                                finish();
                                startActivity(intent);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == 401) {
                                    Toast.makeText(getBaseContext(), "Something went wrong,Try Again", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginPinActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    mProgress.dismiss();
                                    Toast.makeText(getBaseContext(), "Please Try Again", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
                VolleyControl.getInstance().addToRequestQueue(jsonObjReq);
            }
        } else {
            mProgress.dismiss();
            Toast.makeText(getBaseContext(), "Check Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Try Again", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;
        String userpin = uniqueUserPin.getText().toString();
        String confirmpin = uniqueConfirmUserPin.getText().toString();
        if (userpin.length() != 4) {
            Toast.makeText(getBaseContext(), "Enter only 4 digit pin", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            if (!userpin.equals(confirmpin)) {
                Toast.makeText(getBaseContext(), "Both pin does not match", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }
        return valid;
    }
}
