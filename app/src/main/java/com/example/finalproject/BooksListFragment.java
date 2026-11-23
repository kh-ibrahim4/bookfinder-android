package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.List;

public class BooksListFragment extends Fragment implements
        BookAdapter.OnBookClickListener, FetchBooksTask.OnBooksLoadedListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private TextView textViewEmpty;
    private TextView textViewEmptyHint;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookAdapter adapter;
    private OnBookSelectedListener bookSelectedListener;
    private String currentQuery = "android programming";

    public interface OnBookSelectedListener {
        void onBookSelected(Book book);
        void onBooksLoaded(List<Book> books);
        void onBooksError(String error);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBookSelectedListener) {
            bookSelectedListener = (OnBookSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBookSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        textViewEmptyHint = view.findViewById(R.id.textViewEmptyHint);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookAdapter(this);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeColors(
                android.graphics.Color.parseColor("#FF6200EE"),
                android.graphics.Color.parseColor("#FF03DAC5")
        );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchBooks(currentQuery);
            }
        });

        fetchBooks(currentQuery);

        return view;
    }

    public void fetchBooks(String query) {
        this.currentQuery = query;

        progressBar.setVisibility(View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        new FetchBooksTask(this).execute(query);
    }

    @Override
    public void onBooksLoaded(List<Book> books) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (books != null && !books.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            adapter.updateBooks(books);

            if (bookSelectedListener != null) {
                bookSelectedListener.onBooksLoaded(books);
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setText("لا توجد كتب متاحة");
            textViewEmptyHint.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        recyclerView.setVisibility(View.GONE);

        if ("NOT_FOUND".equals(errorMessage)) {
            textViewEmpty.setText("لم يتم العثور على كتب 📚");
            textViewEmptyHint.setText("جرب كلمة بحث أخرى");
            textViewEmptyHint.setVisibility(View.VISIBLE);
        } else if ("No search query provided".equals(errorMessage)) {
            textViewEmpty.setText("الرجاء إدخال كلمة للبحث");
            textViewEmptyHint.setVisibility(View.GONE);
        } else {
            textViewEmpty.setText("خطأ في الاتصال بالإنترنت");
            textViewEmptyHint.setText("تحقق من الاتصال وحاول مجدداً");
            textViewEmptyHint.setVisibility(View.VISIBLE);
        }
        emptyStateLayout.setVisibility(View.VISIBLE);

        if (bookSelectedListener != null) {
            bookSelectedListener.onBooksError(errorMessage);
        }
    }

    @Override
    public void onBookClick(Book book, int position) {
        if (bookSelectedListener != null) {
            bookSelectedListener.onBookSelected(book);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bookSelectedListener = null;
    }
}
