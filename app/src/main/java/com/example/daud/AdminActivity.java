package com.example.daud;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.AdminNewsAdapter;
import com.example.daud.database.AppDatabase;
import com.example.daud.model.Article;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Toolbar toolbar = findViewById(R.id.toolbarAdmin);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        RecyclerView rvAdminNews = findViewById(R.id.rvAdminNews);
        Button btnResetData = findViewById(R.id.btnResetData);
        Button btnAddNews = findViewById(R.id.btnAddNews);

        NewsViewModel viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        rvAdminNews.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                AdminNewsAdapter adapter = new AdminNewsAdapter(articles, this::confirmDelete);
                rvAdminNews.setAdapter(adapter);
            }
        });

        btnAddNews.setOnClickListener(v -> startActivity(new Intent(this, AddNewsActivity.class)));

        btnResetData.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ dữ liệu bài báo?")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> executorService.execute(() -> {
                    AppDatabase.getInstance(this).articleDao().deleteAll();
                    runOnUiThread(() -> Toast.makeText(this, "Đã xóa toàn bộ dữ liệu bài báo", Toast.LENGTH_SHORT).show());
                }))
                .setNegativeButton("Hủy", null)
                .show());
    }

    private void confirmDelete(Article article) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa bài báo")
                .setMessage("Bạn có chắc chắn muốn xóa bài: " + article.getTitle() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> executorService.execute(() -> {
                    AppDatabase.getInstance(this).articleDao().deleteArticle(article);
                    runOnUiThread(() -> Toast.makeText(this, "Đã xóa bài báo", Toast.LENGTH_SHORT).show());
                }))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
