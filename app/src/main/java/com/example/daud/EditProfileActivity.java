package com.example.daud;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.example.daud.database.AppDatabase;
import com.example.daud.model.User;
import com.example.daud.model.Notification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPassword;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String CHANNEL_ID = "DAUD_NOTIFY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etFullName = findViewById(R.id.etEditFullName);
        etPassword = findViewById(R.id.etEditPassword);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        etFullName.setText(pref.getString("fullName", ""));

        findViewById(R.id.btnBackEdit).setOnClickListener(v -> finish());
        findViewById(R.id.btnSaveProfile).setOnClickListener(v -> saveChanges());
        
        createNotificationChannel();
    }

    private void saveChanges() {
        String newName = etFullName.getText().toString().trim();
        String newPass = etPassword.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        executorService.execute(() -> {
            User user = AppDatabase.getInstance(this).userDao().getUserById(userId);
            if (user != null) {
                user.setFullName(newName);
                boolean passwordChanged = false;
                if (!newPass.isEmpty()) {
                    user.setPassword(newPass);
                    passwordChanged = true;
                }
                AppDatabase.getInstance(this).userDao().updateUser(user);

                // Nếu có đổi mật khẩu, chèn thông báo vào Database
                if (passwordChanged) {
                    String currentTime = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    Notification notify = new Notification(
                            userId,
                            "Bảo mật tài khoản",
                            "Bạn đã đổi mật khẩu thành công lúc " + currentTime,
                            System.currentTimeMillis()
                    );
                    AppDatabase.getInstance(this).notificationDao().insertNotification(notify);
                }

                runOnUiThread(() -> {
                    pref.edit().putString("fullName", newName).apply();
                    Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    sendNotification("Hồ sơ của bạn đã được cập nhật thành công!");
                    finish();
                });
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Thông báo hệ thống";
            String description = "Thông báo cập nhật tài khoản";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("DAUD News")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
