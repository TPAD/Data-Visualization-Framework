package edu.cmu.cs.cs214.hw5.core;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * EmotionAnalysisImpl - Implementation of the {@link EmotionAnalysis}
 */
class EmotionAnalysisImpl implements EmotionAnalysis {
    private List<Document> documents;
    private Map<Emotion, Double> avgEmotions;   // average emotions across all documents
    private Map<String, Long> keywords;         // map of keywords to their frequency
    private List<String> topKeywords;           // list of keywords sorted by frequency

    private static final String KEYWORD_ERROR = "Documents do not contain enough keywords for analysis";

    /**
     * Given a list of documents, analyzes the keywords and emotions across all documents
     * @param documents the list of documents
     */
    EmotionAnalysisImpl(List<Document> documents) {
        this.documents = documents;
        calculateKeywords();
        calculateTopKeywords();
        calculateAvgEmotions();
    }

    private void calculateTopKeywords() {
        // sorts the keywords by total frequency
        topKeywords = new ArrayList<>(keywords.keySet());

        topKeywords.sort((key1, key2) -> {
            int freqCompare =  keywords.get(key2).compareTo(keywords.get(key1));
            if(freqCompare == 0) return key1.compareTo(key2); // if frequencies are equal then compare string values
            return freqCompare;
        });
    }

    private void calculateKeywords() {
        keywords = new HashMap<>();

        // Adds the frequency of each keyword from each document
        for(Document document:documents) {
            for(Keyword keyword: document.getKeywords()) {
                long freq = keywords.getOrDefault(keyword.getWord(), (long)0);
                keywords.put(keyword.getWord(), freq + keyword.getFrequency());
            }
        }
    }

    private void calculateAvgEmotions() {
        avgEmotions = new HashMap<>();
        int size = documents.size();

        // initialize all emotion values to zero
        for(Emotion emotion: Emotion.values()) {
            avgEmotions.put(emotion, 0.0);
        }

        for(Document document: documents) {
            Map<Emotion, Double> emotions = document.getEmotions();
            for(Emotion emotion: Emotion.values()) {
                double relevance = emotions.get(emotion)/size;
                avgEmotions.put(emotion, avgEmotions.get(emotion) + relevance);
            }
        }
    }

    /**
     * Gets the keywords and their respective frequencies
     * @return a map of keywords to frequencies
     */
    @Override
    public Map<String, Long> getKeywords() {
        return keywords;
    }

    /**
     * Gets the average emotions associated with each keyword
     * Raises an IllegalArgumentException if numKeywords is greater than the number of keywords
     * @param numKeywords the number of keywords to analyze
     * @return a map of keywords to emotion levels
     */
    @Override
    public Map<String, Map<Emotion, Double>> getAvgKeyEmotions(int numKeywords) {
        if(numKeywords > keywords.size()) {
            throw new IllegalArgumentException(KEYWORD_ERROR);
        }

        List<String> keys = topKeywords.subList(0, numKeywords);

        Map<String, Map<Emotion, Double>> avgKeyEmotions = new HashMap<>();
        for(String key: keys) {
            avgKeyEmotions.put(key, getKeyEmotions(key));
        }
        return avgKeyEmotions;
    }


    /**
     * Gets the average emotion levels across all documents
     * @return map of emotions
     */
    @Override
    public Map<Emotion, Double> getAvgEmotions() {
        return avgEmotions;
    }

    /**
     * Filters out the documents containing the given keyword
     * @param key the keyword to be filtered
     * @return the emotion analysis after filtering
     */
    @Override
    public EmotionAnalysis filterKey(String key) {
        List<Document> filteredDocuments = new ArrayList<>(documents);
        for(Document document: documents) {
            for(Keyword keyword: document.getKeywords()) {
                if(keyword.getWord().equals(key)) {
                    filteredDocuments.remove(document);
                    break;
                }
            }
        }
        return new EmotionAnalysisImpl(filteredDocuments);
    }

    /**
     * Filters out the documents with the given main emotion
     * @param emotion the emotion to be filtered
     * @return the emotion analysis after filtering
     */
    @Override
    public EmotionAnalysis filterEmotion(Emotion emotion) {
        List<Document> filteredDocuments = new ArrayList<>(documents);
        for(Document document: documents) {
            if(getMainEmotion(document).equals(emotion))
                filteredDocuments.remove(document);
        }
        return new EmotionAnalysisImpl(filteredDocuments);
    }


    private Map<Emotion, Double> getKeyEmotions(String key){
        Map<Emotion, Double> emotionSum = new HashMap<>();
        Map<Emotion, Long> emotionFreq = new HashMap<>();

        for(Document document:documents){
            List<Keyword> keywords = document.getKeywords();
            Map<Emotion, Double> emotions = document.getEmotions();
            for(Keyword keyword: keywords){
                if(keyword.getWord().equals(key)) {
                    for(Emotion emotion: Emotion.values()) {
                        double value = emotions.get(emotion);
                        emotionSum.put(emotion, value + emotionSum.getOrDefault(emotion, 0.0));
                        emotionFreq.put(emotion, 1 + emotionFreq.getOrDefault(emotion, (long)0));
                    }
                }
            }
        }

        Map<Emotion, Double> avgEmotions = new HashMap<>();
        for(Emotion emotion: Emotion.values()) {
            avgEmotions.put(emotion, emotionSum.get(emotion)/emotionFreq.get(emotion));
        }

        return avgEmotions;
    }


    // Returns the main emotion of the document
    private Emotion getMainEmotion(Document document) {
        Map<Emotion, Double> emotions = document.getEmotions();
        Emotion currEmotion = null;
        Double currRelevance = -1.0;

        for(Emotion emotion: Emotion.values()) {
            if(emotions.get(emotion) > currRelevance) {
                currRelevance = emotions.get(emotion);
                currEmotion = emotion;
            }
        }
        return currEmotion;
    }
}