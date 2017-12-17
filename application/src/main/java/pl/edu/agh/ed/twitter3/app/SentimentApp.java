package pl.edu.agh.ed.twitter3.app;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLPClient;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.agh.ed.twitter3.config.AppConfig;
import pl.edu.agh.ed.twitter3.model.Tweet;
import pl.edu.agh.ed.twitter3.repository.TweetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SentimentApp {
    private static TweetRepository tweetRepository;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        tweetRepository = context.getBean(TweetRepository.class);

        Properties props = new Properties();
        props.setProperty("annotators", "sentiment");
        StanfordCoreNLPClient pipeline = new StanfordCoreNLPClient(props, "http://localhost", 9000, 4);

        List<Tweet> tweets = new ArrayList<>();
        Pageable pageable = new PageRequest(1, 1000, Sort.Direction.DESC, "id");

        for (Page<Tweet> page = createPage(pageable); page.hasNext(); page = createPage(page.nextPageable())) {
            tweets.clear();
            for (Tweet tweet : page.getContent()) {
                int sentiment = findSentiment(tweet.getText(), pipeline);
                tweet.setSentiment(sentiment);
                tweets.add(tweet);
            }
            tweetRepository.save(tweets);
            tweetRepository.flush();
            System.out.println(page.getNumber());
        }
    }

    private static Page<Tweet> createPage(Pageable pageable) {
        return tweetRepository.findAll(pageable);
    }

    private static int findSentiment(String text, StanfordCoreNLPClient pipeline) {
        int sumOfSentiments = 0;
        int count = 0;
        if (text != null && text.length() > 0) {
            Annotation annotation = pipeline.process(text);

            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                sumOfSentiments += RNNCoreAnnotations.getPredictedClass(tree);
                count++;
            }

            return sumOfSentiments / count;
        }
        return -1;
    }
}
