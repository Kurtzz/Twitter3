package pl.edu.agh.ed.twitter3.model;

import twitter4j.User;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "TWITTER_USER")
public class TwitterUser {
    @Id @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SCREEN_NAME")
    private String screenName;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "IS_VERIFIED")
    private boolean isVerified;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "FOLLOWERS_COUNT")
    private int followersCount;

    @Column(name = "FRIENDS_COUNT")
    private int friendsCount;

    @Column(name = "FAVOURITES_COUNT")
    private int favouritesCount;

    @Column(name = "STATUSES_COUNT")
    private int statusesCount;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "FOLLOW",
            joinColumns = @JoinColumn(name = "FOLLOWING_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "FOLLOWED_ID", referencedColumnName = "ID")
    )
    private Set<TwitterUser> following = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "following", fetch = FetchType.EAGER)
    private Set<TwitterUser> followedBy = new LinkedHashSet<>();

    public TwitterUser() {
    }

    public TwitterUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.screenName = user.getScreenName();
        this.createdAt = user.getCreatedAt();
        this.isVerified = user.isVerified();
        this.location = user.getLocation();
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Set<TwitterUser> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(Set<TwitterUser> followedBy) {
        this.followedBy = followedBy;
    }

    public void addFollowedBy(TwitterUser followed) {
        this.followedBy.add(followed);
    }

    public Set<TwitterUser> getFollowing() {
        return following;
    }

    public void setFollowing(Set<TwitterUser> following) {
        this.following = following;
    }

    public void addFollowing(TwitterUser folowing) {
        this.following.add(folowing);
    }


}
