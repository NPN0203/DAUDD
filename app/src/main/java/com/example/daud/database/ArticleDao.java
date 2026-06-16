package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.daud.model.Article;
import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM articles WHERE source = :categoryName")
    LiveData<List<Article>> getArticlesByCategory(String categoryName);

    @Query("SELECT * FROM articles WHERE isSaved = 1")
    LiveData<List<Article>> getSavedArticles();

    @Query("SELECT * FROM articles WHERE lastReadTime > 0 ORDER BY lastReadTime DESC")
    LiveData<List<Article>> getHistoryArticles();

    @Update
    void updateArticle(Article article);

    @Query("DELETE FROM articles")
    void deleteAll();
}
