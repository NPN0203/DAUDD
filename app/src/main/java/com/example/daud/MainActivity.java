package com.example.daud;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.util.LunarCalendar;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private ConstraintLayout mainLayout;
    private ConstraintLayout homeContainer;
    private View profileContainer;
    private View channelsContainer;
    private View exploreContainer;

    private LinearLayout btnNavHome, btnNavProfile, btnNavExplore;
    private ImageView ivNavHome, ivNavProfile, ivNavExplore;
    private TextView tvNavHome, tvNavProfile, tvNavExplore;

    private ImageView btnOpenChannels, btnCloseChannels;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;
    private TextView utLunar;

    private NewsViewModel viewModel;
    private boolean isNightMode = false;
    private final Calendar currentCalendar = Calendar.getInstance();

    private final int[] allChannelIds = {
            R.id.chanTrangChu, R.id.chanBongDa, R.id.chanXaHoi, R.id.chanGiaiTri, R.id.chanTheGioi,
            R.id.chanKinhTe, R.id.chanCongNghe, R.id.chanThoiTrang, R.id.chanTheThao, R.id.chanPhapLuat,
            R.id.chanDuLich, R.id.chanGame, R.id.chanSucKhoe, R.id.chanAmThuc, R.id.chanXeCo,
            R.id.chanDoiSong, R.id.chanGiaoDuc, R.id.chanHotGirls, R.id.chanLamDep, R.id.chanTinhYeu
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);

        initViews();
        setupRecyclerView();
        setupNavigation();
        setupTabs();
        setupChannelClicks();
        setupExploreUtilities();
        
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                adapter.notifyDataSetChanged();
                rvNews.scrollToPosition(0);
            }
        });

        viewModel.getCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                initializeDefaultData();
            }
        });
    }

    private void initViews() {
        mainLayout = findViewById(R.id.mainLayout);
        homeContainer = findViewById(R.id.homeContainer);
        profileContainer = findViewById(R.id.profileContainer);
        channelsContainer = findViewById(R.id.channelsContainer);
        exploreContainer = findViewById(R.id.exploreContainer);

        btnNavHome = findViewById(R.id.btnNavHome);
        btnNavProfile = findViewById(R.id.btnNavProfile);
        btnNavExplore = findViewById(R.id.btnNavExplore);
        
        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavProfile = findViewById(R.id.ivNavProfile);
        ivNavExplore = findViewById(R.id.ivNavExplore);
        
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavProfile = findViewById(R.id.tvNavProfile);
        tvNavExplore = findViewById(R.id.tvNavExplore);

        btnOpenChannels = findViewById(R.id.btnOpenChannels);
        btnCloseChannels = findViewById(R.id.btnCloseChannels);

        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabBongDa = findViewById(R.id.tabBongDa);
        tabVideo = findViewById(R.id.tabVideo);
        tabXaHoi = findViewById(R.id.tabXaHoi);
        tabGiaiTri = findViewById(R.id.tabGiaiTri);
        tabTheGioi = findViewById(R.id.tabTheGioi);
        utLunar = findViewById(R.id.utLunar);
    }

    private void setupRecyclerView() {
        rvNews = findViewById(R.id.rvNews);
        rvNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(articleList);
        rvNews.setAdapter(adapter);
    }

    private void setupNavigation() {
        btnNavHome.setOnClickListener(v -> showHomePage());
        btnNavProfile.setOnClickListener(v -> showProfilePage());
        btnNavExplore.setOnClickListener(v -> showExplorePage());

        btnOpenChannels.setOnClickListener(v -> channelsContainer.setVisibility(View.VISIBLE));
        if (btnCloseChannels != null) {
            btnCloseChannels.setOnClickListener(v -> channelsContainer.setVisibility(View.GONE));
        }

        View btnNightMode = findViewById(R.id.btnNightMode);
        if (btnNightMode != null) {
            btnNightMode.setOnClickListener(v -> toggleNightMode());
        }
    }

    private void setupExploreUtilities() {
        if (utLunar != null) {
            String todayLunar = LunarCalendar.getTodayLunar();
            utLunar.setText(todayLunar);
            utLunar.setOnClickListener(v -> showLunarCalendarDialog());
        }
        
        View utWeather = findViewById(R.id.utWeather);
        if (utWeather != null) {
            utWeather.setOnClickListener(v -> Toast.makeText(this, "Tính năng Thời tiết đang cập nhật", Toast.LENGTH_SHORT).show());
        }
    }

    private void showLunarCalendarDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_lunar_calendar, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        TextView tvHeader = dialogView.findViewById(R.id.tvCalendarHeader);
        GridLayout gridLayout = dialogView.findViewById(R.id.calendarGrid);
        ImageButton btnPrev = dialogView.findViewById(R.id.btnPrevMonth);
        ImageButton btnNext = dialogView.findViewById(R.id.btnNextMonth);

        updateCalendarGrid(tvHeader, gridLayout);

        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendarGrid(tvHeader, gridLayout);
        });

        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendarGrid(tvHeader, gridLayout);
        });

        dialog.show();
    }

    private void updateCalendarGrid(TextView tvHeader, GridLayout gridLayout) {
        if (gridLayout == null || tvHeader == null) return;
        gridLayout.removeAllViews();
        
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        int year = currentCalendar.get(Calendar.YEAR);
        tvHeader.setText(getString(R.string.calendar_header_format, month, year));

        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // Sunday = 1, Monday = 2...
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 1. Padding for first week (Spacers)
        for (int i = 1; i < firstDayOfWeek; i++) {
            View spacer = new View(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.width = 0;
            params.height = 1;
            spacer.setLayoutParams(params);
            gridLayout.addView(spacer);
        }

        // 2. Add Day Views
        for (int i = 1; i <= daysInMonth; i++) {
            View dayView = LayoutInflater.from(this).inflate(R.layout.item_calendar_day, gridLayout, false);
            TextView tvSolar = dayView.findViewById(R.id.tvSolarDay);
            TextView tvLunar = dayView.findViewById(R.id.tvLunarDay);

            tvSolar.setText(String.valueOf(i));
            LunarCalendar.LunarDate ld = LunarCalendar.getLunarDate(i, cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
            tvLunar.setText(String.valueOf(ld.day));
            
            // Show Month/Year if it's the 1st of the lunar month
            if (ld.day == 1) {
                tvLunar.setText(getString(R.string.lunar_day_full_format, ld.day, ld.month));
            }

            // Important: Use weight to distribute 7 columns evenly
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.width = 0;
            dayView.setLayoutParams(params);

            gridLayout.addView(dayView);
        }
    }

    private void toggleNightMode() {
        isNightMode = !isNightMode;
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        int secondaryBg = isNightMode ? Color.parseColor("#121212") : Color.parseColor("#EEEEEE");
        int itemBg = isNightMode ? Color.parseColor("#222222") : Color.parseColor("#F5F5F5");

        if (mainLayout != null) mainLayout.setBackgroundColor(secondaryBg);
        homeContainer.setBackgroundColor(bgColor);
        profileContainer.setBackgroundColor(bgColor);
        channelsContainer.setBackgroundColor(bgColor);
        exploreContainer.setBackgroundColor(bgColor);
        
        View profileScrollView = findViewById(R.id.profileScrollView);
        if (profileScrollView != null) profileScrollView.setBackgroundColor(bgColor);
        
        View exploreScrollView = findViewById(R.id.exploreScrollView);
        if (exploreScrollView != null) exploreScrollView.setBackgroundColor(bgColor);

        adapter.setNightMode(isNightMode);

        findViewById(R.id.categoryBar).setBackgroundColor(isNightMode ? Color.parseColor("#1A1A1A") : Color.WHITE);
        findViewById(R.id.bottomNav).setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);

        int[] profileTextIds = {R.id.tvTheoDoi, R.id.tvThongBao, R.id.tvNightMode, 
                                R.id.menuLuu, R.id.menuLichSu, R.id.menuPhanHoi, R.id.menuCaiDat, R.id.tvFooterName, R.id.tvFooterId};
        for (int id : profileTextIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(isNightMode && id == R.id.tvFooterId ? Color.GRAY : textColor);
        }
        
        TextView tvNightModeLabel = findViewById(R.id.tvNightMode);
        if (tvNightModeLabel != null) tvNightModeLabel.setText(isNightMode ? R.string.night_mode_on : R.string.night_mode_off);

        TextView tvChanTitle = findViewById(R.id.tvMyChannelsTitle);
        TextView tvChanDesc = findViewById(R.id.tvMyChannelsDesc);
        TextView tvChanRecTitle = findViewById(R.id.tvRecommendedChannelsTitle);
        TextView tvChanRecDesc = findViewById(R.id.tvRecommendedChannelsDesc);
        ImageView ivClose = findViewById(R.id.btnCloseChannels);
        
        if (tvChanTitle != null) tvChanTitle.setTextColor(textColor);
        if (tvChanDesc != null) tvChanDesc.setTextColor(Color.GRAY);
        if (tvChanRecTitle != null) tvChanRecTitle.setTextColor(textColor);
        if (tvChanRecDesc != null) tvChanRecDesc.setTextColor(Color.GRAY);
        if (ivClose != null) ivClose.setColorFilter(textColor);

        for (int id : allChannelIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setBackgroundColor(itemBg);
                if (tv.getText().toString().equals(getString(R.string.tab_football))) tv.setTextColor(Color.RED);
                else tv.setTextColor(textColor);
            }
        }

        int[] exploreTextIds = {R.id.tvTrendingHeader, R.id.tvUtilitiesHeader, 
                                R.id.tvSuggestionHeader, R.id.tvExploreSuggestionTitle,
                                R.id.utWeather, R.id.utGold, R.id.utLottery, R.id.utLunar};
        for (int id : exploreTextIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                if (id == R.id.utWeather || id == R.id.utGold || id == R.id.utLottery || id == R.id.utLunar) {
                    tv.setTextColor(isNightMode ? Color.LTGRAY : Color.parseColor("#555555"));
                } else {
                    tv.setTextColor(textColor);
                }
            }
        }
        
        View exploreSearch = findViewById(R.id.etExploreSearch);
        if (exploreSearch != null) {
            ((View)exploreSearch.getParent()).setBackgroundColor(itemBg);
            ((TextView)exploreSearch).setTextColor(textColor);
            ((TextView)exploreSearch).setHintTextColor(Color.GRAY);
        }

        updateNavColor(homeContainer.getVisibility() == View.VISIBLE, exploreContainer.getVisibility() == View.VISIBLE, profileContainer.getVisibility() == View.VISIBLE);
        updateTabColors();
    }

    private void updateTabColors() {
        int activeColor = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int inactiveColor = isNightMode ? Color.LTGRAY : Color.parseColor("#555555");
        
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView tab : tabs) {
            if (tab != null) {
                if (tab.getTypeface() != null && tab.getTypeface().isBold()) {
                    tab.setTextColor(activeColor);
                } else {
                    tab.setTextColor(inactiveColor);
                }
            }
        }
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            String categoryName = ((TextView) v).getText().toString();
            switchCategory(v.getId(), categoryName);
        };
        if (tabTrangChu != null) tabTrangChu.setOnClickListener(tabClickListener);
        if (tabBongDa != null) tabBongDa.setOnClickListener(tabClickListener);
        if (tabVideo != null) tabVideo.setOnClickListener(tabClickListener);
        if (tabXaHoi != null) tabXaHoi.setOnClickListener(tabClickListener);
        if (tabGiaiTri != null) tabGiaiTri.setOnClickListener(tabClickListener);
        if (tabTheGioi != null) tabTheGioi.setOnClickListener(tabClickListener);
    }

    private void setupChannelClicks() {
        View.OnClickListener channelListener = v -> {
            channelsContainer.setVisibility(View.GONE);
            showHomePage();
            String categoryName = ((TextView)v).getText().toString();
            
            int tabId = -1;
            if (categoryName.equals(getString(R.string.tab_home))) tabId = R.id.tabTrangChu;
            else if (categoryName.equals(getString(R.string.tab_football))) tabId = R.id.tabBongDa;
            else if (categoryName.equals(getString(R.string.tab_video))) tabId = R.id.tabVideo;
            else if (categoryName.equals(getString(R.string.tab_social))) tabId = R.id.tabXaHoi;
            else if (categoryName.equals(getString(R.string.tab_entertainment))) tabId = R.id.tabGiaiTri;
            else if (categoryName.equals(getString(R.string.tab_world))) tabId = R.id.tabTheGioi;
            
            switchCategory(tabId, categoryName);
        };

        for (int id : allChannelIds) {
            View view = findViewById(id);
            if (view != null) view.setOnClickListener(channelListener);
        }
    }

    private void switchCategory(int tabId, String categoryName) {
        resetTabs();
        if (tabId != -1) {
            TextView selectedTab = findViewById(tabId);
            if (selectedTab != null) {
                selectedTab.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                selectedTab.setTypeface(null, Typeface.BOLD);
            }
        }
        viewModel.setCategory(categoryName);
    }

    private void resetTabs() {
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        int grayColor = isNightMode ? Color.LTGRAY : ContextCompat.getColor(this, android.R.color.darker_gray);
        for (TextView tab : tabs) {
            if (tab != null) {
                tab.setTextColor(grayColor);
                tab.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void initializeDefaultData() {
        viewModel.insertCategories(Arrays.asList(new Category(getString(R.string.tab_home), true), new Category(getString(R.string.tab_football), true)));
        viewModel.insertArticles(Collections.singletonList(new Article("Chào mừng", "Hệ thống", "Bây giờ", null, 1, "Chào mừng bạn đến với ứng dụng tin tức.")));
    }

    private void showHomePage() {
        homeContainer.setVisibility(View.VISIBLE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        channelsContainer.setVisibility(View.GONE);
        updateNavColor(true, false, false);
    }

    private void showExplorePage() {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);
        channelsContainer.setVisibility(View.GONE);
        updateNavColor(false, true, false);
    }

    private void showProfilePage() {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.VISIBLE);
        channelsContainer.setVisibility(View.GONE);
        updateNavColor(false, false, true);
    }

    private void updateNavColor(boolean isHome, boolean isExplore, boolean isProfile) {
        int red = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int inactiveColor = isNightMode ? Color.LTGRAY : ContextCompat.getColor(this, android.R.color.darker_gray);
        
        ivNavHome.setColorFilter(isHome ? red : inactiveColor);
        tvNavHome.setTextColor(isHome ? red : inactiveColor);
        
        ivNavExplore.setColorFilter(isExplore ? red : inactiveColor);
        tvNavExplore.setTextColor(isExplore ? red : inactiveColor);
        
        ivNavProfile.setColorFilter(isProfile ? red : inactiveColor);
        tvNavProfile.setTextColor(isProfile ? red : inactiveColor);
    }
}
