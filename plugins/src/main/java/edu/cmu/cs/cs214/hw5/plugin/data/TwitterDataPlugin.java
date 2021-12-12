package edu.cmu.cs.cs214.hw5.core.data;

import edu.cmu.cs.cs214.hw5.core.DataPlugin;

import java.util.ArrayList;
import java.util.List;
import twitter4j.*;

/**
 * TwitterDataPlugin - A data plugin for Text Emotion Analysis framework that uses twitter as a data source.
 * A user provides it with a name, and this will retrieve that individual's last hundred tweets or as many as they have
 * if under one hundred.
 */
public class TwitterDataPlugin implements DataPlugin {

    // name of plugin
    private static final String NAME = "Twitter Data Plugin";
    // description of plugin
    private static final String DESCRIPTION = "Please enter a twitter handle (omitting \\@) to retrieve that handle's" +
        " latest 100 tweets";
    private static final String EMPTY_PARAMS_ERROR = "Please input a valid Twitter handle";
    // max number of tweets to retrieve per page
    private static final int MAX_TWEET = 10;
    // max number of pages to retrieve
    private static final int MAX_PAGE = 10;

    private Twitter twitter;

    // getter for the name of the plugin
    @Override
    public String getName() {
        return NAME;
    }

    // getter for a small description of the plugin
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Given a twitter handle, retrieves that handle's last 100 tweets, or as many tweets as they have if the number is
     * less than 100. Groups tweets into t/10 groups of 10 + a group of t % 10 where t is the number
     * @param params a twitter handle
     * @return data a list of the user's tweets
     * @throws TwitterException if a handle is not found or invalid
     */
    @Override
    public List<String> getData(List<String> params) throws TwitterException {
        if (params.isEmpty()) {
            throw new TwitterException(EMPTY_PARAMS_ERROR);
        }
        int i = 1;
        List<String> result = new ArrayList<>();
        // get up to 10 tweets at a time
        Paging page = new Paging(1, MAX_TWEET);
        // get ten pages of 10 tweets
        while (i <= MAX_PAGE) {
            StringBuilder sb = new StringBuilder();
            page.setPage(i);
            ResponseList<Status> timeline = twitter.getUserTimeline(params.get(0), page);
            if (timeline.size() == 0) { break; }
            for (Status status : timeline) {
                if (status.isRetweet()) {
                    sb.append(status.getRetweetedStatus().getText());
                } else {
                    sb.append(status.getText());
                }
                sb.append(" ");
            }
            result.add(sb.toString());
            i++;
        }
        return result;
    }

    @Override
    public void onRegister() {
        twitter = new TwitterFactory().getInstance();
    }

}
