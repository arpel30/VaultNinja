package com.example.vaultninja;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null) {
            Toast.makeText(this, "NO NFC Capabilities",
                    Toast.LENGTH_SHORT).show();
//            finish();
        }
        //Create a PendingIntent object so the Android system can
        //populate it with the details of the tag when it is scanned.
        //PendingIntent.getActivity(Context,requestcode(identifier for
        //                           intent),intent,int)
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        Log.d("aaa", pendingIntent.toString());
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
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            Log.d("aaa", "disabled 2");
        }
        finish();
    }

    private void login() {
//        Log.d("aaa","Volume : " + MySensorsUtils.getVolume(this));
//        Log.d("aaa","Bright : " + MySensorsUtils.getBrightness(this));
//        Log.d("aaa","Sound : " + MySensorsUtils.getSoundMode(this));
//        Log.d("aaa","getHour : " + MySensorsUtils.getHour());
//        Log.d("aaa","getMinute : " + MySensorsUtils.getMinute());
        Log.d("aaa","airplaneMode : " + MySensorsUtils.airplaneMode(this));
        String password = main_TIL_password.getEditText().getText().toString();
        checkPass(password);
    }

    private void checkPass(String password) {
//        Log.d("aaa", MySensorsUtils.battery(this) + "");
        // password = battery percentage - user must scan card & turn on bluetooth
        if (password.equals(MySensorsUtils.battery(this) + "") && MySensorsUtils.bluetooth() && isCardScanned) {
            main_IMG_settings.setVisibility(View.VISIBLE);
            isCardScanned = false;
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
        assert nfcAdapter != null;
        //nfcAdapter.enableForegroundDispatch(context,pendingIntent,
        //                                    intentFilterArray,
        //                                    techListsArray)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
//        Log.d("aaa", "onResume");
    }

    protected void onPause() {
        super.onPause();
        //Onpause stop listening
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
            Log.d("aaa", "disabled");
        }
//        Log.d("aaa", "onPause");
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
//                    Log.d("aaa", "onNewIntent : " + messages[i].toString());
                }
//                Log.d("aaa", "onNewIntent : " + messages);
                // Process the messages array.
            }
        }
        setIntent(intent);
        String id = MySensorsUtils.resolveIntent(intent);
        String savedId = "3749746671";
        if (id.equals(savedId))

            isCardScanned = true;
//            main_LBL_passtxt.setText("Hi moti");
        else
            isCardScanned = false;
//            main_LBL_passtxt.setText("");
        Log.d("aaa", "onNewIntent : " + id);
    }

    //    private String detectTagData(Tag tag) {
//        StringBuilder sb = new StringBuilder();
//        byte[] id = tag.getId();
//        sb.append("ID (hex): ").append(toHex(id)).append('\n');
//        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
//        sb.append("ID (dec): ").append(toDec(id)).append('\n');
//        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');
//
//        String prefix = "android.nfc.tech.";
//        sb.append("Technologies: ");
//        for (String tech : tag.getTechList()) {
//            sb.append(tech.substring(prefix.length()));
//            sb.append(", ");
//        }
//
//        sb.delete(sb.length() - 2, sb.length());
//
//        for (String tech : tag.getTechList()) {
//            if (tech.equals(MifareClassic.class.getName())) {
//                sb.append('\n');
//                String type = "Unknown";
//
//                try {
//                    MifareClassic mifareTag = MifareClassic.get(tag);
//
//                    switch (mifareTag.getType()) {
//                        case MifareClassic.TYPE_CLASSIC:
//                            type = "Classic";
//                            break;
//                        case MifareClassic.TYPE_PLUS:
//                            type = "Plus";
//                            break;
//                        case MifareClassic.TYPE_PRO:
//                            type = "Pro";
//                            break;
//                    }
//                    sb.append("Mifare Classic type: ");
//                    sb.append(type);
//                    sb.append('\n');
//
//                    sb.append("Mifare size: ");
//                    sb.append(mifareTag.getSize() + " bytes");
//                    sb.append('\n');
//
//                    sb.append("Mifare sectors: ");
//                    sb.append(mifareTag.getSectorCount());
//                    sb.append('\n');
//
//                    sb.append("Mifare blocks: ");
//                    sb.append(mifareTag.getBlockCount());
//                } catch (Exception e) {
//                    sb.append("Mifare classic error: " + e.getMessage());
//                }
//            }
//
//            if (tech.equals(MifareUltralight.class.getName())) {
//                sb.append('\n');
//                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
//                String type = "Unknown";
//                switch (mifareUlTag.getType()) {
//                    case MifareUltralight.TYPE_ULTRALIGHT:
//                        type = "Ultralight";
//                        break;
//                    case MifareUltralight.TYPE_ULTRALIGHT_C:
//                        type = "Ultralight C";
//                        break;
//                }
//                sb.append("Mifare Ultralight type: ");
//                sb.append(type);
//            }
//        }
//        Log.v("test",sb.toString());
//        return sb.toString();
//    }
}