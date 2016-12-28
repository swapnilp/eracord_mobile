package com.example.vidhiraj.sample;

import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vidhiraj on 12-08-2016.
 */
public class FeedbackActivity extends BaseActivity {
    String url_icon = null;
    TextView from_cust;
    EditText title, body_message;
    Button send_feedback;

    private static String RECIPIENT = "nileshgorle@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.feedback_form);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        from_cust = (TextView) findViewById(R.id.from_cust);
        from_cust.setText(super.user_email);
        title = (EditText) findViewById(R.id.title);
        body_message = (EditText) findViewById(R.id.message);
        send_feedback = (Button) findViewById(R.id.send_feedback);

        // Setting adapter
        setmRAdapter(FeedbackActivity.this, "Feedback Page");

        send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_feedback_user();
            }
        });
    }

    public boolean validate_fields() {
        if (title.getText().toString().equals("") || body_message.getText().toString().equals("")) {
            Toast.makeText(getBaseContext(), "Title and message are required!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void send_feedback_user() {
        final ProgressDialog mDialog;

        if (!validate_fields()) {
            return;
        }
        send_feedback.setEnabled(false);
        mDialog = ProgressDialog.show(FeedbackActivity.this, "In Progress", "Loading...");

        /*
        //get to, subject and content from the user input and store as string.
        String emailTo 		= RECIPIENT;
        String emailSubject = "From: " + from_cust.getText() + " Title: " + title.getText();
        String emailContent = body_message.getText().toString();

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode(emailTo) +
                "?subject=" + Uri.encode(emailSubject) +
                "&body=" + Uri.encode(emailContent);
        Uri uri = Uri.parse(uriText);
        send.setType("message/rfc822");
        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send feedback using"));
        */

        JSONObject userObj = new JSONObject();
        JSONObject feed_back = new JSONObject();

        try {
            userObj.put("email", from_cust.getText().toString());
            userObj.put("title", title.getText().toString());
            userObj.put("message", body_message.getText().toString());
            feed_back.put("feed_back", userObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String feedbackURL = ApiKeyConstant.apiUrl + "/api/v1/feedbacks";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, feedbackURL, feed_back, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        //ApiKeyConstant.authToken = response.getString("token");
                        Toast.makeText(getBaseContext(), "Feedback mailed successfully !!!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FeedbackActivity.this, TimeTableActivity.class);
                        startActivity(intent);
                        send_feedback.setEnabled(true);
                        mDialog.dismiss();
                    }

                } catch (JSONException e) {
                    send_feedback.setEnabled(true);
                    mDialog.dismiss();
                    String err = (e.getMessage() == null) ? "SD Card failed" : e.getMessage();
                    Log.e("sdcard-err2:", err);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        send_feedback.setEnabled(true);
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Mail sending failed !!!", Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyControl.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(FeedbackActivity.this, TimeTableActivity.class));
        finish();
    }
}