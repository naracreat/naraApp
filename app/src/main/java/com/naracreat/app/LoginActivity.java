package com.naracreat.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUser;
    private Button btnDoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        btnDoLogin = findViewById(R.id.btnDoLogin);

        btnDoLogin.setOnClickListener(v -> {

            String email = etUser.getText() == null ? "" : etUser.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Masukkan email", Toast.LENGTH_SHORT).show();
                return;
            }

            // password dummy (tidak perlu daftar)
            Session.login(this, email, "local");

            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
