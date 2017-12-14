package pl.edu.agh.ed.twitter3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.ed.twitter3.model.Hashtag;
import pl.edu.agh.ed.twitter3.model.Tweet;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Hashtag findOneByText(String text);
}
