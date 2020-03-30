package me.zeeroooo.materialfb.misc;

public class ModelBookmarks {
    private String url, title;

    public ModelBookmarks(String title, String url) {
        this.url = url;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
