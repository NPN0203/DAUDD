package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticle(Article article);

    @Query("SELECT * FROM articles ORDER BY id DESC")
    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM articles WHERE id = :id")
    LiveData<Article> getArticleById(int id);

    @Query("SELECT * FROM articles WHERE source = :categoryName")
    LiveData<List<Article>> getArticlesByCategory(String categoryName);

    @Query("SELECT * FROM articles WHERE title LIKE :query OR source LIKE :query")
    LiveData<List<Article>> searchArticles(String query);

    @Query("SELECT a.* FROM articles a INNER JOIN user_article_interactions uai ON a.id = uai.articleId WHERE uai.userId = :userId AND uai.isSaved = 1")
    LiveData<List<Article>> getSavedArticlesByUser(int userId);

    @Query("SELECT a.* FROM articles a INNER JOIN user_article_interactions uai ON a.id = uai.articleId WHERE uai.userId = :userId AND uai.lastReadTime > 0 ORDER BY uai.lastReadTime DESC")
    LiveData<List<Article>> getHistoryArticlesByUser(int userId);

    @Update
    void updateArticle(Article article);

    @Delete
    void deleteArticle(Article article);

    @Query("DELETE FROM articles")
    void deleteAll();
}
