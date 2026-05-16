package com.example.daud.database;

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
    List<Article> getAllArticles();

    @Query("DELETE FROM articles")
    void deleteAll();
}
