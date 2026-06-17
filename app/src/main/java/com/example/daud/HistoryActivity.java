package com.example.daud;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private NewsAdapter adapter;
    private TextView tvHistoryCount;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);
        applyNightMode(isNightMode);

        ImageView btnBack = findViewById(R.id.btnBack);
        tvHistoryCount = findViewById(R.id.tvHistoryCount);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NewsAdapter(new ArrayList<>());
        adapter.setNightMode(isNightMode);
        rvHistory.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        
        if (userId != -1) {
            viewModel.getHistoryArticles(userId).observe(this, articles -> {
                if (articles != null) {
                    adapter.setArticleList(articles);
                    if (articles.isEmpty()) {
                        tvHistoryCount.setText("Chưa có lịch sử đọc");
                    } else {
                        tvHistoryCount.setText(String.format(Locale.getDefault(), "Đã đọc %d bài", articles.size()));
                    }
                }
            });
        } else {
            tvHistoryCount.setText("Vui lòng đăng nhập để xem lịch sử");
        }
    }

    private void applyNightMode(boolean isNightMode) {
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        
        View root = findViewById(R.id.historyRoot); // Assuming there's a root ID or use android.R.id.content
        if (root != null) root.setBackgroundColor(bgColor);
        else getWindow().getDecorView().setBackgroundColor(bgColor);
        
        TextView tvTitle = findViewById(R.id.tvHistoryHeaderTitle);
        if (tvTitle != null) tvTitle.setTextColor(textColor);

        if (tvHistoryCount != null) {
            tvHistoryCount.setTextColor(isNightMode ? Color.GRAY : Color.parseColor("#777777"));
        }
    }
}
