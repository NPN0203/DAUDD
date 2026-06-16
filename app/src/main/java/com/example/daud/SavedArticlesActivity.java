package com.example.daud;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;

public class SavedArticlesActivity extends AppCompatActivity {

    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        // Nhận trạng thái Night Mode từ Intent
        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);
        applyNightMode(isNightMode);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rvSaved = findViewById(R.id.rvSavedArticles);
        rvSaved.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NewsAdapter(new ArrayList<>());
        adapter.setNightMode(isNightMode);
        rvNewsAdapterSetNightMode(isNightMode); // Đảm bảo adapter biết chế độ hiển thị
        rvSaved.setAdapter(adapter);

        NewsViewModel viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        viewModel.getSavedArticles().observe(this, articles -> {
            if (articles != null) {
                adapter.setArticleList(articles);
            }
        });
    }

    private void rvNewsAdapterSetNightMode(boolean isNightMode) {
        if (adapter != null) {
            adapter.setNightMode(isNightMode);
        }
    }

    private void applyNightMode(boolean isNightMode) {
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        findViewById(android.R.id.content).getRootView().setBackgroundColor(bgColor);
    }
}
