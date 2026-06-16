package com.example.daud;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.daud.database.AppDatabase;
import com.example.daud.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etPassword;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sửa lỗi Ambiguous method call bằng cách gọi qua super
        super.setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        findViewById(R.id.btnRegister).setOnClickListener(v -> register());
        
        if (tvGoToLogin != null) {
            tvGoToLogin.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
        }
    }

    private void register() {
        String fullName = etFullName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User existingUser = db.userDao().getUserByUsername(username);
            if (existingUser != null) {
                runOnUiThread(() -> Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show());
            } else {
                // Mặc định tài khoản "admin" sẽ là quản trị viên
                boolean isAdmin = username.equalsIgnoreCase("admin");
                db.userDao().register(new User(username, password, fullName, isAdmin));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
