package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.daud.model.Article;
import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles(); // Trả về LiveData để quan sát biến động

    @Query("SELECT * FROM articles WHERE source = :categoryName")
    LiveData<List<Article>> getArticlesByCategory(String categoryName);

    @Query("DELETE FROM articles")
    void deleteAll();
}
