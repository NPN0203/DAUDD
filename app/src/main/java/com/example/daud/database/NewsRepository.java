package com.example.daud.database;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.model.Comment;
import com.example.daud.model.UserArticleInteraction;
import com.example.daud.model.Notification;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsRepository {
    private ArticleDao articleDao;
    private CategoryDao categoryDao;
    private CommentDao commentDao;
    private UserArticleInteractionDao interactionDao;
    private NotificationDao notificationDao;
    private ExecutorService executorService;

    public NewsRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        articleDao = db.articleDao();
        categoryDao = db.categoryDao();
        commentDao = db.commentDao();
        interactionDao = db.userArticleInteractionDao();
        notificationDao = db.notificationDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<List<Article>> getAllArticles() {
        return articleDao.getAllArticles();
    }

    // Bổ sung phương thức bị thiếu
    public LiveData<Article> getArticleById(int id) {
        return articleDao.getArticleById(id);
    }

    public LiveData<List<Article>> getArticlesByCategory(String category) {
        if (category == null || category.equals("Trang chủ")) return articleDao.getAllArticles();
        return articleDao.getArticlesByCategory(category);
    }

    public LiveData<List<Article>> searchArticles(String query) {
        return articleDao.searchArticles("%" + query + "%");
    }

    public LiveData<UserArticleInteraction> getInteraction(int userId, int articleId) {
        return interactionDao.getInteractionLive(userId, articleId);
    }

    public void updateInteraction(UserArticleInteraction interaction) {
        executorService.execute(() -> interactionDao.insertOrUpdate(interaction));
    }

    public LiveData<List<Comment>> getComments(int articleId) {
        return commentDao.getCommentsForArticle(articleId);
    }

    public void insertComment(Comment comment) {
        executorService.execute(() -> commentDao.insertComment(comment));
    }

    public void updateComment(Comment comment) {
        executorService.execute(() -> commentDao.updateComment(comment));
    }

    public LiveData<List<Article>> getSavedArticlesByUser(int userId) {
        return articleDao.getSavedArticlesByUser(userId);
    }

    public LiveData<List<Article>> getHistoryArticlesByUser(int userId) {
        return articleDao.getHistoryArticlesByUser(userId);
    }

    public LiveData<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    public LiveData<List<Notification>> getNotifications(int userId) {
        return notificationDao.getNotificationsForUser(userId);
    }

    public void insertNotification(Notification notification) {
        executorService.execute(() -> notificationDao.insertNotification(notification));
    }

    public void markNotificationsAsRead(int userId) {
        executorService.execute(() -> notificationDao.markAllAsRead(userId));
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
