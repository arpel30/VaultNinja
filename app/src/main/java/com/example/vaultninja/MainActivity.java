package com.example.vaultninja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private ImageView main_IMG_vault;
    private ImageView main_IMG_settings;
    private TextView main_LBL_passtxt;
    private TextInputLayout main_TIL_password;
    private Button main_BTN_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();
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
        finish();
    }

    private void login() {
        String password = main_TIL_password.getEditText().getText().toString();
        checkPass(password);
    }

    private void checkPass(String password) {
        if(password.equals("123")){
            main_IMG_settings.setVisibility(View.VISIBLE);
        }else
            main_IMG_settings.setVisibility(View.INVISIBLE);
    }

    private void findViews() {
        main_IMG_vault = findViewById(R.id.main_IMG_vault);
        main_IMG_settings = findViewById(R.id.main_IMG_settings);
        main_LBL_passtxt = findViewById(R.id.main_LBL_passtxt);
        main_TIL_password = findViewById(R.id.main_TIL_password);
        main_BTN_login = findViewById(R.id.main_BTN_login);
    }
}