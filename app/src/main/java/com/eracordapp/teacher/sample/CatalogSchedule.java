package com.eracordapp.teacher.sample;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Lenovo on 11/13/2016.
 */

public class CatalogSchedule extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.calendar_activity);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        final String teachId = intent.getStringExtra("teach_id");
        setmRAdapter(CatalogSchedule.this, "Catalog Page");
    }
}

