package com.p1.scmu.home_security_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by Vanessa on 5/28/2017.
 */

public class ActivitySettings extends AppCompatActivity {

    public static final String SETTINGS = "Settings";
    public static final String PREVIOUS_SETTINGS = "PreviousSettings";
    //public static final String OWNER = "Owner";

    private Switch silentMode, alarmMode;
    private Button btn_ok, btn_cancel, btn_auth;
    private String auth;
    private Settings previousSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        previousSettings = bundle.getParcelable(PREVIOUS_SETTINGS);

        silentMode = (Switch) findViewById(R.id.switch_silent_mode);
        alarmMode = (Switch) findViewById(R.id.switch_alarm_mode);
        silentMode.setChecked(previousSettings.getSilentMode());
        alarmMode.setChecked(previousSettings.getAlarmMode());
        auth = "";

        btn_auth = (Button) findViewById(R.id.button_settings_auth);
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth = previousSettings.getAuthTag();
            }
        });

        btn_ok = (Button) findViewById(R.id.button_ok_settings);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(view.getContext(), "Changes saved", Toast.LENGTH_SHORT).show();
                createSettings();

            }
        });

        btn_cancel = (Button) findViewById(R.id.button_cancel_settings);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Changes canceled", Toast.LENGTH_SHORT).show();
                createSettings();
            }
        });
    }


    public void createSettings() {

        boolean silentModeStatus = silentMode.isChecked();
        boolean alarmModeStatus = alarmMode.isChecked();


        Settings updatedSettings = new Settings(silentModeStatus, alarmModeStatus, auth);

        Log.i("settings", "sendingData!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Log.i("updateSettings", updatedSettings.toString() );

        Intent i = new Intent();
        i.putExtra(SETTINGS, updatedSettings);
        setResult(RESULT_OK, i);
        finish();

    }
}
