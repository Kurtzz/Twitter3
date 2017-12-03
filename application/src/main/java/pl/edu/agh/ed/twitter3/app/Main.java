package pl.edu.agh.ed.twitter3.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import pl.edu.agh.ed.twitter3.config.AppConfig;
import pl.edu.agh.ed.twitter3.model.Hashtag;
import pl.edu.agh.ed.twitter3.model.Tweet;
import pl.edu.agh.ed.twitter3.model.TwitterUser;
import pl.edu.agh.ed.twitter3.repository.HashtagRepository;
import pl.edu.agh.ed.twitter3.repository.TweetRepository;
import pl.edu.agh.ed.twitter3.repository.TwitterUserRepository;
import twitter4j.*;

public class Main {
    private static Twitter twitter;
    private static TwitterUserRepository twitterUserRepository;
    private static TweetRepository tweetRepository;
    private static HashtagRepository hashtagRepository;


    static int tweetsSaved = 0;
    static int usersSaved = 0;
    static double totalWaitTime = 0.0;

    private static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TwitterStream twitterStream = context.getBean(TwitterStream.class);
        twitter = context.getBean(Twitter.class);

        tweetRepository = context.getBean(TweetRepository.class);
        twitterUserRepository = context.getBean(TwitterUserRepository.class);
        hashtagRepository = context.getBean(HashtagRepository.class);


        FilterQuery query = new FilterQuery("north korea", "dprk", "northkorea", "nuclear war");
        query.language("en");

        twitterStream.addListener(new StatusListener() {
            @Override
            public void onException(Exception e) {

            }

            public void onStatus(Status status) {
                try {
                    if(tweetsSaved % 10 == 0) {
                        System.out.println("Tweets saved " + tweetsSaved);
                        System.out.println("Users saved " + usersSaved);
                        System.out.println("Total wait time " + totalWaitTime + " min");
                        long currentTime = System.currentTimeMillis();
                        System.out.println("Program run time " + (double)(currentTime - startTime)/(1000.0*60.0) + " min");
                        System.out.println("---------------------------------------------------------");
                    }

                    processAndSaveStatus(status);
                } catch (TwitterException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            @Override
            public void onTrackLimitationNotice(int i) {

            }

            @Override
            public void onScrubGeo(long l, long l1) {

            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {

            }
        });

        twitterStream.filter(query);


    }

    private static void processAndSaveStatus(Status status) throws TwitterException, InterruptedException {
        Tweet tweet = addStatusDataIfNotPresent(status);

        tweet = saveRetweetedTweetIfPresent(status, tweet);
        tweet = saveQuotedStatusIfPresent(status, tweet);
        tweet = saveInReplyToStatusIfPresent(status, tweet);

        tweetRepository.save(tweet);
    }

    private static void updateUserMentions(Status status, Tweet tweet) {
        for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
            TwitterUser mentionedUser = null;
            try {
                mentionedUser = updateAndGetUser(userMentionEntity);
            } catch (TwitterException | InterruptedException e) {
                e.printStackTrace();
            }
            tweet.getUserMentions().add(mentionedUser);
        }
    }

    private static void updateHashtags(Status status, Tweet tweet) {
        for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
            Hashtag hashtag = updateAngGetHashtag(hashtagEntity);
            tweet.getHashtags().add(hashtag);
        }
    }

    private static TwitterUser saveUser(Status status) {
        User user = status.getUser();
        TwitterUser twitterUser;

        twitterUser = twitterUserRepository.findOne(user.getId());
        if (twitterUser == null) {
            twitterUser = new TwitterUser(user);

            usersSaved++;
            twitterUser = twitterUserRepository.save(twitterUser);
        }
        return twitterUser;
    }

    private static Status getStatusWithLimit(long statusId) throws InterruptedException {
        Status status = null;
        try {
            status = twitter.showStatus(statusId);
        } catch (TwitterException e) {
            e.printStackTrace();
            if(e.exceededRateLimitation()) {
                waitForLimitRenew(e);
                status = getStatusWithLimit(statusId);
            }
        }

        return status;
    }

    private static TwitterUser updateAndGetUser(UserMentionEntity userMentionEntity) throws TwitterException, InterruptedException {
        long userId = userMentionEntity.getId();
        TwitterUser user = twitterUserRepository.findOne(userId);
        if (user == null) {
            user = new TwitterUser(getUserWithLimit(userId));
            twitterUserRepository.save(user);
        }
        return user;
    }

    private static User getUserWithLimit(long userId) throws TwitterException, InterruptedException {
        User user = null;

        try {
            user = twitter.showUser(userId);
        } catch (TwitterException e) {
            e.printStackTrace();
            if(e.exceededRateLimitation()) {
                waitForLimitRenew(e);
                user = getUserWithLimit(userId);
            }
        }

        return user;
    }

    private static void waitForLimitRenew(TwitterException e) throws InterruptedException {
        int secondsUntilReset = e.getRateLimitStatus().getSecondsUntilReset() + 1;
        System.out.println("Sleeping " + secondsUntilReset / 60.0 + " minutes...");
        totalWaitTime += secondsUntilReset / 60.0;
        Thread.sleep(secondsUntilReset * 1000);
    }

    private static Hashtag updateAngGetHashtag(HashtagEntity hashtagEntity) {
        Hashtag hashtag = hashtagRepository.findOneByText(hashtagEntity.getText());
        if (hashtag == null) {
            hashtag = new Hashtag(hashtagEntity.getText());
            hashtagRepository.save(hashtag);
        }
        return hashtag;
    }

    private static Tweet saveInReplyToStatusIfPresent(Status status, Tweet tweet) throws InterruptedException {
        long inReplyToStatusId = status.getInReplyToStatusId();
        Status inReplyToStatus = null;
        if (inReplyToStatusId != -1) {
            inReplyToStatus = getStatusWithLimit(inReplyToStatusId);
        }

        if (inReplyToStatus != null) {
            Tweet inReplyToTweet = addStatusDataIfNotPresent(inReplyToStatus);
            tweetRepository.save(inReplyToTweet);

            tweet.setInReplyToTweet(inReplyToTweet);
        }

        return tweet;
    }

    private static Tweet saveQuotedStatusIfPresent(Status status, Tweet tweet) {
        Status quatedStatus = status.getQuotedStatus();
        if (quatedStatus != null) {
            Tweet quotedTweet = addStatusDataIfNotPresent(quatedStatus);
            tweetRepository.save(quotedTweet);

            tweet.setQuotedTweet(quotedTweet);
        }

        return tweet;
    }

    private static Tweet saveRetweetedTweetIfPresent(Status status, Tweet tweet) {
        Status retweetedStatus = status.getRetweetedStatus();
        if (retweetedStatus != null) {
            Tweet retweetedTweet = addStatusDataIfNotPresent(retweetedStatus);
            tweetRepository.save(retweetedTweet);

            tweet.setRetweetedTweet(retweetedTweet);
        }

        return tweet;
    }

    private static Tweet addStatusDataIfNotPresent(Status status) {
        Tweet tweet = tweetRepository.findOne(status.getId());
        if (tweet == null) {
            tweetsSaved++;

            tweet = new Tweet(status);

            TwitterUser twitterUser = saveUser(status);
            tweet.setTwitterUser(twitterUser);

            updateHashtags(status, tweet);
            updateUserMentions(status, tweet);
        }

        return tweet;
    }
}