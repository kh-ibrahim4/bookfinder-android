package com.example.finalproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BooksListFragment.OnBookSelectedListener {

    private static final String CHANNEL_ID = "bookfinder_channel";
    private static final int NOTIFICATION_SUCCESS_ID = 1;
    private static final int NOTIFICATION_ERROR_ID = 2;
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LinearLayout searchLayout;
    private EditText editTextSearch;
    private ImageButton btnSearch;

    private ViewPagerAdapter viewPagerAdapter;

    private boolean isSearchVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        searchLayout = findViewById(R.id.searchLayout);
        editTextSearch = findViewById(R.id.editTextSearch);
        btnSearch = findViewById(R.id.btnSearch);

        setSupportActionBar(toolbar);

        createNotificationChannel();

        requestNotificationPermission();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        setupSearch();
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            BooksListFragment fragment = viewPagerAdapter.getBooksListFragment();
            if (fragment != null) {
                fragment.fetchBooks(query);
            }

            viewPager.setCurrentItem(0);

            hideKeyboard();
        } else {
            Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            toggleSearchBar();
            return true;
        } else if (id == R.id.action_refresh) {
            BooksListFragment fragment = viewPagerAdapter.getBooksListFragment();
            if (fragment != null) {
                fragment.fetchBooks("android programming");
            }
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleSearchBar() {
        if (isSearchVisible) {
            searchLayout.setVisibility(View.GONE);
            isSearchVisible = false;
            hideKeyboard();
        } else {
            searchLayout.setVisibility(View.VISIBLE);
            editTextSearch.requestFocus();
            isSearchVisible = true;
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.about_title)
                .setMessage(R.string.about_message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    @Override
    public void onBookSelected(Book book) {
        BookDetailFragment detailFragment = viewPagerAdapter.getBookDetailFragment();
        if (detailFragment != null) {
            detailFragment.updateBookDetails(book);
        }

        viewPager.setCurrentItem(1);
    }

    @Override
    public void onBooksLoaded(List<Book> books) {
        if (books != null && !books.isEmpty()) {
            BookDetailFragment detailFragment = viewPagerAdapter.getBookDetailFragment();
            if (detailFragment != null) {
                detailFragment.updateBookDetails(books.get(0));
            }
        }

        showNotification("Books Loaded \u2713",
                books.size() + " books loaded successfully!",
                NOTIFICATION_SUCCESS_ID, true);
    }

    @Override
    public void onBooksError(String error) {
        showNotification("Loading Error \u2717",
                "Failed to load books: " + error,
                NOTIFICATION_ERROR_ID, false);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void showNotification(String title, String message, int id, boolean isSuccess) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(isSuccess ?
                        android.R.drawable.ic_dialog_info :
                        android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            notificationManager.notify(id, builder.build());
        } catch (SecurityException e) {
            Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
