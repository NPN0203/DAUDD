package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.daud.model.Comment;
import java.util.List;

@Dao
public interface CommentDao {
    @Insert
    void insertComment(Comment comment);

    @Query("SELECT * FROM comments WHERE articleId = :articleId ORDER BY timestamp ASC")
    LiveData<List<Comment>> getCommentsForArticle(int articleId);

    @Query("SELECT * FROM comments WHERE parentId = :parentId ORDER BY timestamp ASC")
    LiveData<List<Comment>> getRepliesForComment(int parentId);

    @Update
    void updateComment(Comment comment);
}
