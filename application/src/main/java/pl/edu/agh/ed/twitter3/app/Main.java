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

import java.util.List;

public class Main {
    private static Twitter twitter;
    private static TwitterUserRepository twitterUserRepository;
    private static TweetRepository tweetRepository;
    private static HashtagRepository hashtagRepository;


    public static void main(String[] args) throws TwitterException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        twitter = context.getBean(Twitter.class);

        tweetRepository = context.getBean(TweetRepository.class);
        twitterUserRepository = context.getBean(TwitterUserRepository.class);
        hashtagRepository = context.getBean(HashtagRepository.class);

        int tweetsProcessed = 0;

        Query query = new Query("" +
                "(northkorea) OR " +
                "(dprk) OR " +
                "(north korea) OR " +
                "(ww3) OR " +
                "(world war 3)"
        )
                .count(100)
                .lang("en")
                .since("2017-11-20");

        QueryResult result = tweetSearchWithLimit(query); // searchWithRetry is my function that deals with rate limits

        while (result.getTweets().size() != 0) {
            List<Status> tweets = result.getTweets();
            Long minId = Long.MAX_VALUE;

            for (Status tweet : tweets) {
                processAndSaveTweet(tweet);

                if (tweet.getId() < minId)
                    minId = tweet.getId();

                tweetsProcessed++;

                if(tweetsProcessed % 100 == 0) {
                    System.out.println(tweetsProcessed);
                }
            }
            query.setMaxId(minId - 1);
            result = tweetSearchWithLimit(query);
        }
    }

    private static void processAndSaveTweet(Status status) {
        Tweet tweet = new Tweet(status);

        TwitterUser twitterUser = saveUser(status);
        tweet.setTwitterUser(twitterUser);

        updateHashtags(status, tweet);
        updateUserMentions(status, tweet);

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

//            try {
//                followStaff(twitterUser, "FOLLOWERS");
//                followStaff(twitterUser, "FRIENDS");
//
//            } catch (TwitterException e) {
//                e.printStackTrace();
//            }

            twitterUser = twitterUserRepository.save(twitterUser);
        }
        return twitterUser;
    }

    private static QueryResult tweetSearchWithLimit(Query query) throws InterruptedException {
        QueryResult result;

        try {
            result = twitter.search(query);
        } catch (TwitterException e) {
            waitForLimitRenew(e);
            result = tweetSearchWithLimit(query);
        }

        return result;
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
            if(e.exceededRateLimitation()) {
                waitForLimitRenew(e);
                user = getUserWithLimit(userId);
            } else {
                e.printStackTrace();
            }
        }

        return user;
    }

    private static void waitForLimitRenew(TwitterException e) throws InterruptedException {
        int secondsUntilReset = e.getRateLimitStatus().getSecondsUntilReset() + 1;
        System.out.println("Sleeping " + secondsUntilReset + " seconds...");
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

    public static void followStaff(TwitterUser twitterUser, String direction) throws TwitterException {
        PagableResponseList<User> responseList;
        if (direction.equals("FOLLOWERS")) {
            responseList = twitter.getFollowersList(twitterUser.getId(), -1);
        } else if (direction.equals("FRIENDS")) {
            responseList = twitter.getFriendsList(twitterUser.getId(), -1);
        } else {
            return;
        }

        for (User followU : responseList) {
            TwitterUser followTU;
            followTU = twitterUserRepository.findOne(followU.getId());
            if (followTU == null) {
                followTU = new TwitterUser(followU);
                followTU = twitterUserRepository.save(followTU);
            }

            if (direction.equals("FOLLOWERS")) {
                twitterUser.addFollowedBy(followTU);
                followTU.addFollowing(twitterUser);
            } else {
                followTU.addFollowedBy(twitterUser);
                twitterUser.addFollowing(followTU);
            }

            twitterUserRepository.save(followTU);
        }
//        twitterUserRepository.save(twitterUser);
    }

//    public static List<Status> findReplies(String screenName, long tweetId) throws TwitterException {
//        Query query = new Query("to:" + screenName + " since_id:" + tweetId);
//        query.count(100);
//        QueryResult results = tweetSearchWithLimit(query);
//        List<Status> tweets = results.getTweets();
//
//        return tweets.stream().filter(tweet -> tweet.getInReplyToStatusId() == tweetId).collect(Collectors.toList());
//    }

//    public static Iterable<Tweet> prepareTweets(List<Status> statuses) {
//        return statuses.stream()
//                .map(status -> {
//                    Tweet newTweet = new Tweet(status);
//                    Status reetweetedStatus = status.getRetweetedStatus();
//                    Status quatedStatus = status.getQuotedStatus();
//                    List<Tweet> tweets = new ArrayList<>(3);
//                    try {
//                        tweets = findReplies(status.getUserWithLimit().getScreenName(), status.getId())
//                                .stream().map(Tweet::new).peek(tweet -> tweet.setInReplyToTweet(newTweet)).collect(Collectors.toList());
//                    } catch (TwitterException e) {
//                        e.printStackTrace();
//                    }
//                    if (reetweetedStatus != null) {
//                        Tweet reetweetedTweet = new Tweet(reetweetedStatus);
//                        newTweet.setRetweetedTweet(reetweetedTweet);
//                        tweets.add(reetweetedTweet);
//                    }
//                    if (quatedStatus != null) {
//                        Tweet quotedTweet = new Tweet(quatedStatus);
//                        newTweet.setQuotedTweet(quotedTweet);
//                        tweets.add(quotedTweet);
//                    }
//                    tweets.add(newTweet);
//
//
//                    System.out.println(tweets);
//                    return tweets;
//                })
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
//    }
}