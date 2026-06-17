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

public class LikedArticlesActivity extends AppCompatActivity {
    private NewsAdapter adapter;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_articles);

        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);
        applyNightMode(isNightMode);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        RecyclerView rv = findViewById(R.id.rvLikedArticles);
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NewsAdapter(new ArrayList<>());
        adapter.setNightMode(isNightMode);
        rv.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            viewModel.getLikedArticles(userId).observe(this, articles -> {
                if (articles != null) adapter.setArticleList(articles);
            });
        }
    }

    private void applyNightMode(boolean isNightMode) {
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        findViewById(R.id.likedRoot).setBackgroundColor(bgColor);
        ((TextView) findViewById(R.id.tvHeaderTitle)).setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
        ((ImageView) findViewById(R.id.btnBack)).setColorFilter(isNightMode ? Color.WHITE : Color.BLACK);
    }
}
