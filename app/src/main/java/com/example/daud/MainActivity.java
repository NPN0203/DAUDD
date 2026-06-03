package com.example.daud;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private ConstraintLayout mainLayout;
    private ConstraintLayout homeContainer;
    private View profileContainer;
    private View channelsContainer;

    private LinearLayout btnNavHome, btnNavProfile;
    private ImageView ivNavHome, ivNavProfile;
    private TextView tvNavHome, tvNavProfile;

    private ImageView btnOpenChannels, btnCloseChannels;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;

    private NewsViewModel viewModel;
    private boolean isNightMode = false;

    // Danh sách ID đầy đủ của 20 kênh trong trang "Kênh của tôi"
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
        
        // Lắng nghe dữ liệu bài báo từ Database
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                adapter.notifyDataSetChanged();
                rvNews.scrollToPosition(0);
            }
        });

        // Nạp dữ liệu mẫu nếu Database trống
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

        btnNavHome = findViewById(R.id.btnNavHome);
        btnNavProfile = findViewById(R.id.btnNavProfile);
        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavProfile = findViewById(R.id.ivNavProfile);
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavProfile = findViewById(R.id.tvNavProfile);

        btnOpenChannels = findViewById(R.id.btnOpenChannels);
        btnCloseChannels = findViewById(R.id.btnCloseChannels);

        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabBongDa = findViewById(R.id.tabBongDa);
        tabVideo = findViewById(R.id.tabVideo);
        tabXaHoi = findViewById(R.id.tabXaHoi);
        tabGiaiTri = findViewById(R.id.tabGiaiTri);
        tabTheGioi = findViewById(R.id.tabTheGioi);
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

        btnOpenChannels.setOnClickListener(v -> channelsContainer.setVisibility(View.VISIBLE));
        if (btnCloseChannels != null) {
            btnCloseChannels.setOnClickListener(v -> channelsContainer.setVisibility(View.GONE));
        }

        View btnNightMode = findViewById(R.id.btnNightMode);
        if (btnNightMode != null) {
            btnNightMode.setOnClickListener(v -> toggleNightMode());
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
        
        View profileScrollView = findViewById(R.id.profileScrollView);
        if (profileScrollView != null) profileScrollView.setBackgroundColor(bgColor);

        adapter.setNightMode(isNightMode);

        findViewById(R.id.categoryBar).setBackgroundColor(isNightMode ? Color.parseColor("#1A1A1A") : Color.WHITE);
        findViewById(R.id.bottomNav).setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);

        // Cập nhật trang Profile
        int[] profileTextIds = {R.id.tvTheoDoi, R.id.tvThongBao, R.id.tvNightMode, 
                                R.id.menuLuu, R.id.menuLichSu, R.id.menuPhanHoi, R.id.menuCaiDat, R.id.tvFooterName, R.id.tvFooterId};
        for (int id : profileTextIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(isNightMode && id == R.id.tvFooterId ? Color.GRAY : textColor);
        }
        
        TextView tvNightModeLabel = findViewById(R.id.tvNightMode);
        if (tvNightModeLabel != null) tvNightModeLabel.setText(isNightMode ? "Chế độ ngày" : "Ban đêm");

        // Cập nhật trang Kênh
        TextView tvChanTitle = findViewById(R.id.tvMyChannelsTitle);
        TextView tvChanDesc = findViewById(R.id.tvMyChannelsDesc);
        TextView tvChanRecTitle = findViewById(R.id.tvRecommendedChannelsTitle);
        ImageView ivClose = findViewById(R.id.btnCloseChannels);
        
        if (tvChanTitle != null) tvChanTitle.setTextColor(textColor);
        if (tvChanDesc != null) tvChanDesc.setTextColor(Color.GRAY);
        if (tvChanRecTitle != null) tvChanRecTitle.setTextColor(textColor);
        if (ivClose != null) ivClose.setColorFilter(textColor);

        for (int id : allChannelIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setBackgroundColor(itemBg);
                if (tv.getText().toString().equals("Bóng đá")) tv.setTextColor(Color.RED);
                else tv.setTextColor(textColor);
            }
        }

        updateNavColor(homeContainer.getVisibility() == View.VISIBLE);
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
            
            // Tìm Tab ID tương ứng để cập nhật UI
            int tabId = -1;
            if (categoryName.equals("Trang chủ")) tabId = R.id.tabTrangChu;
            else if (categoryName.equals("Bóng đá")) tabId = R.id.tabBongDa;
            else if (categoryName.equals("Video")) tabId = R.id.tabVideo;
            else if (categoryName.equals("Xã hội")) tabId = R.id.tabXaHoi;
            else if (categoryName.equals("Giải trí")) tabId = R.id.tabGiaiTri;
            else if (categoryName.equals("Thế giới")) tabId = R.id.tabTheGioi;
            
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
        viewModel.insertCategories(Arrays.asList(new Category("Trang chủ", true), new Category("Bóng đá", true)));
        viewModel.insertArticles(Collections.singletonList(new Article("Chào mừng", "Hệ thống", "Bây giờ", null, 1, "Chào mừng bạn đến với ứng dụng tin tức.")));
    }

    private void showHomePage() {
        homeContainer.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);
        channelsContainer.setVisibility(View.GONE);
        updateNavColor(true);
    }

    private void showProfilePage() {
        homeContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.VISIBLE);
        channelsContainer.setVisibility(View.GONE);
        updateNavColor(false);
    }

    private void updateNavColor(boolean isHome) {
        int red = ContextCompat.getColor(this, android.R.color.holo_red_dark);
        int inactiveColor = isNightMode ? Color.LTGRAY : ContextCompat.getColor(this, android.R.color.darker_gray);
        ivNavHome.setColorFilter(isHome ? red : inactiveColor);
        tvNavHome.setTextColor(isHome ? red : inactiveColor);
        ivNavProfile.setColorFilter(isHome ? inactiveColor : red);
        tvNavProfile.setTextColor(isHome ? inactiveColor : red);
    }
}
