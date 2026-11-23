package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book, int position);
    }

    public BookAdapter(OnBookClickListener listener) {
        this.bookList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthors());
        holder.textViewPublisher.setText(book.getPublisher());

        if (book.getRating() > 0) {
            holder.textViewRating.setText(String.valueOf(book.getRating()));
        } else {
            holder.textViewRating.setText("N/A");
        }

        if (book.getPageCount() > 0) {
            holder.textViewPages.setText(book.getPageCount() + " pages");
        } else {
            holder.textViewPages.setText("-- pages");
        }

        if (book.getThumbnailUrl() != null && !book.getThumbnailUrl().isEmpty()) {
            holder.imageViewCover.setImageBitmap(null);
            new ImageLoadTask(holder.imageViewCover).execute(book.getThumbnailUrl());
        } else {
            holder.imageViewCover.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBookClick(book, holder.getBindingAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateBooks(List<Book> newBooks) {
        this.bookList.clear();
        this.bookList.addAll(newBooks);
        notifyDataSetChanged();
    }

    public List<Book> getBookList() {
        return bookList;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewPublisher;
        TextView textViewRating;
        TextView textViewPages;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewBookCover);
            textViewTitle = itemView.findViewById(R.id.textViewBookTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewBookAuthor);
            textViewPublisher = itemView.findViewById(R.id.textViewBookPublisher);
            textViewRating = itemView.findViewById(R.id.textViewBookRating);
            textViewPages = itemView.findViewById(R.id.textViewBookPages);
        }
    }
}
