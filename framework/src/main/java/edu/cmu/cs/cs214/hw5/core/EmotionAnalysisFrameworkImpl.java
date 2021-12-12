package edu.cmu.cs.cs214.hw5.core;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * EmotionAnalysisFrameworkImpl - Implementation of the {@link EmotionAnalysisFramework}.
 * Uses IBM Watson's Natural Language Understanding API suite to run analysis on text. Uses a {@link DataPlugin} as a
 * data source that will retrieve text to be formed into a {@link EmotionAnalysis} object which is passed to a
 * {@link VisualPlugin} to visualize the analysis.
 */
public class EmotionAnalysisFrameworkImpl implements EmotionAnalysisFramework {

    /**
     * IBM Watson API key
     */
    private final String watsonAPIKey = getWatsonAPIKey();

    /**
     * Minimum length for IBM Watson text analysis
     */
    private static final int MIN_LENGTH = 15;

    private EmotionAnalysisFrameworkListener listener;
    private EmotionAnalysis analysis;
    private DataPlugin currentDataPlugin;
    private VisualPlugin currentVisualPlugin;

    /**
     * Registers a {@link EmotionAnalysisFrameworkListener} to observe changes in the {@link EmotionAnalysisFramework}
     * @param listener : a listener that responds to changes in the framework.
     */
    @Override
    public void setStateChangeListener(EmotionAnalysisFrameworkListener listener) {
        this.listener = listener;
    }

    /**
     * Registers a {@link DataPlugin} with the framework and notifies the observer
     * @param plugin the data plugin registered
     */
    @Override
    public void registerDataPlugin(DataPlugin plugin) {
        listener.onDataPluginRegistered(plugin);
    }

    /**
     * Sets the {@link DataPlugin} that will be used as a data source for the emotion analysis
     * @param plugin : the data plugin most recently selected by the framework GUI
     */
    @Override
    public void setCurrentDataPlugin(DataPlugin plugin) {
        currentDataPlugin = plugin;
    }

    /**
     * Registers a {@link VisualPlugin} with the framework and notifies the observer
     * @param plugin : the visual plugin most recently selected by the framework GUI
     */
    @Override
    public void registerVisualPlugin(VisualPlugin plugin) {
        listener.onVisualPluginRegistered(plugin);
    }

    /**
     * Sets the {@link VisualPlugin} that will be used to display the results of the emotion analysis
     * @param plugin : the visual plugin most recently selected by the framework gui
     */
    @Override
    public void setCurrentVisualPlugin(VisualPlugin plugin) {
        currentVisualPlugin = plugin;
    }

    /**
     * Gets API key from secret file
     * @return the Watson API Key
     */
    private String getWatsonAPIKey() {
        Properties prop = new Properties();
        try {
            FileInputStream is = new FileInputStream("../framework/src/main/resources/secret.properties");
            prop.load(is);
            String apiKey = prop.getProperty("WATSON_API_KEY");
            is.close();
            return apiKey;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Takes a string and removes all non-unicode characters such as emojis
     * @param text the text
     * @return text without any non-unicode characters
     */
    private String getTextOnly(String text) {
        text = text.strip();
        String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
        Pattern pattern = Pattern.compile(
                regex,
                Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(text);
        String result = matcher.replaceAll("");
        return result;
    }

    /**
     * Calls the IBM Watson API on the text and returns result of API request
     * @param text the text to analyze
     * @return the API analysis result
     */
    private AnalysisResults getDataFromAPI(String text) {
        try {
            Authenticator authenticator = new IamAuthenticator(watsonAPIKey);
            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding("2019-07-12", authenticator);

            // API call configurations
            EmotionOptions emotions = new EmotionOptions.Builder()
                    .build();

            KeywordsOptions keywords = new KeywordsOptions.Builder()
                    .emotion(true)
                    .build();

            Features features = new Features.Builder()
                    .emotion(emotions)
                    .keywords(keywords)
                    .build();
            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(text)
                    .features(features)
                    .build();

            return service.analyze(parameters).execute().getResult(); // Return API request result
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Method invoked by the gui when user selects a data plugin and indicates a request for data
     * @param source the source of the data grabbed from user input
     */
    public void requestDataFromSource(List<String> source) {
        if (currentDataPlugin == null) {
            listener.onCatchLoadException("Please select a data plugin");
        } else {
            listener.onDataRequest();
            try {
                List<Document> docs = new ArrayList<>();
                List<String> dataRequestResult = currentDataPlugin.getData(source);
                for (String text : dataRequestResult) {
                    AnalysisResults apiResult = getDataFromAPI(text); // Get text analysis results from Watson API
                    String textOnly = getTextOnly(text);
                    if (apiResult != null && textOnly.length() > MIN_LENGTH) {
                        try {
                            // Create new document with text and API results
                            Document doc = new Document(textOnly, apiResult);
                            docs.add(doc);
                        }
                        catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }

                }
                analysis = new EmotionAnalysisImpl(docs);
                listener.onDataReadyToDisplay();
            } catch (Exception e) {
                listener.onCatchLoadException(e.getMessage());
            }
        }
    }

    /**
     * Method invoked by the gui when user selects visual plugin and indicates a request for data
     */
    public void requestVisualizeData() {
        if (currentVisualPlugin == null) {
            listener.onCatchLoadException("Please select a visual plugin");
            return;
        }
        if (analysis == null) {
            listener.onCatchLoadException("Data not loaded from data plugin");
            return;
        }
        listener.onDataRequest();
        try {
            listener.onLoadVisualRequest(currentVisualPlugin.getVisual(analysis));
        } catch (Exception e) {
            listener.onCatchLoadException(e.getMessage());
        }
    }
}