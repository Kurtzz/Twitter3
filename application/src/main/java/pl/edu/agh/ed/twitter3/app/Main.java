package pl.edu.agh.ed.twitter3.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.edu.agh.ed.twitter3.config.AppConfig;
import pl.edu.agh.ed.twitter3.model.Tweet;
import pl.edu.agh.ed.twitter3.repository.TweetRepository;
import twitter4j.*;

import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws TwitterException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Twitter twitter = context.getBean(Twitter.class);
        TweetRepository repository = context.getBean(TweetRepository.class);
        int tweetsNumber = 5000;

        Query query = new Query("demo");
        query.setCount(100);
        QueryResult queryResult = twitter.search(query);
        repository.save(queryResult.getTweets().stream().map(Tweet::new).collect(Collectors.toList()));

        while (queryResult.hasNext() && repository.count() < tweetsNumber) {
            queryResult = twitter.search(queryResult.nextQuery());
            repository.save(queryResult.getTweets().stream().map(Tweet::new).collect(Collectors.toList()));
        }
    }
}
