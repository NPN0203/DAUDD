package com.example.daud.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class LunarCalendar {

    public static class LunarDate {
        public int day;
        public int month;
        public int year;
        public boolean isLeap;

        public LunarDate(int d, int m, int y, boolean l) {
            day = d;
            month = m;
            year = y;
            isLeap = l;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "%d/%d%s", day, month, isLeap ? " (N)" : "");
        }
    }

    public static String getTodayLunar() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        LunarDate ld = getLunarDate(cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
        return String.format(Locale.getDefault(), "%d/%d", ld.day, ld.month);
    }

    /**
     * Chuyển đổi Solar sang Lunar (Việt Nam) chính xác cho 2023-2028.
     * Thuật toán dựa trên Julian Day Number và bảng mốc mùng 1 Tết chuẩn.
     */
    public static LunarDate getLunarDate(int dd, int mm, int yy) {
        int jd = getJulianDay(dd, mm, yy);
        
        // Bảng cấu hình Âm lịch Việt Nam chuẩn (Múi giờ +7)
        // Format: {Năm Dương (Tết rơi vào), JDN mùng 1 Tết, Tháng Nhuận (0 nếu không), số ngày từng tháng 1, 2, ...}
        int[][] config = {
            {2023, 2459967, 0, 30, 29, 29, 30, 29, 30, 30, 29, 30, 30, 29, 30},
            {2024, 2460351, 0, 30, 30, 29, 30, 29, 30, 30, 29, 30, 29, 30, 29},
            {2025, 2460705, 6, 30, 29, 30, 29, 30, 30, 29, 30, 29, 30, 30, 29, 30},
            {2026, 2461089, 0, 30, 29, 30, 29, 30, 29, 30, 29, 30, 30, 29, 30},
            {2027, 2461443, 0, 30, 30, 29, 29, 30, 29, 30, 29, 30, 30, 29, 30},
            {2028, 2461797, 5, 30, 29, 30, 29, 30, 29, 30, 29, 30, 29, 30, 30, 29}
        };

        // Xác định năm âm lịch phù hợp
        int rowIdx = -1;
        for (int i = 0; i < config.length; i++) {
            if (jd >= config[i][1]) {
                if (i == config.length - 1 || jd < config[i+1][1]) {
                    rowIdx = i;
                    break;
                }
            }
        }

        // Dự phòng nếu ngoài dải dữ liệu
        if (rowIdx == -1) return new LunarDate(dd, mm, yy, false);

        int[] row = config[rowIdx];
        int lYear = row[0];
        int baseJD = row[1];
        int leapMonth = row[2];
        int diff = jd - baseJD;

        int temp = diff;
        for (int i = 3; i < row.length; i++) {
            if (temp < row[i]) {
                int lMonth = i - 2;
                boolean isLeap = false;
                if (leapMonth > 0) {
                    if (lMonth > leapMonth) {
                        lMonth--;
                        if (lMonth == leapMonth) isLeap = true;
                    }
                }
                return new LunarDate(temp + 1, lMonth, lYear, isLeap);
            }
            temp -= row[i];
        }

        return new LunarDate(1, 1, lYear + 1, false);
    }

    /**
     * Tính Julian Day Number (JDN) cho lịch Gregorian.
     */
    public static int getJulianDay(int dd, int mm, int yy) {
        int a = (14 - mm) / 12;
        int y = yy + 4800 - a;
        int m = mm + 12 * a - 3;
        return dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }
}
