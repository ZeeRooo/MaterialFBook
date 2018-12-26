package me.zeeroooo.materialfb.misc;

public class BookmarksH {
    private String url, title;

    public BookmarksH(String title, String url) {
            this.url = url;
            this.title = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
