CREATE TABLE `TWITTER_USER` (
  `ID` bigint(20) NOT NULL PRIMARY KEY,
  `NAME` varchar(255) DEFAULT NULL,
  `SCREEN_NAME` varchar(255) DEFAULT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  `IS_VERIFIED` bit(1) DEFAULT NULL,
  `LOCATION` varchar(255) DEFAULT NULL,
  `FAVOURITES_COUNT` int(11) DEFAULT NULL,
  `FOLLOWERS_COUNT` int(11) DEFAULT NULL,
  `FRIENDS_COUNT` int(11) DEFAULT NULL,
  `STATUSES_COUNT` int(11) DEFAULT NULL
) ENGINE=INNODB;

CREATE TABLE `FOLLOW` (
  `FOLLOWED_ID` bigint(20) NOT NULL,
  `FOLLOWING_ID` bigint(20) NOT NULL,
  PRIMARY KEY (FOLLOWED_ID, FOLLOWING_ID),
  CONSTRAINT FK_FOLLOWED_TWITTER_USER_ID FOREIGN KEY (FOLLOWED_ID) REFERENCES TWITTER_USER(ID),
  CONSTRAINT FK_FOLLOWING_TWITTER_USER_ID FOREIGN KEY (FOLLOWING_ID) REFERENCES TWITTER_USER(ID)
);

CREATE TABLE `TWEET` (
  `ID` bigint(20) NOT NULL PRIMARY KEY,
  `TWITTER_USER_ID` bigint(20) DEFAULT NULL,
  `CREATED_AT` datetime DEFAULT NULL,
  `TEXT` varchar(255) DEFAULT NULL,
  `LANG` varchar(3) DEFAULT NULL,
  `FAVORITE_COUNT` int(11) DEFAULT NULL,
  `RETWEET_COUNT` int(11) DEFAULT NULL,
  `SOURCE` varchar(255) DEFAULT NULL,
  `RETWEETED_TWEET_ID` bigint(20) DEFAULT -1,
  `QUOTED_TWEET_ID` bigint(20) DEFAULT -1,
  `IN_REPLY_TO_TWEET_ID` BIGINT(20) DEFAULT -1,
  CONSTRAINT FK_TWEET_TWITTER_USER_ID FOREIGN KEY (TWITTER_USER_ID) REFERENCES TWITTER_USER(ID),
  CONSTRAINT FK_TWEET_RETWEETED_TWEET_ID FOREIGN KEY (RETWEETED_TWEET_ID) REFERENCES TWEET(ID),
  CONSTRAINT FK_TWEET_QUOTED_TWEET_ID FOREIGN KEY (QUOTED_TWEET_ID) REFERENCES TWEET(ID),
  CONSTRAINT FK_TWEET_IN_REPLY_TO_TWEET_ID FOREIGN KEY (IN_REPLY_TO_TWEET_ID) REFERENCES TWEET(ID)
) ENGINE=INNODB;

