package com.example.daud;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.daud.database.AppDatabase;
import com.example.daud.model.Article;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddNewsActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);

        EditText etAdminTitle = findViewById(R.id.etAdminTitle);
        EditText etAdminSource = findViewById(R.id.etAdminSource);
        EditText etAdminImage = findViewById(R.id.etAdminImage);
        EditText etAdminContent = findViewById(R.id.etAdminContent);
        Button btnSaveNews = findViewById(R.id.btnSaveNews);

        btnSaveNews.setOnClickListener(v -> {
            String title = etAdminTitle.getText().toString().trim();
            String source = etAdminSource.getText().toString().trim();
            String imageUrl = etAdminImage.getText().toString().trim();
            String content = etAdminContent.getText().toString().trim();

            if (title.isEmpty() || source.isEmpty() || imageUrl.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Article article = new Article(
                    title,
                    source,
                    "Vừa xong",
                    Collections.singletonList(imageUrl),
                    Article.TYPE_ONE_IMAGE,
                    content
            );

            executorService.execute(() -> {
                AppDatabase.getInstance(this).articleDao().insertArticle(article);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã thêm bài báo thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
