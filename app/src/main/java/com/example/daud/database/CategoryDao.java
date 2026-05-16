package com.example.daud.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.daud.model.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> categories);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("UPDATE categories SET isSelected = :selected WHERE name = :name")
    void updateCategorySelection(String name, boolean selected);
}
