package pl.edu.agh.ed.twitter3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
@PropertySource("classpath:twitter4j.properties")
@ComponentScan({"pl.edu.agh.ed.twitter3"})
public class TwitterConfig {

    private final TwitterProperties twitterProperties;

    @Autowired
    public TwitterConfig(TwitterProperties twitterProperties) {
        this.twitterProperties = twitterProperties;
    }

    @Bean
    public twitter4j.conf.Configuration configuration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterProperties.getConsumerKey())
                .setOAuthConsumerSecret(twitterProperties.getConsumerSecret())
                .setOAuthAccessToken(twitterProperties.getAccessToken())
                .setOAuthAccessTokenSecret(twitterProperties.getAccessTokenSecret());

        return configurationBuilder.build();
    }

    @Bean
    public Twitter twitter(twitter4j.conf.Configuration configuration) {
        return new TwitterFactory(configuration).getInstance();
    }

    @Bean
    public TwitterStream twitterStream(twitter4j.conf.Configuration configuration) {
        return new TwitterStreamFactory(configuration).getInstance();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
