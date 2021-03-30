package com.example.vaultninja;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private ImageView main_IMG_vault;
    private ImageView main_IMG_settings;
    private TextView main_LBL_passtxt;
    private TextInputLayout main_TIL_password;
    private Button main_BTN_login;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;

    private boolean isCardScanned = false;
    private String cardId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //TODO glide images
        main_IMG_settings.setVisibility(View.INVISIBLE);
        main_IMG_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        main_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void openSettings() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
        finish();
    }

    private void login() {
        String password = main_TIL_password.getEditText().getText().toString();
        checkPass(password);
    }

    private void checkPass(String password) {

        // password = username + battery percentage + minutes + hours - user need to scan card, turn off bluetooth, turn on airplane mode, brightness < 50%, soundmode = vibrate and music volume > 50%
        String myPass = "" + MySensorsUtils.getUserName(this) + MySensorsUtils.battery(this) + MySensorsUtils.getMinute() + MySensorsUtils.getHour();
        String cardSavedId = MySPV.getInstance().getString(Constants.NFC_KEY, "def");
        boolean nfc_valid = false;
        if(cardSavedId.equals("def") || (isCardScanned && cardSavedId.equals(cardId))){ // nfc is not set by user or card is scanned and nfc is match
            nfc_valid = true;
        }
        if (password.equals(myPass) && !MySensorsUtils.bluetooth() && MySensorsUtils.airplaneMode(this) && MySensorsUtils.getBrightness(this) <  50 &&
                MySensorsUtils.getSoundMode(this) == AudioManager.RINGER_MODE_VIBRATE && MySensorsUtils.getVolume(this) > 50 && nfc_valid) {
            main_IMG_settings.setVisibility(View.VISIBLE);
            isCardScanned = false;
            Toast.makeText(this, "Phone is unlocked !", Toast.LENGTH_SHORT);
        } else
            main_IMG_settings.setVisibility(View.INVISIBLE);
    }

    private void findViews() {
        main_IMG_vault = findViewById(R.id.main_IMG_vault);
        main_IMG_settings = findViewById(R.id.main_IMG_settings);
        main_LBL_passtxt = findViewById(R.id.main_LBL_passtxt);
        main_TIL_password = findViewById(R.id.main_TIL_password);
        main_BTN_login = findViewById(R.id.main_BTN_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    protected void onPause() {
        super.onPause();
        //Onpause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            Log.d("aaa", "disabled");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
            }
        }
        setIntent(intent);
        cardId = MySensorsUtils.resolveIntent(intent);
        isCardScanned = true;
    }
}