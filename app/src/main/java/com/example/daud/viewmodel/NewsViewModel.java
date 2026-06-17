package com.example.daud.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.daud.database.NewsRepository;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.model.Comment;
import com.example.daud.model.Notification;
import com.example.daud.model.UserArticleInteraction;
import java.util.List;

public class NewsViewModel extends AndroidViewModel {
    private NewsRepository repository;
    private MutableLiveData<String> currentCategory = new MutableLiveData<>("Trang chủ");
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private MutableLiveData<Integer> currentUserId = new MutableLiveData<>(-1);
    
    private LiveData<List<Article>> articles;
    private LiveData<List<Category>> categories;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
        
        articles = Transformations.switchMap(currentCategory, category -> 
            Transformations.switchMap(searchQuery, query -> {
                if (query == null || query.trim().isEmpty()) {
                    return repository.getArticlesByCategory(category);
                } else {
                    return repository.searchArticles(query);
                }
            })
        );
        
        categories = repository.getAllCategories();
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }

    public LiveData<Article> getArticleById(int id) {
        return repository.getArticleById(id);
    }

    // Hàm lấy toàn bộ bài báo để làm tin gợi ý
    public LiveData<List<Article>> getAllArticles() {
        return repository.getAllArticles();
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void setCategory(String category) {
        currentCategory.setValue(category);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setUserId(int userId) {
        currentUserId.setValue(userId);
    }

    public LiveData<UserArticleInteraction> getInteraction(int articleId) {
        return Transformations.switchMap(currentUserId, userId -> 
            repository.getInteraction(userId, articleId)
        );
    }

    public void updateInteraction(UserArticleInteraction interaction) {
        repository.updateInteraction(interaction);
    }

    public LiveData<List<Comment>> getComments(int articleId) {
        return repository.getComments(articleId);
    }

    public void addComment(Comment comment) {
        repository.insertComment(comment);
    }

    public void updateComment(Comment comment) {
        repository.updateComment(comment);
    }

    public LiveData<List<Article>> getSavedArticles(int userId) {
        return repository.getSavedArticlesByUser(userId);
    }

    public LiveData<List<Article>> getHistoryArticles(int userId) {
        return repository.getHistoryArticlesByUser(userId);
    }

    public LiveData<List<Notification>> getNotifications() {
        return Transformations.switchMap(currentUserId, userId -> 
            repository.getNotifications(userId)
        );
    }

    public void addNotification(Notification notification) {
        repository.insertNotification(notification);
    }

    public void markNotificationsAsRead() {
        Integer userId = currentUserId.getValue();
        if (userId != null && userId != -1) {
            repository.markNotificationsAsRead(userId);
        }
    }

    public void insertArticles(List<Article> articles) {
        repository.insertArticles(articles);
    }

    public void insertCategories(List<Category> categories) {
        repository.insertCategories(categories);
    }

    public void updateArticle(Article article) {
        repository.updateArticle(article);
    }
}
