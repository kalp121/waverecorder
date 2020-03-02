package com.danielkim.soundrecorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnOk;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnOk = (Button) findViewById(R.id.btnOk);
        etName = (EditText) findViewById(R.id.etName);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Please Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }

                MySharedPreferences.setUserName(LoginActivity.this, name);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
