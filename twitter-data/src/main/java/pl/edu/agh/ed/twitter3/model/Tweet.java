package pl.edu.agh.ed.twitter3.model;

import twitter4j.Status;

import java.util.Date;

public class Tweet {
    private long id;
    private String text;
    private TwitterUser twitterUser;
    private Date createdAt;
    private String lang;
    private int favoriteCount;
    private int retweetCount;
    private String source; //TODO: necessary?

    public Tweet() {
    }

    public Tweet(Status status) {
        this.id = status.getId();
        this.text = status.getText();
        this.twitterUser = new TwitterUser(status.getUser());
        this.createdAt = status.getCreatedAt();
        this.lang = status.getLang();
        this.favoriteCount = status.getFavoriteCount();
        this.retweetCount = status.getRetweetCount();
        this.source = status.getSource();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TwitterUser getTwitterUser() {
        return twitterUser;
    }

    public void setTwitterUser(TwitterUser twitterUser) {
        this.twitterUser = twitterUser;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(int retweetCount) {
        this.retweetCount = retweetCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

