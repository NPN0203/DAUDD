package com.example.daud.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsRepository {
    private ArticleDao articleDao;
    private CategoryDao categoryDao;
    private ExecutorService executorService;

    public NewsRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        articleDao = db.articleDao();
        categoryDao = db.categoryDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Article>> getAllArticles() {
        return articleDao.getAllArticles();
    }

    public LiveData<List<Article>> getArticlesByCategory(String category) {
        if (category.equals("Trang chủ")) return articleDao.getAllArticles();
        return articleDao.getArticlesByCategory(category);
    }

    public LiveData<List<Article>> getSavedArticles() {
        return articleDao.getSavedArticles();
    }

    public LiveData<List<Article>> getHistoryArticles() {
        return articleDao.getHistoryArticles();
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public void insertArticles(List<Article> articles) {
        executorService.execute(() -> articleDao.insertArticles(articles));
    }

    public void insertCategories(List<Category> categories) {
        executorService.execute(() -> categoryDao.insertCategories(categories));
    }

    public void updateArticle(Article article) {
        executorService.execute(() -> articleDao.updateArticle(article));
    }
}
