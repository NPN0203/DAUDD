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

public class SavedArticlesActivity extends AppCompatActivity {

    private NewsAdapter adapter;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rvSaved = findViewById(R.id.rvSavedArticles);
        rvSaved.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new NewsAdapter(new ArrayList<>());
        adapter.setNightMode(isNightMode);
        rvSaved.setAdapter(adapter);

        applyNightMode(isNightMode);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        
        if (userId != -1) {
            viewModel.getSavedArticles(userId).observe(this, articles -> {
                if (articles != null) {
                    adapter.setArticleList(articles);
                }
            });
        }
    }

    private void applyNightMode(boolean isNightMode) {
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        View root = findViewById(R.id.savedRoot);
        if (root != null) root.setBackgroundColor(bgColor);
        
        TextView tvTitle = findViewById(R.id.tvSavedHeaderTitle);
        if (tvTitle != null) tvTitle.setTextColor(isNightMode ? Color.WHITE : Color.BLACK);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setColorFilter(isNightMode ? Color.WHITE : Color.BLACK);
    }
}
