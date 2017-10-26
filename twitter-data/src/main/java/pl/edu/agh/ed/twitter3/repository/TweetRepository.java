package pl.edu.agh.ed.twitter3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.ed.twitter3.model.Tweet;

public interface TweetRepository extends JpaRepository<Tweet, Long> {
}
