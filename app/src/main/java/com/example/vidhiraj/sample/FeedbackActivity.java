package com.example.vidhiraj.sample;

import android.view.WindowManager;
import android.widget.TextView;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.example.vidhiraj.sample.AndroidSpinnerExampleActivity.MY_PREFS_NAME;

/**
 * Created by vidhiraj on 12-08-2016.
 */
public class FeedbackActivity extends AppCompatActivity {
    String TITLES[] = {"Home", "Daily Catalog", "Students","TimeTable","Off Classes","Feedback","Share app","Logout"};
    int ICONS[] = {R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos, R.drawable.ic_photos};
    String org = null;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout
    ActionBarDrawerToggle mDrawerToggle;
    String url_icon = null;
    TextView from_cust;
    EditText title, body_message;
    Button send_feedback;

    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    private GoogleApiClient client;

    private static String RECIPIENT = "nileshgorle@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String user_email = prefs.getString("email", null);
        org = prefs.getString("specificorg", null);
        url_icon = prefs.getString("org_icon", null);

        from_cust = (TextView) findViewById(R.id.from_cust);
        from_cust.setText(user_email);
        title = (EditText) findViewById(R.id.title);
        body_message = (EditText) findViewById(R.id.message);
        send_feedback = (Button) findViewById(R.id.send_feedback);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(org);
        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
        mAdapter = new EraMyAdapter(FeedbackActivity.this, TITLES, ICONS, org, user_email, url_icon);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_feedback_user();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.Drawer.isDrawerOpen(GravityCompat.START)) {
            this.Drawer.closeDrawer(GravityCompat.START);
        } else {
            this.Drawer.openDrawer(GravityCompat.START);
        }
    }

    public boolean validate_fields() {
        if (title.getText().toString().equals("") || body_message.getText().toString().equals("")) {
            Toast.makeText(getBaseContext(), "Title and message are required!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void send_feedback_user() {
        if (!validate_fields()) {
            return;
        }

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
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Class Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.vidhiraj.sample/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Class Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.vidhiraj.sample/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}