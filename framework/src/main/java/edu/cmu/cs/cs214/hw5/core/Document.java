package edu.cmu.cs.cs214.hw5.core;

import com.ibm.watson.natural_language_understanding.v1.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Document -- a Java class representing each data source. Has keywords, text,
 * emotions, and keywordEmotions. Calls the IBM Watson API to run analysis on
 * the keywords and emotions in the text.
 */
class Document {
    /**
     * IBM Watson API analysis result
     */
    private final AnalysisResults results;

    /**
     * The list of keywords in the text
     */
    private List<Keyword> keywords;

    /**
     * The document text
     */
    private final String text;

    /**
     * The emotions in the text
     */
    private Map<Emotion, Double> emotions;

    /**
     * The emotions associated with each keyword in the text
     */
    private Map<Keyword, Map<Emotion, Double>> keywordEmotions;

    /**
     * Constructs a new Document object given the text and API analysis results
     * @param text the text
     * @param results the API analysis results
     */
    Document(String text, AnalysisResults results) {
        this.text = text;
        this.results = results;
        parseAPIAnalysis();
    }

    /**
     * Gets the keywords
     * @return the keyword list
     */
    List<Keyword> getKeywords() {
        return keywords;
    }

    /**
     * Gets the text
     * @return the text string
     */
    String getText() {
        return text;
    }

    /**
     * Gets the emotions
     * @return the emotions map
     */
    Map<Emotion, Double> getEmotions() {
        return emotions;
    }

    /**
     * Gets the keyword-emotion associations
     * @return the map of keyword emotions
     */
    Map<Keyword, Map<Emotion, Double>> getKeywordEmotions() {
        return keywordEmotions;
    }

    /**
     * Parses data from API analysis result and sets fields
     */
    private void parseAPIAnalysis() {
        if (results == null) {
            throw new IllegalStateException("Unable to get API analysis results");
        }

        // get keywords and keyword emotions
        this.keywords = new ArrayList<>(); // initialize empty keywords list
        this.keywordEmotions = new HashMap<>(); // initialize empty keyword-emotions map

        for (KeywordsResult k : results.getKeywords()) {
            // add new keyword to keywords list
            Keyword newKeyword = new Keyword(k.getText(), k.getCount(), k.getRelevance());
            this.keywords.add(newKeyword);

            // add keyword and emotions to keyword emotions list
            Map<Emotion, Double> emotionMap = new HashMap<>();
            EmotionScores keyEmotions = k.getEmotion();

            emotionMap.put(Emotion.ANGER, keyEmotions.getAnger());
            emotionMap.put(Emotion.DISGUST, keyEmotions.getDisgust());
            emotionMap.put(Emotion.FEAR, keyEmotions.getFear());
            emotionMap.put(Emotion.JOY, keyEmotions.getJoy());
            emotionMap.put(Emotion.SADNESS, keyEmotions.getSadness());

            this.keywordEmotions.put(newKeyword, emotionMap);
        }

        // get emotions
        this.emotions = new HashMap<>(); // initialize empty emotions map
        EmotionScores e = results.getEmotion().getDocument().getEmotion();
        this.emotions.put(Emotion.ANGER, e.getAnger());
        this.emotions.put(Emotion.DISGUST, e.getDisgust());
        this.emotions.put(Emotion.FEAR, e.getFear());
        this.emotions.put(Emotion.JOY, e.getJoy());
        this.emotions.put(Emotion.SADNESS, e.getSadness());
    }
}