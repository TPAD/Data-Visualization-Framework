package edu.cmu.cs.cs214.hw5.core.data;

import edu.cmu.cs.cs214.hw5.core.DataPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * WebpagePlugin -- A data plugin for the Text Emotion Analysis framework that scrapes a webpage for text.
 * A user provides a URL and the plugin scrapes all body text from the webpage.
 */
public class WebpagePlugin implements DataPlugin {

    private static final String NAME = "Webpage Text Plugin";
    private static final String DESCRIPTION = "How to use me: Copy and paste webpage url. " +
            "Example: http://www.script-o-rama.com/movie_scripts/a1/bee-movie-script-transcript-seinfeld.html";

    /**
     * Getter for the name of the plugin
     *
     * @return the name of the data plugin
     */
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Gets string of HTML content given a url
     * @param url the webpage url
     * @return the html string
     */
    private String getHTML(String url) throws Exception {
        String htmlString = null;
        URLConnection connection = null;
        try {
            connection =  new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            htmlString = scanner.next();
            scanner.close();
        } catch ( Exception ex ) {
            throw new Exception("Invalid URL");
//            ex.printStackTrace();
        }
        return htmlString;
    }

    /**
     * Retrieves the data for the framework to process from a source specified in params
     *
     * @param urls the source of the data to retrieve
     * @return a list of text
     */
    @Override
    public List<String> getData(List<String> urls) throws Exception {
        List<String> textList = new ArrayList<>();
        for (String url : urls) {
            try {
                String html = getHTML(url);
                Document document = Jsoup.parse(html);
                String bodyText = document.body().text();
                textList.add(bodyText);
            } catch (Exception e) {
                throw new Exception("Invalid URL");
//                e.printStackTrace();
            }
        }
        return textList;
    }

    @Override
    public void onRegister() {

    }

}
