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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.daud.adapter.NewsAdapter;
import com.example.daud.database.AppDatabase;
import com.example.daud.model.Article;
import com.example.daud.model.Category;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private List<Article> articleList = new ArrayList<>();

    private ConstraintLayout mainLayout;
    private ConstraintLayout homeContainer;
    private View profileContainer;
    private View channelsContainer;

    private LinearLayout btnNavHome, btnNavProfile;
    private ImageView ivNavHome, ivNavProfile;
    private TextView tvNavHome, tvNavProfile;

    private ImageView btnOpenChannels, btnCloseChannels;
    private TextView tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi;

    private AppDatabase db;
    private boolean isNightMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        initViews();
        setupRecyclerView();
        setupNavigation();
        setupTabs();
        setupChannelClicks();
        initializeData();
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

        // Định nghĩa bảng màu
        int bgColor = isNightMode ? Color.BLACK : Color.WHITE;
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;
        int secondaryBg = isNightMode ? Color.parseColor("#121212") : Color.parseColor("#EEEEEE");
        int itemBg = isNightMode ? Color.parseColor("#333333") : Color.parseColor("#F5F5F5");

        // 1. Cập nhật nền ứng dụng và các vùng chứa chính
        if (mainLayout != null) mainLayout.setBackgroundColor(secondaryBg);
        homeContainer.setBackgroundColor(bgColor);
        profileContainer.setBackgroundColor(bgColor);
        channelsContainer.setBackgroundColor(bgColor);
        
        View profileScrollView = findViewById(R.id.profileScrollView);
        if (profileScrollView != null) profileScrollView.setBackgroundColor(bgColor);

        // 2. Cập nhật NewsAdapter (Danh sách tin tức)
        adapter.setNightMode(isNightMode);

        // 3. Cập nhật thanh Tab và thanh điều hướng dưới
        findViewById(R.id.categoryBar).setBackgroundColor(isNightMode ? Color.parseColor("#1A1A1A") : Color.WHITE);
        findViewById(R.id.bottomNav).setBackgroundColor(isNightMode ? Color.BLACK : Color.WHITE);

        // 4. Cập nhật giao diện trang Cá nhân (Tôi)
        int[] profileTextIds = {R.id.tvTheoDoi, R.id.tvThongBao, R.id.tvNightMode, 
                                R.id.menuLuu, R.id.menuLichSu, R.id.menuPhanHoi, R.id.menuCaiDat, R.id.tvFooterName};
        for (int id : profileTextIds) {
            TextView tv = findViewById(id);
            if (tv != null) tv.setTextColor(textColor);
        }
        
        TextView tvNightMode = findViewById(R.id.tvNightMode);
        if (tvNightMode != null) tvNightMode.setText(isNightMode ? "Chế độ ngày" : "Ban đêm");

        // 5. Cập nhật giao diện trang "Kênh của tôi" (Như trong ảnh bạn gửi)
        TextView tvChanTitle = findViewById(R.id.tvMyChannelsTitle);
        TextView tvChanDesc = findViewById(R.id.tvMyChannelsDesc);
        TextView tvChanRec = findViewById(R.id.tvRecommendedChannelsTitle);
        ImageView ivClose = findViewById(R.id.btnCloseChannels);
        
        if (tvChanTitle != null) tvChanTitle.setTextColor(textColor);
        if (tvChanDesc != null) tvChanDesc.setTextColor(Color.GRAY);
        if (tvChanRec != null) tvChanRec.setTextColor(textColor);
        if (ivClose != null) ivClose.setColorFilter(textColor);

        // Cập nhật màu các ô danh mục trong Grid
        int[] channelIds = {
            R.id.chanTrangChu, R.id.chanBongDa, R.id.chanXaHoi, R.id.chanGiaiTri, R.id.chanTheGioi,
            R.id.chanKinhTe, R.id.chanCongNghe, R.id.chanThoiTrang, R.id.chanTheThao, R.id.chanPhapLuat,
            R.id.chanDuLich, R.id.chanGame, R.id.chanSucKhoe, R.id.chanAmThuc, R.id.chanXeCo,
            R.id.chanDoiSong, R.id.chanGiaoDuc, R.id.chanHotGirls, R.id.chanLamDep, R.id.chanTinhYeu
        };
        for (int id : channelIds) {
            TextView tv = findViewById(id);
            if (tv != null) {
                tv.setBackgroundColor(itemBg);
                if (tv.getText().toString().equals("Bóng đá")) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(textColor);
                }
            }
        }

        updateNavColor(homeContainer.getVisibility() == View.VISIBLE);
        updateTabColors();
    }

    private void updateTabColors() {
        int textColor = isNightMode ? Color.LTGRAY : Color.parseColor("#555555");
        TextView[] tabs = {tabTrangChu, tabBongDa, tabVideo, tabXaHoi, tabGiaiTri, tabTheGioi};
        for (TextView tab : tabs) {
            if (tab != null) {
                if (tab.getText().toString().equals("Trang chủ") && !isNightMode) {
                    tab.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                } else {
                    tab.setTextColor(textColor);
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
            int id = v.getId();
            if (id == R.id.chanTrangChu) tabId = R.id.tabTrangChu;
            else if (id == R.id.chanBongDa) tabId = R.id.tabBongDa;
            else if (id == R.id.chanXaHoi) tabId = R.id.tabXaHoi;
            else if (id == R.id.chanGiaiTri) tabId = R.id.tabGiaiTri;
            else if (id == R.id.chanTheGioi) tabId = R.id.tabTheGioi;
            switchCategory(tabId, categoryName);
        };

        int[] channelIds = {
            R.id.chanTrangChu, R.id.chanBongDa, R.id.chanXaHoi, R.id.chanGiaiTri, R.id.chanTheGioi,
            R.id.chanKinhTe, R.id.chanCongNghe, R.id.chanThoiTrang, R.id.chanTheThao, R.id.chanPhapLuat,
            R.id.chanDuLich, R.id.chanGame, R.id.chanSucKhoe, R.id.chanAmThuc, R.id.chanXeCo,
            R.id.chanDoiSong, R.id.chanGiaoDuc, R.id.chanHotGirls, R.id.chanLamDep, R.id.chanTinhYeu
        };
        for (int id : channelIds) {
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
        loadDataByCategory(categoryName);
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

    private void initializeData() {
        new Thread(() -> {
            List<Category> categories = db.categoryDao().getAllCategories();
            if (categories.isEmpty()) {
                List<Category> defaultCategories = Arrays.asList(
                    new Category("Trang chủ", true), new Category("Bóng đá", true),
                    new Category("Xã hội", true), new Category("Giải trí", true),
                    new Category("Thế giới", true), new Category("Kinh tế", false),
                    new Category("Công nghệ", false), new Category("Thời trang", false),
                    new Category("Thể thao", false), new Category("Pháp luật", false),
                    new Category("Du lịch", false), new Category("Game", false),
                    new Category("Sức khỏe", false), new Category("Ẩm thực", false),
                    new Category("Xe cộ", false), new Category("Đời sống", false),
                    new Category("Giáo dục", false), new Category("Hot Girls", false),
                    new Category("Làm đẹp", false), new Category("Tình yêu", false)
                );
                db.categoryDao().insertCategories(defaultCategories);
                db.articleDao().insertArticles(getSampleArticles());
            }
            runOnUiThread(() -> loadDataByCategory("Trang chủ"));
        }).start();
    }

    private List<Article> getSampleArticles() {
        List<Article> articles = new ArrayList<>();
        articles.add(new Article("Nga bác đề xuất của Trump", "Thế giới", "Vừa xong", 
            Arrays.asList("https://picsum.photos/400/300?random=1"), Article.TYPE_THREE_IMAGES, "Nội dung bài báo..."));
        articles.add(new Article("Ưu đãi Techcombank", "Techcombank", "Tài trợ", 
            Collections.singletonList("https://picsum.photos/800/400?random=2"), Article.TYPE_BIG_IMAGE, "Ưu đãi cực lớn..."));
        return articles;
    }

    private void loadDataByCategory(String categoryName) {
        new Thread(() -> {
            List<Article> articles = db.articleDao().getAllArticles();
            if (!categoryName.equals("Trang chủ")) {
                articles.removeIf(a -> !a.getSource().equals(categoryName));
            }
            runOnUiThread(() -> {
                articleList.clear();
                articleList.addAll(articles);
                adapter.notifyDataSetChanged();
                rvNews.scrollToPosition(0);
            });
        }).start();
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
