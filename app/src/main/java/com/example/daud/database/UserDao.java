package com.example.daud.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.daud.model.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Insert
    void register(User user);
}
