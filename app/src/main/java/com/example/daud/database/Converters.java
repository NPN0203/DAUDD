package com.example.daud.database;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        // .trim() để xóa khoảng trắng dư thừa ở đầu/cuối mỗi link ảnh
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null) return null;
        return list.stream().collect(Collectors.joining(","));
    }
}
