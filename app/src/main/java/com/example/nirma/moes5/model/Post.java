package com.example.nirma.moes5.model;

/**
 * Created by mvryan on 13/06/18.
 * this class is model for article
 */

public class Post {
    private String id;
    private String image;
    private Title title;
    private String category;
    private Content content;
    private Excerpt excerpt;

    public Post(String id, String image, Title title, String category, Content content, Excerpt excerpt) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.category = category;
        this.content = content;
        this.excerpt = excerpt;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public Title getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public Content getContent() {
        return content;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public class Excerpt{
        private String rendered;

        public String getRendered(){return rendered;}
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
