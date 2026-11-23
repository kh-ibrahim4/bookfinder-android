package com.example.finalproject;

public class Book {

    private String title;
    private String authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private String thumbnailUrl;
    private int pageCount;
    private double rating;

    public Book(String title, String authors, String publisher, String publishedDate,
                String description, String thumbnailUrl, int pageCount, double rating) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.pageCount = pageCount;
        this.rating = rating;
    }

    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getPublisher() { return publisher; }
    public String getPublishedDate() { return publishedDate; }
    public String getDescription() { return description; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public int getPageCount() { return pageCount; }
    public double getRating() { return rating; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthors(String authors) { this.authors = authors; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }
    public void setDescription(String description) { this.description = description; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    public void setRating(double rating) { this.rating = rating; }
}
