package pl.edu.agh.ed.twitter3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.agh.ed.twitter3.model.TwitterUser;

import java.util.Optional;

public interface TwitterUserRepository extends JpaRepository<TwitterUser, Long> {
}
