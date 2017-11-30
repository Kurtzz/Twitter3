package pl.edu.agh.ed.twitter3.model;

import twitter4j.HashtagEntity;
import twitter4j.Status;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "TWEET")
public class Tweet {
    @Id @Column(name = "ID")
    private long id;

    @Column(name = "TEXT")
    private String text;

    @ManyToOne(fetch= FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="TWITTER_USER_ID", foreignKey = @ForeignKey(name = "TWITTER_USER_ID"))
    private TwitterUser twitterUser;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "LANG")
    private String lang;

    @Column(name = "FAVORITE_COUNT")
    private int favoriteCount;

    @Column(name = "RETWEET_COUNT")
    private int retweetCount;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "RETWEETED_TWEET_ID", referencedColumnName = "ID")
    private Tweet retweetedTweet;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "QUOTED_TWEET_ID", referencedColumnName = "ID")
    private Tweet quotedTweet;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "IN_REPLY_TO_TWEET_ID", referencedColumnName = "ID")
    private Tweet inReplyToTweet;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "TWEET_HASHTAGS",
            joinColumns = @JoinColumn(name = "TWEET_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "HASHTAG_ID", referencedColumnName = "ID")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @JoinTable(name = "TWEET_USER_MENTIONS",
            joinColumns = @JoinColumn(name = "TWEET_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    )
    private Set<TwitterUser> userMentions = new HashSet<>();

    public Tweet() {
    }

    public Tweet(Status status) {
        this.id = status.getId();
        this.text = removeIllegalChars(status.getText());
//        this.twitterUser = new TwitterUser(status.getUser());
        this.createdAt = status.getCreatedAt();
        this.lang = status.getLang();
        this.favoriteCount = status.getFavoriteCount();
        this.retweetCount = status.getRetweetCount();
    }

    public String removeIllegalChars(String s) {
        if (s == null) return null;
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < s.length() ; i++){
            if (Character.isHighSurrogate(s.charAt(i))) continue;
            sb.append(s.charAt(i));
        }
        return sb.toString();
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

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    public void setRetweetedTweet(Tweet retweetedTweet) {
        this.retweetedTweet = retweetedTweet;
    }

    public Tweet getQuotedTweet() {
        return quotedTweet;
    }

    public void setQuotedTweet(Tweet quotedTweet) {
        this.quotedTweet = quotedTweet;
    }

    public Tweet getInReplyToTweet() {
        return inReplyToTweet;
    }

    public void setInReplyToTweet(Tweet inReplyToTweet) {
        this.inReplyToTweet = inReplyToTweet;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public Set<TwitterUser> getUserMentions() {
        return userMentions;
    }

    public void setUserMentions(Set<TwitterUser> userMentions) {
        this.userMentions = userMentions;
    }
}

