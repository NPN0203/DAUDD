package com.example.daud;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NotificationAdapter;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    private NotificationAdapter adapter;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);
        viewModel.setUserId(userId);

        findViewById(R.id.btnBackNotify).setOnClickListener(v -> finish());

        RecyclerView rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(new ArrayList<>());
        rvNotifications.setAdapter(adapter);

        if (userId != -1) {
            viewModel.getNotifications().observe(this, notifications -> {
                if (notifications != null) {
                    adapter.setNotifications(notifications);
                }
            });
            // Đánh dấu đã đọc khi thoát ra hoặc khi vào xem
            viewModel.markNotificationsAsRead();
        }
    }
}
