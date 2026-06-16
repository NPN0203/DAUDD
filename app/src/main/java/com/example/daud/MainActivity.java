package com.example.daud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import com.example.daud.viewmodel.NewsViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private final List<Article> articleList = new ArrayList<>();

    private View homeContainer, profileContainer, channelsContainer, exploreContainer;
    private ImageView ivNavHome, ivNavExplore, ivNavProfile;
    private TextView tvNavHome, tvNavExplore, tvNavProfile;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;

    private NewsViewModel viewModel;
    private boolean isNightMode = false;

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
        observeData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileUI();
    }

    private void initViews() {
        homeContainer = findViewById(R.id.homeContainer);
        profileContainer = findViewById(R.id.profileContainer);
        channelsContainer = findViewById(R.id.channelsContainer);
        exploreContainer = findViewById(R.id.exploreContainer);
        ivNavHome = findViewById(R.id.ivNavHome);
        ivNavExplore = findViewById(R.id.ivNavExplore);
        ivNavProfile = findViewById(R.id.ivNavProfile);
        tvNavHome = findViewById(R.id.tvNavHome);
        tvNavExplore = findViewById(R.id.tvNavExplore);
        tvNavProfile = findViewById(R.id.tvNavProfile);
        tabTrangChu = findViewById(R.id.tabTrangChu);
        tabBongDa = findViewById(R.id.tabBongDa);
        tabVideo = findViewById(R.id.tabVideo);
        tabXaHoi = findViewById(R.id.tabXaHoi);
        tabGiaiTri = findViewById(R.id.tabGiaiTri);
        tabTheGioi = findViewById(R.id.tabTheGioi);
    }

    private void setupRecyclerView() {
        rvNews = findViewById(R.id.rvNews);
        if (rvNews != null) {
            rvNews.setLayoutManager(new LinearLayoutManager(this));
            adapter = new NewsAdapter(articleList);
            rvNews.setAdapter(adapter);
        }
    }

    private void observeData() {
        viewModel.getArticles().observe(this, articles -> {
            if (articles != null) {
                articleList.clear();
                articleList.addAll(articles);
                if (adapter != null) adapter.notifyDataSetChanged();
                if (rvNews != null) rvNews.scrollToPosition(0);
            }
        });
        viewModel.getCategories().observe(this, categories -> {
            if (categories == null || categories.isEmpty()) {
                viewModel.insertCategories(Arrays.asList(new Category("Trang chủ", true), new Category("Bóng đá", true)));
            }
        });
    }

    private void setupNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> showPage(homeContainer));
        findViewById(R.id.btnNavExplore).setOnClickListener(v -> showPage(exploreContainer));
        findViewById(R.id.btnNavProfile).setOnClickListener(v -> showPage(profileContainer));
        View btnOpen = findViewById(R.id.btnOpenChannels);
        if (btnOpen != null) btnOpen.setOnClickListener(v -> channelsContainer.setVisibility(View.VISIBLE));
        View btnCloseChan = findViewById(R.id.btnCloseChannels);
        if (btnCloseChan != null) btnCloseChan.setOnClickListener(v -> channelsContainer.setVisibility(View.GONE));
        
        View rootProfile = findViewById(R.id.profileRootLayout);
        if (rootProfile != null) {
            rootProfile.findViewById(R.id.btnNightMode).setOnClickListener(v -> toggleNightMode());
            rootProfile.findViewById(R.id.btnMenuLuu).setOnClickListener(v -> startActivity(new Intent(this, SavedArticlesActivity.class).putExtra("nightMode", isNightMode)));
            rootProfile.findViewById(R.id.btnMenuLichSu).setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class).putExtra("nightMode", isNightMode)));

            rootProfile.findViewById(R.id.loginHeader).setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                if (!pref.getBoolean("isLoggedIn", false)) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            });

            rootProfile.findViewById(R.id.btnLogout).setOnClickListener(v -> {
                SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                pref.edit().clear().apply();
                updateProfileUI();
                Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            });

            rootProfile.findViewById(R.id.btnAdminPanel).setOnClickListener(v -> {
                startActivity(new Intent(this, AdminActivity.class));
            });
        }
    }

    private void updateProfileUI() {
        View rootProfile = findViewById(R.id.profileRootLayout);
        if (rootProfile == null) return;

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = pref.getBoolean("isLoggedIn", false);
        boolean isAdmin = pref.getBoolean("isAdmin", false);
        String fullName = pref.getString("fullName", "Đăng nhập / Đăng ký");
        String username = pref.getString("username", "");

        TextView tvUserStatus = rootProfile.findViewById(R.id.tvUserStatus);
        TextView tvUserSubStatus = rootProfile.findViewById(R.id.tvUserSubStatus);
        ImageView btnLogout = rootProfile.findViewById(R.id.btnLogout);
        ImageView ivUserAvatar = rootProfile.findViewById(R.id.ivUserAvatar);
        View btnAdminPanel = rootProfile.findViewById(R.id.btnAdminPanel);
        View dividerAdmin = rootProfile.findViewById(R.id.dividerAdmin);

        if (isLoggedIn) {
            tvUserStatus.setText(fullName);
            tvUserSubStatus.setText("@" + username);
            btnLogout.setVisibility(View.VISIBLE);
            ivUserAvatar.setColorFilter(Color.RED);

            if (isAdmin) {
                btnAdminPanel.setVisibility(View.VISIBLE);
                dividerAdmin.setVisibility(View.VISIBLE);
            } else {
                btnAdminPanel.setVisibility(View.GONE);
                dividerAdmin.setVisibility(View.GONE);
            }
        } else {
            tvUserStatus.setText("Đăng nhập / Đăng ký");
            tvUserSubStatus.setText("Bấm để đăng nhập và đồng bộ");
            btnLogout.setVisibility(View.GONE);
            ivUserAvatar.setColorFilter(Color.LTGRAY);
            btnAdminPanel.setVisibility(View.GONE);
            dividerAdmin.setVisibility(View.GONE);
        }
    }

    private void showPage(View page) {
        homeContainer.setVisibility(View.GONE);
        exploreContainer.setVisibility(View.GONE);
        profileContainer.setVisibility(View.GONE);
        channelsContainer.setVisibility(View.GONE);
        page.setVisibility(View.VISIBLE);
        updateNavUI(page.getId());
    }

    private void updateNavUI(int id) {
        int active = Color.RED;
        int inactive = isNightMode ? Color.LTGRAY : Color.GRAY;
        ivNavHome.setColorFilter(id == R.id.homeContainer ? active : inactive);
        tvNavHome.setTextColor(id == R.id.homeContainer ? active : inactive);
        ivNavExplore.setColorFilter(id == R.id.exploreContainer ? active : inactive);
        tvNavExplore.setTextColor(id == R.id.exploreContainer ? active : inactive);
        ivNavProfile.setColorFilter(id == R.id.profileContainer ? active : inactive);
        tvNavProfile.setTextColor(id == R.id.profileContainer ? active : inactive);
    }

    private void setupTabs() {
        View.OnClickListener listener = v -> switchCategory(v.getId(), ((TextView) v).getText().toString());
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView t : tabs) if (t != null) t.setOnClickListener(listener);
    }

    private void setupChannelClicks() {
        View.OnClickListener listener = v -> {
            String name = ((TextView) v).getText().toString();
            showPage(homeContainer);
            int tabId = -1;
            if (name.equals("Trang chủ")) tabId = R.id.tabTrangChu;
            else if (name.equals("Bóng đá")) tabId = R.id.tabBongDa;
            else if (name.equals("Video")) tabId = R.id.tabVideo;
            else if (name.equals("Xã hội")) tabId = R.id.tabXaHoi;
            else if (name.equals("Giải trí")) tabId = R.id.tabGiaiTri;
            else if (name.equals("Thế giới")) tabId = R.id.tabTheGioi;
            switchCategory(tabId, name);
        };
        for (int id : allChannelIds) {
            View v = findViewById(id);
            if (v != null) v.setOnClickListener(listener);
        }
    }

    private void switchCategory(int id, String name) {
        int active = Color.RED;
        int inactive = isNightMode ? Color.LTGRAY : Color.DKGRAY;
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView t : tabs) {
            if (t != null) {
                t.setTextColor(t.getId() == id ? active : inactive);
                t.setTypeface(null, t.getId() == id ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
        viewModel.setCategory(name);
    }

    private void toggleNightMode() {
        isNightMode = !isNightMode;
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.parseColor("#333333");
        int secondaryTextColor = isNightMode ? Color.LTGRAY : Color.GRAY;

        homeContainer.setBackgroundColor(bgColor);
        exploreContainer.setBackgroundColor(bgColor);
        profileContainer.setBackgroundColor(bgColor);
        channelsContainer.setBackgroundColor(bgColor);

        // Update background of components inside layout_profile.xml
        View profileScroll = findViewById(R.id.profileScrollView);
        if (profileScroll != null) profileScroll.setBackgroundColor(bgColor);

        View categoryBar = findViewById(R.id.categoryBar);
        if (categoryBar != null) categoryBar.setBackgroundColor(bgColor);

        View bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) bottomNav.setBackgroundColor(bgColor);

        // Update Button text and color
        TextView tvNightMode = findViewById(R.id.tvNightMode);
        if (tvNightMode != null) {
            tvNightMode.setText(isNightMode ? "Ban ngày" : "Ban đêm");
            tvNightMode.setTextColor(textColor);
        }

        // Update other text colors in profile
        int[] textIds = {
                R.id.tvTheoDoi, R.id.tvThongBao, R.id.menuLuu, R.id.menuLichSu,
                R.id.menuPhanHoi, R.id.menuCaiDat, R.id.tvFooterName, R.id.tvUserStatus, R.id.menuAdmin
        };
        for (int id : textIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(textColor);
        }

        TextView tvFooterId = findViewById(R.id.tvFooterId);
        if (tvFooterId != null) tvFooterId.setTextColor(secondaryTextColor);

        TextView tvUserSubStatus = findViewById(R.id.tvUserSubStatus);
        if (tvUserSubStatus != null) tvUserSubStatus.setTextColor(secondaryTextColor);

        if (adapter != null) {
            adapter.setNightMode(isNightMode);
            adapter.notifyDataSetChanged();
        }

        showPage(profileContainer);
    }
}
