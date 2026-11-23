package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookDetailFragment extends Fragment {

    private ImageView imageViewCover;
    private TextView textViewTitle;
    private TextView textViewAuthor;
    private TextView textViewPages;
    private TextView textViewPublished;
    private TextView textViewRating;
    private TextView textViewPublisher;
    private TextView textViewDescription;
    private TextView textViewSelectPrompt;

    private Book currentBook;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        imageViewCover = view.findViewById(R.id.imageViewDetailCover);
        textViewTitle = view.findViewById(R.id.textViewDetailTitle);
        textViewAuthor = view.findViewById(R.id.textViewDetailAuthor);
        textViewPages = view.findViewById(R.id.textViewDetailPages);
        textViewPublished = view.findViewById(R.id.textViewDetailPublished);
        textViewRating = view.findViewById(R.id.textViewDetailRating);
        textViewPublisher = view.findViewById(R.id.textViewDetailPublisher);
        textViewDescription = view.findViewById(R.id.textViewDetailDescription);
        textViewSelectPrompt = view.findViewById(R.id.textViewSelectPrompt);

        if (currentBook != null) {
            displayBookDetails(currentBook);
        }

        return view;
    }

    public void updateBookDetails(Book book) {
        this.currentBook = book;

        if (getView() != null) {
            displayBookDetails(book);
        }
    }

    private void displayBookDetails(Book book) {
        if (book == null) {
            textViewSelectPrompt.setVisibility(View.VISIBLE);
            return;
        }

        textViewSelectPrompt.setVisibility(View.GONE);

        textViewTitle.setText(book.getTitle());
        textViewAuthor.setText(book.getAuthors());

        if (book.getPageCount() > 0) {
            textViewPages.setText(String.valueOf(book.getPageCount()));
        } else {
            textViewPages.setText("--");
        }

        String publishedDate = book.getPublishedDate();
        if (publishedDate != null && publishedDate.length() >= 4) {
            textViewPublished.setText(publishedDate.substring(0, 4));
        } else {
            textViewPublished.setText("--");
        }

        if (book.getRating() > 0) {
            textViewRating.setText(String.format("%.1f ★", book.getRating()));
        } else {
            textViewRating.setText("--");
        }

        textViewPublisher.setText(book.getPublisher());

        String description = book.getDescription();
        if (description != null && !description.isEmpty()) {
            description = description.replaceAll("<[^>]*>", "");
            textViewDescription.setText(description);
        } else {
            textViewDescription.setText("No description available for this book.");
        }

        if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
            new ImageLoadTask(imageViewCover).execute(book.getThumbnailUrl());
        } else {
            imageViewCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }
}
