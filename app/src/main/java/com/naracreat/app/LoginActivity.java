package com.naracreat.app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUser = findViewById(R.id.etUser);
        Button btn = findViewById(R.id.btnDoLogin);

        btn.setOnClickListener(v -> {
            String u = etUser.getText().toString().trim();
            if (u.isEmpty()) {
                Toast.makeText(this, "Isi username dulu", Toast.LENGTH_SHORT).show();
                return;
            }
            Session.login(this, u);
            Toast.makeText(this, "Login OK", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
