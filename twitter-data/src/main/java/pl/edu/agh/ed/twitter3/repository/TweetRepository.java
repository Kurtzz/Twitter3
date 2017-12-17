package pl.edu.agh.ed.twitter3.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.ed.twitter3.model.Tweet;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
    Page<Tweet> findByTextLike(String text, Pageable pageable);
    List<Tweet> findBySentimentEquals(int sentiment);
}
