package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private BooksListFragment booksListFragment;
    private BookDetailFragment bookDetailFragment;

    private String[] tabTitles = {"📚 Books", "📖 Details"};

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        booksListFragment = new BooksListFragment();
        bookDetailFragment = new BookDetailFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return booksListFragment;
            case 1:
                return bookDetailFragment;
            default:
                return booksListFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public BooksListFragment getBooksListFragment() {
        return booksListFragment;
    }

    public BookDetailFragment getBookDetailFragment() {
        return bookDetailFragment;
    }
}
