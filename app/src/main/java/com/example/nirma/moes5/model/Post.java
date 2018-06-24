package com.example.nirma.moes5.model;

/**
 * Created by mvryan on 13/06/18.
 * this class is model for article
 */

public class Post {
    private String link;
    private Title title;
    private Content content;
    private String slug;
    private String date;

    public String getLink() {
        return link;
    }

    public Title getTitle() {
        return title;
    }

    public Content getContent() {
        return content;
    }

    public String getSlug() {
        return slug;
    }

    public String getDate() {
        return date;
    }

    public class Title {
        private String rendered;

        public String getRendered() {
            return rendered;
        }
    }

    public class Content {
        private String rendered;

        public String getRendered() {
            return rendered;
        }
    }
}
