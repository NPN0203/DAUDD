package com.example.daud.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.daud.model.Notification;
import java.util.List;

@Dao
public interface NotificationDao {
    @Insert
    void insertNotification(Notification notification);

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    LiveData<List<Notification>> getNotificationsForUser(int userId);

    @Update
    void updateNotification(Notification notification);

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    void markAllAsRead(int userId);
}
