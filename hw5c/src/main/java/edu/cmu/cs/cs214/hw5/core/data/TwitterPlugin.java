package edu.cmu.cs.cs214.hw5.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.cmu.cs.cs214.hw5.core.*;
import twitter4j.*;

/**
 * TwitterPlugin - a data plugin for the GeoData Framework which given a hashtag, searches for tweets containing
 * that hashtag. The plugin then returns information about those tweets including the number of retweets,
 * the number of favorites, the location of the user, the user's number of followers, and the user's number of friends.
 */
public class TwitterPlugin implements DataPlugin {
    private static final String NAME = "Twitter Plugin";
    private static final String HASHTAG_LABEL = "Hashtag";
    private static final String LOCATION_LABEL = "Location";
    private static final String RETWEET_LABEL = "Number of Retweets";
    private static final String FAVORITES_LABEL = "Number of Favorites";
    private static final String FRIENDS_LABEL = "Number of Friends";
    private static final String FOLLOWER_LABEL = "Number of Followers";
    private Twitter twitter = new TwitterFactory().getInstance();


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserInputConfig> getUserInputConfigs() {
        return Collections.singletonList(new UserInputConfig(HASHTAG_LABEL, UserInputType.TEXT_FIELD, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet loadData(Map<String, List<String>> params) {
        String tag = params.get(HASHTAG_LABEL).get(0);

        List<String> labels = Arrays.asList(LOCATION_LABEL, RETWEET_LABEL, FAVORITES_LABEL, FRIENDS_LABEL, FOLLOWER_LABEL);
        List<DataType> types = Arrays.asList(DataType.STRING, DataType.INTEGER, DataType.INTEGER, DataType.INTEGER, DataType.INTEGER);

        List<List<Object>> data = new ArrayList<>();
        Query query = new Query("#" + tag + " -filter:retweets"); // filter out retweets
        query.setCount(100);
        QueryResult result;

        try{
            result = twitter.search(query);
        } catch (TwitterException e) {
            throw new IllegalStateException(e);
        }

        for(Status status : result.getTweets()) {
            List<Object> row = new ArrayList<>();

            row.add(status.getUser().getLocation());
            row.add(status.getRetweetCount());
            row.add(status.getFavoriteCount());
            row.add(status.getUser().getFriendsCount());
            row.add(status.getUser().getFollowersCount());
            data.add(row);
        }

        return new DataSet(labels, types, data);
    }

}
