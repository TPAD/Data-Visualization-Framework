package edu.cmu.cs.cs214.hw5.core.data;

import edu.cmu.cs.cs214.hw5.core.*;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * NewsPlugin -- A data plugin for Text Emotion Analysis framework that retrieves news article descriptions
 * according to a given keyword
 */
public class NewsPlugin implements DataPlugin {
    private final String apiKey = getNewsAPIKey();
    private static final String NAME = "News Plugin";
    private static final String DESCRIPTION = "Please enter a keyword to retrieve the top twenty " +
            "news articles containing that keyword";

    // URL for API request
    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything?q=";
    private static final String NEWS_API_URL_QUERY = "&apiKey=";

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
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Given a keyword, retrieves the top twenty news article descriptions containing that keyword
     * @param params the keyword
     * @return data a list of descriptions
     * @throws IllegalArgumentException if the keyword is invalid and did not return valid articles
     */
    @Override
    public List<String> getData(List<String> params) throws IllegalArgumentException {
        if(params.size() == 0) throw new IllegalArgumentException("Empty keyword");

        String keyword = params.get(0);
        List<String> descriptions = new ArrayList<>();
        String url = NEWS_API_URL + keyword + NEWS_API_URL_QUERY + apiKey;

        JSONObject response;
        try {
            response = getResponse(url);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Invalid Keyword");
        }

        for (Object o : response.getJSONArray("articles")) {
            JSONObject article = (JSONObject) o;
            descriptions.add(article.getString("description"));
        }
        return descriptions;
    }


    private String getNewsAPIKey() {
        Properties prop = new Properties();
        try {
            FileInputStream is = new FileInputStream("../framework/src/main/resources/secret.properties");
            prop.load(is);
            String apiKey = prop.getProperty("NEWS_API_KEY");
            is.close();
            return apiKey;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // get json response from url
    private JSONObject getResponse(String url) throws IOException {
        URL uri = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return new JSONObject(response.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRegister() {

    }

}
