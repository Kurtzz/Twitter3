package pl.edu.agh.ed.twitter3.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.edu.agh.ed.twitter3.config.AppConfig;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class Main {
    public static void main(String[] args) throws TwitterException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        Twitter twitter = context.getBean(Twitter.class);

        Query query = new Query("demo");
        query.setCount(1);
        QueryResult queryResult = twitter.search(query);
        System.out.println(queryResult.getTweets().get(0));
    }
}
