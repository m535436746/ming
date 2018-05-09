package com.telstar.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutSystem extends Activity {

    TextView modle_number , firmware_version , build_number , kernel_version ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_system);
        modle_number = (TextView) findViewById(R.id.model_number_value);
        firmware_version = (TextView) findViewById(R.id.firmware_version_value);
        build_number = (TextView) findViewById(R.id.build_number_value);
        kernel_version = (TextView) findViewById(R.id.kernel_version_value);

        modle_number.setText(SystemInfoManager.getModelNumber());
        firmware_version.setText(SystemInfoManager.getAndroidVersion());
        build_number.setText(SystemInfoManager.getBuildNumber());
        kernel_version.setText(SystemInfoManager.getKernelVersion());
    }
}
