package pl.edu.agh.ed.twitter3.model;

import twitter4j.User;

import java.util.Date;

public class TwitterUser {
    private long id;
    private String name;
    private String screenName;
    private Date createdAt;
    private boolean isVerified;
    private String getLocation;
    private int followersCount;
    private int friendsCount;
    private int favouritesCount;
    private int statusesCount;

    public TwitterUser() {
    }

    public TwitterUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.createdAt = user.getCreatedAt();
        this.isVerified = user.isVerified();
        this.getLocation = user.getLocation();
        this.followersCount = user.getFollowersCount();
        this.friendsCount = user.getFriendsCount();
        this.favouritesCount = user.getFavouritesCount();
        this.statusesCount = user.getStatusesCount();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getGetLocation() {
        return getLocation;
    }

    public void setGetLocation(String getLocation) {
        this.getLocation = getLocation;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }
}
