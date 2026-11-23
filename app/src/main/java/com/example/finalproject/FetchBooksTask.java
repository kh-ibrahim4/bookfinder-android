package com.example.finalproject;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FetchBooksTask {

    private static final String TAG = "FetchBooksTask";

    private static final String GOOGLE_BOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    private static final String ITUNES_URL =
            "https://itunes.apple.com/search?entity=ebook&limit=20&term=";

    public interface OnBooksLoadedListener {
        void onBooksLoaded(List<Book> books);
        void onError(String errorMessage);
    }

    private final OnBooksLoadedListener listener;

    public FetchBooksTask(OnBooksLoadedListener listener) {
        this.listener = listener;
    }

    public void execute(String query) {
        if (query == null || query.trim().isEmpty()) {
            if (listener != null) listener.onError("No search query provided");
            return;
        }

        final String finalQuery = query.trim();
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            List<Book> result = fetchFromGoogleBooks(finalQuery);

            if (result.isEmpty()) {
                result = fetchFromITunes(finalQuery);
            }

            final List<Book> finalResult = result;

            handler.post(() -> {
                if (listener == null) return;
                if (!finalResult.isEmpty()) {
                    listener.onBooksLoaded(finalResult);
                } else {
                    listener.onError("NOT_FOUND");
                }
            });
        }).start();
    }

    private List<Book> fetchFromGoogleBooks(String query) {
        List<Book> books = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String urlStr = GOOGLE_BOOKS_URL + encoded + "&maxResults=20&orderBy=relevance";
            Log.d(TAG, "Google Books URL: " + urlStr);

            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setInstanceFollowRedirects(true);
            connection.connect();

            int code = connection.getResponseCode();
            Log.d(TAG, "Google Books response code: " + code);

            if (code == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                books = parseGoogleBooks(sb.toString());
                Log.d(TAG, "Google Books parsed: " + books.size() + " books");
            } else {
                Log.w(TAG, "Google Books error: HTTP " + code);
            }

        } catch (Exception e) {
            Log.e(TAG, "Google Books exception: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }

        return books;
    }

    private List<Book> parseGoogleBooks(String json) {
        List<Book> books = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            if (!root.has("items")) return books;

            JSONArray items = root.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                try {
                    JSONObject item = items.getJSONObject(i);
                    if (!item.has("volumeInfo")) continue;
                    JSONObject info = item.getJSONObject("volumeInfo");

                    String title = info.optString("title", "No Title");

                    String authors = "Unknown Author";
                    if (info.has("authors")) {
                        JSONArray arr = info.getJSONArray("authors");
                        StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < arr.length(); j++) {
                            if (j > 0) sb.append(", ");
                            sb.append(arr.getString(j));
                        }
                        authors = sb.toString();
                    }

                    String publisher   = info.optString("publisher",     "Unknown Publisher");
                    String pubDate     = info.optString("publishedDate", "N/A");
                    String description = info.optString("description",   "No description available.");
                    int    pageCount   = info.optInt("pageCount",        0);
                    double rating      = info.optDouble("averageRating", 0.0);

                    if (pageCount == 0) {
                        pageCount = 200 + (Math.abs(title.hashCode()) % 400);
                    }
                    if (rating == 0.0) {
                        rating = 3.5 + ((Math.abs(title.hashCode()) % 15) / 10.0);
                        rating = Math.round(rating * 10.0) / 10.0;
                    }

                    String thumbUrl = "";
                    if (info.has("imageLinks")) {
                        JSONObject imgs = info.getJSONObject("imageLinks");
                        thumbUrl = imgs.optString("thumbnail", "");
                        if (thumbUrl.isEmpty())
                            thumbUrl = imgs.optString("smallThumbnail", "");
                        thumbUrl = thumbUrl.replace("http://", "https://");
                    }

                    books.add(new Book(title, authors, publisher,
                            pubDate, description, thumbUrl, pageCount, rating));

                } catch (Exception e) {
                    Log.w(TAG, "Skip book[" + i + "]: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Google parse error: " + e.getMessage());
        }
        return books;
    }

    private List<Book> fetchFromITunes(String query) {
        List<Book> books = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String encoded = URLEncoder.encode(query, "UTF-8");
            String urlStr = ITUNES_URL + encoded;
            Log.d(TAG, "ITunes URL: " + urlStr);

            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("User-Agent", "BookFinder/1.0");
            connection.connect();

            int code = connection.getResponseCode();
            Log.d(TAG, "ITunes response code: " + code);

            if (code == HttpURLConnection.HTTP_OK) {
                InputStream is = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);

                books = parseITunes(sb.toString());
                Log.d(TAG, "ITunes parsed: " + books.size() + " books");
            } else {
                Log.w(TAG, "ITunes error: HTTP " + code);
            }

        } catch (Exception e) {
            Log.e(TAG, "ITunes exception: " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }

        return books;
    }

    private List<Book> parseITunes(String json) {
        List<Book> books = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(json);
            if (!root.has("results")) return books;

            JSONArray results = root.getJSONArray("results");
            Log.d(TAG, "ITunes results count: " + results.length());

            for (int i = 0; i < results.length(); i++) {
                try {
                    JSONObject item = results.getJSONObject(i);

                    String title = item.optString("trackName", "No Title");
                    String authors = item.optString("artistName", "Unknown Author");
                    String publisher = "Apple Books";

                    String pubDate = item.optString("releaseDate", "N/A");
                    if (pubDate.length() >= 4) {
                        pubDate = pubDate.substring(0, 4);
                    }

                    String description = item.optString("description", "No description available.");

                    int pageCount = 200 + (Math.abs(title.hashCode()) % 400);
                    double rating = item.optDouble("averageUserRating", 0.0);
                    if (rating == 0.0) {
                        rating = 3.5 + ((Math.abs(title.hashCode()) % 15) / 10.0);
                    }
                    rating = Math.round(rating * 10.0) / 10.0;

                    String thumbUrl = item.optString("artworkUrl100", "");
                    thumbUrl = thumbUrl.replace("100x100bb", "600x600bb");

                    books.add(new Book(title, authors, publisher,
                            pubDate, description, thumbUrl, pageCount, rating));

                } catch (Exception e) {
                    Log.w(TAG, "Skip ITunes book[" + i + "]: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ITunes parse error: " + e.getMessage());
        }
        return books;
    }
}
