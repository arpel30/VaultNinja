package com.example.vaultninja;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.BatteryManager;
import android.provider.CallLog;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.BATTERY_SERVICE;

public class MySensorsUtils {

    // get the domain of the first user's email - example : mosheyomtov12@gmail.com -> mosheyomtov12
    public static String getUserName(Context context) {
        Account[] accounts = AccountManager.get(context).getAccounts();
        if (accounts.length > 0) {
            String domain = getDomain(accounts[0].name);
            return domain;
        }
        return "";
    }

    private static String getDomain(String email) {
        String domain = "";
        for (int i = 0; i < email.length(); i++) {
            if (email.charAt(i) == '@')
                break;
            domain+=email.charAt(i);
        }
        return domain;
    }

    // check if bluetooth is on
    public static boolean bluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return false; // bluetooth not supported
        } else if (!mBluetoothAdapter.isEnabled()) {
            return false; // Bluetooth is not enabled
        } else {
            return true; // Bluetooth is enabled
        }
    }
    // check if airplane mode is on
    public static boolean airplaneMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    // get battery %
    public static int battery(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    // get NFC tag
    public static String resolveIntent(Intent intent) {
        String action = intent.getAction();
        String payload = null;
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            assert tag != null;
            payload = detectTagData(tag);
        }
        return payload;
    }

    public static String readTag(MifareUltralight mifareUlTag) {
        try {
            mifareUlTag.connect();
            byte[] payload = mifareUlTag.readPages(4);
            return new String(payload, Charset.forName("US-ASCII"));
        } catch (IOException e) {
            Log.d("aaa", "IOException while reading MifareUltralight message...", e);
        } finally {
            if (mifareUlTag != null) {
                try {
                    mifareUlTag.close();
                } catch (IOException e) {
                    Log.d("aaa", "Error closing tag...", e);
                }
            }
        }
        return null;
    }

    private static String detectTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append(toDec(id));
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareUltralight.class.getName())) {
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                readTag(mifareUlTag);
            }
        }
        return sb.toString();
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private static long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    // get music volume %
    public static int getVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        // Get the music current volume level
        int music_volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        // Get the device music maximum volume level
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return (music_volume_level*100)/max;
    }

    // get brightness %
    public static int getBrightness(Context context)  {
        int max = 255;
        try {
            return (Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS)*100)/max;
        }catch (Settings.SettingNotFoundException e){
            return -1;
        }
    }

    // get sound mode - sound, silent, vibrate
    public static int getSoundMode(Context context)  {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode();
    }

    // get current hour - 24h, example : 12:46 -> 12
    public static String getHour( )  {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String str = sdf.format(new Date());
        return str;
    }

    // get current minute, example : 12:46 -> 46
    public static String getMinute( )  {
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        String str = sdf.format(new Date());
        return str;

    }

}
