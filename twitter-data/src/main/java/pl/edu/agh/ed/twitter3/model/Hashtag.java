package pl.edu.agh.ed.twitter3.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "HASHTAG")
public class Hashtag {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "TEXT")
    @NaturalId
    private String text;

    @ManyToMany(mappedBy = "hashtags", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Tweet> tweets = new HashSet<>();

    public Hashtag() {
    }

    public Hashtag(String text) {
        this.text = text;
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

//    public Set<Tweet> getTweets() {
//        return tweets;
//    }
//
//    public void setTweets(Set<Tweet> tweets) {
//        this.tweets = tweets;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hashtag hashtag = (Hashtag) o;

        return text != null ? text.equals(hashtag.text) : hashtag.text == null;
    }

    @Override
    public int hashCode() {
        return text != null ? text.hashCode() : 0;
    }
}

