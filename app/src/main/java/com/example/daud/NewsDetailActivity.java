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

        // Khởi tạo các View
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

        // Nhận dữ liệu
        article = (Article) getIntent().getSerializableExtra("article");
        boolean isNightMode = getIntent().getBooleanExtra("nightMode", false);

        // Áp dụng Night Mode (Kiểm tra null cho chắc chắn)
        if (root != null) {
            if (isNightMode) {
                root.setBackgroundColor(Color.BLACK);
                if (tvTitle != null) tvTitle.setTextColor(Color.WHITE);
                if (tvSourceTime != null) tvSourceTime.setTextColor(Color.LTGRAY);
                if (tvContent != null) tvContent.setTextColor(Color.WHITE);
                if (bottomBar != null) bottomBar.setBackgroundColor(Color.parseColor("#1A1A1A"));
                if (topBar != null) topBar.setBackgroundColor(Color.BLACK);
                if (btnBack != null) btnBack.setColorFilter(Color.WHITE);
            } else {
                root.setBackgroundColor(Color.WHITE);
                if (tvTitle != null) tvTitle.setTextColor(Color.BLACK);
                if (tvSourceTime != null) tvSourceTime.setTextColor(Color.GRAY);
                if (tvContent != null) tvContent.setTextColor(Color.parseColor("#333333"));
                if (bottomBar != null) bottomBar.setBackgroundColor(Color.WHITE);
                if (topBar != null) topBar.setBackgroundColor(Color.WHITE);
                if (btnBack != null) btnBack.setColorFilter(Color.BLACK);
            }
        }

        // Xử lý nút Back (Kiểm tra null để không bị văng app)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (article != null) {
            // Cập nhật dữ liệu lên giao diện
            if (tvTitle != null) tvTitle.setText(article.getTitle());
            if (tvSourceTime != null) tvSourceTime.setText(String.format("%s - %s", article.getSource(), article.getTimeOrComment()));
            if (tvContent != null) tvContent.setText(article.getContent());
            
            updateSaveUI();

            if (ivImage != null && article.getImages() != null && !article.getImages().isEmpty()) {
                Glide.with(this).load(article.getImages().get(0)).into(ivImage);
            }

            // Cập nhật lịch sử đọc
            article.setLastReadTime(System.currentTimeMillis());
            viewModel.updateArticle(article);

            // Xử lý nút Save
            if (btnSave != null) {
                btnSave.setOnClickListener(v -> {
                    article.setSaved(!article.isSaved());
                    viewModel.updateArticle(article);
                    updateSaveUI();
                    Toast.makeText(this, article.isSaved() ? "Đã lưu" : "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
                });
            }

            // Xử lý nút Share
            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, article.getTitle() + "\n" + article.getContent());
                    startActivity(Intent.createChooser(intent, "Chia sẻ"));
                });
            }
        }
    }

    private void updateSaveUI() {
        if (article != null && ivSave != null && tvSave != null) {
            if (article.isSaved()) {
                ivSave.setColorFilter(Color.parseColor("#D32F2F"));
                tvSave.setTextColor(Color.parseColor("#D32F2F"));
                tvSave.setText("Đã lưu");
            } else {
                ivSave.setColorFilter(Color.GRAY);
                tvSave.setTextColor(Color.GRAY);
                tvSave.setText("Lưu");
            }
        }
    }
}
