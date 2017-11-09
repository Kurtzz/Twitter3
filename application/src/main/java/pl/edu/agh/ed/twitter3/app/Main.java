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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static Twitter twitter;
    private static TwitterUserRepository twitterUserRepository;
    private static TweetRepository tweetRepository;
    private static HashtagRepository hashtagRepository;


    public static void main(String[] args) throws TwitterException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TwitterStream twitterStream = context.getBean(TwitterStream.class);
        twitter = context.getBean(Twitter.class);

        tweetRepository = context.getBean(TweetRepository.class);
        twitterUserRepository = context.getBean(TwitterUserRepository.class);
        hashtagRepository = context.getBean(HashtagRepository.class);

        FilterQuery query = new FilterQuery("northkorea", "ww3", "nuclear", "world war 3");
        query.language("en");

        twitterStream.addListener(new StatusListener() {
            @Override
            public void onException(Exception e) {

            }

            public void onStatus(Status status) {
                User user = status.getUser();
                TwitterUser twitterUser;

                twitterUser = twitterUserRepository.findOne(user.getId());
                if (twitterUser == null) {
                    twitterUser = new TwitterUser(user);

//                                        try {
//                                            followStaff(twitterUser, "FOLLOWERS");
//                                            followStaff(twitterUser, "FRIENDS");
//
//                                        } catch (TwitterException e) {
//                                            e.printStackTrace();
//                                        }

                    twitterUser = twitterUserRepository.save(twitterUser);
                }

                Tweet tweet = new Tweet(status);

                for (HashtagEntity hashtagEntity : status.getHashtagEntities()) {
                    Hashtag hashtag = updateAngGetHashtag(hashtagEntity);
                    tweet.getHashtags().add(hashtag);
                }

                for (UserMentionEntity userMentionEntity : status.getUserMentionEntities()) {
                    TwitterUser mentionedUser = null;
                    try {
                        mentionedUser = updateAndGetUser(userMentionEntity);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    tweet.getUserMentions().add(mentionedUser);
                }

                tweetRepository.save(tweet);
//              tweetRepository.save(prepareTweets(Collections.singletonList(status)));
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

    private static TwitterUser updateAndGetUser(UserMentionEntity userMentionEntity) throws TwitterException {
        long userId = userMentionEntity.getId();
        TwitterUser user = twitterUserRepository.findOne(userId);
        if (user == null) {
            user = new TwitterUser(twitter.showUser(userId));
        }
        return user;
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

    public static List<Status> findReplies(String screenName, long tweetId) throws TwitterException {
        Query query = new Query("to:" + screenName + " since_id:" + tweetId);
        query.count(100);
        QueryResult results = twitter.search(query);
        List<Status> tweets = results.getTweets();

        return tweets.stream().filter(tweet -> tweet.getInReplyToStatusId() == tweetId).collect(Collectors.toList());
    }

    public static Iterable<Tweet> prepareTweets(List<Status> statuses) {
        return statuses.stream()
                .map(status -> {
                    Tweet newTweet = new Tweet(status);
                    Status reetweetedStatus = status.getRetweetedStatus();
                    Status quatedStatus = status.getQuotedStatus();
                    List<Tweet> tweets = new ArrayList<>(3);
                    try {
                        tweets = findReplies(status.getUser().getScreenName(), status.getId())
                                .stream().map(Tweet::new).peek(tweet -> tweet.setInReplyToTweet(newTweet)).collect(Collectors.toList());
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    if (reetweetedStatus != null) {
                        Tweet reetweetedTweet = new Tweet(reetweetedStatus);
                        newTweet.setRetweetedTweet(reetweetedTweet);
                        tweets.add(reetweetedTweet);
                    }
                    if (quatedStatus != null) {
                        Tweet quotedTweet = new Tweet(quatedStatus);
                        newTweet.setQuotedTweet(quotedTweet);
                        tweets.add(quotedTweet);
                    }
                    tweets.add(newTweet);


                    System.out.println(tweets);
                    return tweets;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}