package com.example.daud;

import android.graphics.Color;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Nhận trạng thái Night Mode
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

        NewsViewModel viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        viewModel.getHistoryArticles().observe(this, articles -> {
            if (articles != null) {
                adapter.setArticleList(articles);
                if (articles.isEmpty()) {
                    tvHistoryCount.setText("Hôm nay chưa đọc bài nào");
                } else {
                    String text = String.format(Locale.getDefault(), "Hôm nay đã đọc %d bài", articles.size());
                    tvHistoryCount.setText(text);
                }
            }
        });
    }

    private void applyNightMode(boolean isNightMode) {
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        int secondaryBg = isNightMode ? Color.parseColor("#121212") : Color.parseColor("#F9F9F9");

        // Áp dụng màu nền cho root (cần ID trong layout hoặc dùng content view)
        getWindow().getDecorView().setBackgroundColor(bgColor);
        
        TextView tvTitle = findViewById(R.id.tvHistoryHeaderTitle);
        if (tvTitle != null) tvTitle.setTextColor(textColor);

        ImageView ivBack = findViewById(R.id.btnBack);
        if (ivBack != null) ivBack.setColorFilter(textColor);

        if (tvHistoryCount != null) {
            tvHistoryCount.setBackgroundColor(secondaryBg);
            tvHistoryCount.setTextColor(isNightMode ? Color.GRAY : Color.parseColor("#777777"));
        }
    }
}
