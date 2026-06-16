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
import java.util.List;

public class NewsViewModel extends AndroidViewModel {
    private NewsRepository repository;
    private MutableLiveData<String> currentCategory = new MutableLiveData<>("Trang chủ");
    private LiveData<List<Article>> articles;
    private LiveData<List<Category>> categories;
    private LiveData<List<Article>> savedArticles;
    private LiveData<List<Article>> historyArticles;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(application);
        
        articles = Transformations.switchMap(currentCategory, category -> 
            repository.getArticlesByCategory(category)
        );
        
        categories = repository.getAllCategories();
        savedArticles = repository.getSavedArticles();
        historyArticles = repository.getHistoryArticles();
    }

    public LiveData<List<Article>> getArticles() {
        return articles;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Article>> getSavedArticles() {
        return savedArticles;
    }

    public LiveData<List<Article>> getHistoryArticles() {
        return historyArticles;
    }

    public void setCategory(String category) {
        currentCategory.setValue(category);
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
