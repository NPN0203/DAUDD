package com.example.daud;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.daud.model.Article;

public class NewsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        View root = findViewById(R.id.newsDetailRoot);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvSourceTime = findViewById(R.id.tvDetailSourceTime);
        TextView tvContent = findViewById(R.id.tvDetailContent);
        ImageView ivImage = findViewById(R.id.ivDetailImage);

        Article article = (Article) getIntent().getSerializableExtra("article");
        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);

        // Áp dụng chế độ ban đêm cho trang chi tiết
        if (isNightMode) {
            root.setBackgroundColor(Color.BLACK);
            tvTitle.setTextColor(Color.WHITE);
            tvSourceTime.setTextColor(Color.LTGRAY);
            tvContent.setTextColor(Color.WHITE);
        } else {
            root.setBackgroundColor(Color.WHITE);
            tvTitle.setTextColor(Color.BLACK);
            tvSourceTime.setTextColor(Color.GRAY);
            tvContent.setTextColor(Color.parseColor("#333333"));
        }

        if (article != null) {
            tvTitle.setText(article.getTitle());
            tvSourceTime.setText(article.getSource() + " - " + article.getTimeOrComment());
            tvContent.setText(article.getContent());

            if (article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(this).load(article.getImages().get(0)).into(ivImage);
            }
        }
    }
}
