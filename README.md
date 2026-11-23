# BookFinder

An Android app for searching and browsing books. It queries the **Google Books API**, falls back to the **iTunes Search API** when no results come back, and shows the results in a list with a separate details view.

> Final project for the Mobile App Development 2 course.

## Features

- **Book search** from a collapsible search bar in the toolbar.
- **Two data sources** – Google Books first, iTunes e-books as an automatic fallback.
- **Books list** in a `RecyclerView` with cover, title and author, plus pull-to-refresh.
- **Details tab** showing cover, author, publisher, publish date, page count, rating and description.
- **Tabbed navigation** with `TabLayout` and `ViewPager`; selecting a book opens it in the Details tab.
- **Notifications** when a search finishes or fails, with the runtime permission request required on Android 13+.
- **Background work without third-party libraries** – networking uses `HttpURLConnection` on a worker thread, JSON is parsed with `org.json`, and cover images are loaded on an executor.

## Tech stack

| Area | Technology |
|---|---|
| Language | Java 11 |
| UI | Fragments, RecyclerView, CardView, Material Components, SwipeRefreshLayout |
| Networking | `HttpURLConnection`, `org.json` |
| APIs | Google Books API, iTunes Search API |
| Build | Gradle (Kotlin DSL), Android Gradle Plugin 8.13 |
| SDK | min 26 (Android 8.0), target 36 |

## Project structure

```
app/src/main/
├── java/com/example/finalproject/
│   ├── MainActivity.java         # Toolbar, search, tabs, notifications
│   ├── BooksListFragment.java    # Results list and pull-to-refresh
│   ├── BookDetailFragment.java   # Selected book details
│   ├── BookAdapter.java          # RecyclerView adapter
│   ├── ViewPagerAdapter.java     # Tabs adapter
│   ├── FetchBooksTask.java       # API requests and JSON parsing
│   ├── ImageLoadTask.java        # Cover image loading
│   └── Book.java                 # Model
└── res/                          # Layouts, drawables, menu, strings, themes
```

## How it works

1. The user types a query and taps search.
2. `FetchBooksTask` requests up to 20 results from Google Books on a background thread.
3. If the result is empty, the same query is sent to the iTunes Search API.
4. Results are posted back to the main thread and displayed in the list; a notification reports success or failure.
5. Tapping a book switches to the Details tab.

## Getting started

1. Clone the repository:
   ```bash
   git clone https://github.com/kh-ibrahim4/bookfinder-android.git
   ```
2. Open the folder in Android Studio and let Gradle sync.
3. Run the app on an emulator or a device running Android 8.0 or later.

No API key is needed: both APIs are used through their public endpoints.

## Possible improvements

- Cache results and covers so the app works offline.
- Save favourite books locally with Room.
- Add pagination for more than 20 results.
- Migrate from `ViewPager` to `ViewPager2` and to an MVVM architecture.

## Author

**Khalil I. Al-Absi** – [GitHub](https://github.com/kh-ibrahim4) · [LinkedIn](https://www.linkedin.com/in/khalil-alabsi/)
