package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.daud.model.UserArticleInteraction;
import java.util.List;

@Dao
public interface UserArticleInteractionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserArticleInteraction interaction);

    @Query("SELECT * FROM user_article_interactions WHERE userId = :userId AND articleId = :articleId LIMIT 1")
    UserArticleInteraction getInteraction(int userId, int articleId);

    @Query("SELECT * FROM user_article_interactions WHERE userId = :userId AND articleId = :articleId LIMIT 1")
    LiveData<UserArticleInteraction> getInteractionLive(int userId, int articleId);

    @Query("SELECT articleId FROM user_article_interactions WHERE userId = :userId AND isSaved = 1")
    LiveData<List<Integer>> getSavedArticleIds(int userId);

    @Query("SELECT articleId FROM user_article_interactions WHERE userId = :userId AND lastReadTime > 0 ORDER BY lastReadTime DESC")
    LiveData<List<Integer>> getHistoryArticleIds(int userId);
}
