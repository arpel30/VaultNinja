package com.example.vaultninja.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaultninja.Other.Constants;
import com.example.vaultninja.Utils.MySPV;
import com.example.vaultninja.Utils.MySensorsUtils;
import com.example.vaultninja.R;

public class SettingsActivity extends AppCompatActivity {

    private TextView settings_LBL_txt;
    private TextView settings_LBL_scantxt;
    private Button settings_BTN_scan;
    private Button settings_BTN_remove;
    private boolean isCardScanned = false;
    private boolean isWaitingForScan = false;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        initViews();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    private void initViews() {
        settings_LBL_scantxt.setText(Constants.HINT_TXT);
        settings_BTN_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWaitingForScan = true;
                settings_LBL_scantxt.setText(Constants.WAITING_TXT);
            }
        });
        settings_BTN_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySPV.getInstance().removeKey(Constants.NFC_KEY);
                settings_LBL_scantxt.setText(Constants.REMOVE_TXT);
            }
        });
    }

    private void findViews() {
        settings_LBL_txt = findViewById(R.id.settings_LBL_txt);
        settings_LBL_scantxt = findViewById(R.id.settings_LBL_scantxt);
        settings_BTN_scan = findViewById(R.id.settings_BTN_scan);
        settings_BTN_remove = findViewById(R.id.settings_BTN_remove);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(isWaitingForScan){
            String id = MySensorsUtils.resolveIntent(intent);
            isWaitingForScan = false;
            settings_LBL_scantxt.setText(Constants.SCAN_TXT);
            MySPV.getInstance().putString(Constants.NFC_KEY, id);
        }else{
            settings_LBL_scantxt.setText(Constants.HINT_TXT);
        }
    }
}