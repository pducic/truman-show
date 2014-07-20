package com.pducic.cardboard;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class ConfigurationActivity extends Activity {

    public static final int PORT = 8765;
    private static final String TAG = ConfigurationActivity.class.getSimpleName();

    private Switch manualSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuration);

        final TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        textIpaddr.setText("Control the box appearance on:\nhttp://" + formatedIpAddress + ":" + PORT);

        manualSwitch = (Switch) findViewById(R.id.manual_switch);
        manualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    textIpaddr.setVisibility(View.VISIBLE);
                }
                else{
                    textIpaddr.setVisibility(View.INVISIBLE);
                }
            }
        });

        Log.i(TAG, formatedIpAddress);

    }


    /** Called when the user clicks the Start button */
    public void startCardboard(View view) {
        if(manualSwitch.isChecked()) {
            startService(new Intent(this, InputActionsService.class));
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.AUTO_GENERATE_OBJECTS, !manualSwitch.isChecked());
        startActivity(intent);
    }
}
