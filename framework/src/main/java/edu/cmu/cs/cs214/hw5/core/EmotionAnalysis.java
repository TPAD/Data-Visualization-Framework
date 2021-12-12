package edu.cmu.cs.cs214.hw5.core;

import java.util.Map;

/**
 * EmotionAnalysis - interface for result of emotion analysis
 */
public interface EmotionAnalysis {

    /**
     * Gets the keywords and their respective frequencies
     * @return a map of keywords to frequencies
     */
    Map<String, Long> getKeywords();

    /**
     * Gets the average emotions associated with each keyword
     * Raises an IllegalArgumentException if numKeywords is greater than the number of keywords
     * @param numKeywords the number of keywords to analyze
     * @return a map of keywords to emotion levels
     */
    Map<String, Map<Emotion, Double>> getAvgKeyEmotions(int numKeywords);

    /**
     * Gets the average emotion levels across all documents
     * @return map of emotions
     */
    Map<Emotion, Double> getAvgEmotions();

    /**
     * Filters out the documents containing the given keyword
     * @param key the keyword to be filtered
     * @return the emotion analysis after filtering
     */
    EmotionAnalysis filterKey(String key);

    /**
     * Filters out the documents with the given main emotion
     * @param emotion the emotion to be filtered
     * @return the emotion analysis after filtering
     */
    EmotionAnalysis filterEmotion(Emotion emotion);
}
