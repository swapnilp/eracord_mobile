package com.eracordapp.teacher.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "MainActivityLogin";
    public Cursor cursor;
    Button loginButton;
    private EditText userEmail;
    private EditText userPassword;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    ActionBarDrawerToggle mDrawerToggle;
    EditText editTextEmail, editPassword;
    private ProgressDialog mProgress;
    private GoogleApiClient client;
    boolean signflag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent1 = getIntent();
        signflag = intent1.getBooleanExtra("diffflag", false);
        cursor = getContentResolver().query(User.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() != 0 && signflag == false) {
            Intent intent = new Intent(MainActivity.this, AndroidSpinnerExampleActivity.class);
            startActivity(intent);
        } else if ((cursor.getCount() != 0 && signflag == true) || (cursor.getCount() == 0 && signflag == true) || (cursor.getCount() == 0 && signflag == false)) {
            setContentView(R.layout.activity_main);
            getSupportLoaderManager().initLoader(0, null, this);
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            mProgress = new ProgressDialog(this);
            mProgress.setTitle("Processing...");
            mProgress.setMessage("Please wait...");
            mProgress.setCancelable(false);
            mProgress.setIndeterminate(true);
            editTextEmail = (EditText) findViewById(R.id.editTextEmail);
            editPassword = (EditText) findViewById(R.id.editTextPassword);
            userEmail = (EditText) findViewById(R.id.editTextEmail);
            userPassword = (EditText) findViewById(R.id.editTextPassword);
            loginButton = (Button) findViewById(R.id.buttonLogin);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        login();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri uri = User.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    public void login() throws JSONException {
        if (Utils.isConnected(getApplicationContext())) {
            if (!validate()) {
                onLoginFailed();
                return;
            } else {
                final String user_email = editTextEmail.getText().toString();
                String user_password = editPassword.getText().toString();
                JSONObject jo = new JSONObject();
                jo.put("email", user_email);
                jo.put("password", user_password);
                JSONObject userObj = new JSONObject();
                userObj.put("user", jo);
                String loginURL = ApiKeyConstant.apiUrl + "/users/mobile_sign_in";
                mProgress.show();
                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, loginURL, userObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            ApiKeyConstant.authToken = response.getString("token");
                            if (success) {
                                mProgress.dismiss();
                                Intent intent = new Intent(MainActivity.this, LoginPinActivity.class);
                                intent.putExtra("email", user_email);
                                intent.putExtra("authorization_token", ApiKeyConstant.authToken);
                                finish();
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        new ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mProgress.dismiss();
                                Toast.makeText(getBaseContext(), "Login failed!", Toast.LENGTH_LONG).show();

                            }
                        }
                );
                VolleyControl.getInstance().addToRequestQueue(jsonObjReq);
            }
        } else {
            Toast.makeText(getBaseContext(), "Check internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid User", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("enter a valid email address");
            valid = false;
        } else {
            userEmail.setError(null);
        }
        if (password.isEmpty() || password.length() < 4) {
            userPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            userPassword.setError(null);
        }
        return valid;
    }

    @Override
    public void onBackPressed() {
        if (cursor.getCount() != 0) {
            Intent intent = new Intent(MainActivity.this, AndroidSpinnerExampleActivity.class);
            startActivity(intent);
        } else {
            finish();
        }

    }
}
