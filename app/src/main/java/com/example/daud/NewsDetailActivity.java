package com.example.daud;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.daud.model.Article;
import com.example.daud.viewmodel.NewsViewModel;

public class NewsDetailActivity extends AppCompatActivity {

    private NewsViewModel viewModel;
    private Article article;
    private ImageView ivSave;
    private TextView tvSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        View root = findViewById(R.id.newsDetailRoot);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvSourceTime = findViewById(R.id.tvDetailSourceTime);
        TextView tvContent = findViewById(R.id.tvDetailContent);
        ImageView ivImage = findViewById(R.id.ivDetailImage);
        
        ImageView btnBack = findViewById(R.id.btnBack);
        View topBar = findViewById(R.id.topBar);
        LinearLayout btnSave = findViewById(R.id.btnSave);
        LinearLayout btnShare = findViewById(R.id.btnShare);
        ivSave = findViewById(R.id.ivSave);
        tvSave = findViewById(R.id.tvSave);
        View bottomBar = findViewById(R.id.bottomBar);

        article = (Article) getIntent().getSerializableExtra("article");
        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);

        // Xử lý chế độ ban đêm
        if (isNightMode) {
            root.setBackgroundColor(Color.BLACK);
            tvTitle.setTextColor(Color.WHITE);
            tvSourceTime.setTextColor(Color.LTGRAY);
            tvContent.setTextColor(Color.WHITE);
            bottomBar.setBackgroundColor(Color.parseColor("#1A1A1A"));
            if (topBar != null) topBar.setBackgroundColor(Color.BLACK);
            if (btnBack != null) btnBack.setColorFilter(Color.WHITE);
        } else {
            root.setBackgroundColor(Color.WHITE);
            tvTitle.setTextColor(Color.BLACK);
            tvSourceTime.setTextColor(Color.GRAY);
            tvContent.setTextColor(Color.parseColor("#333333"));
            bottomBar.setBackgroundColor(Color.WHITE);
            if (topBar != null) topBar.setBackgroundColor(Color.WHITE);
            if (btnBack != null) btnBack.setColorFilter(Color.BLACK);
        }

        // 1. CODE ĐỂ QUAY LẠI: Đặt ở đây để luôn hoạt động
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (article != null) {
            // Cập nhật thời gian đọc bài
            article.setLastReadTime(System.currentTimeMillis());
            viewModel.updateArticle(article);

            tvTitle.setText(article.getTitle());
            tvSourceTime.setText(String.format("%s - %s", article.getSource(), article.getTimeOrComment()));
            tvContent.setText(article.getContent());
            updateSaveUI();

            if (article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(this).load(article.getImages().get(0)).into(ivImage);
            }

            // Xử lý nút lưu bài báo
            btnSave.setOnClickListener(v -> {
                article.setSaved(!article.isSaved());
                viewModel.updateArticle(article);
                updateSaveUI();
                String msg = article.isSaved() ? "Đã lưu bài báo" : "Đã bỏ lưu bài báo";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });

            // Xử lý nút chia sẻ
            btnShare.setOnClickListener(v -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n\n" + article.getContent());
                startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài báo qua"));
            });
        }
    }

    private void updateSaveUI() {
        if (article.isSaved()) {
            ivSave.setImageResource(android.R.drawable.ic_menu_save);
            ivSave.setColorFilter(Color.parseColor("#D32F2F"));
            tvSave.setTextColor(Color.parseColor("#D32F2F"));
            tvSave.setText("Đã lưu");
        } else {
            ivSave.setImageResource(android.R.drawable.ic_menu_save);
            ivSave.setColorFilter(Color.GRAY);
            tvSave.setTextColor(Color.GRAY);
            tvSave.setText("Lưu");
        }
    }
}
