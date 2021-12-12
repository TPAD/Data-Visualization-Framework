package edu.cmu.cs.cs214.hw5.core;

/**
 * Keyword -- a Java class representing each keyword in a text with a word, a frequency, and a relevance.
 */
class Keyword {

    /**
     * The keyword phrase
     */
    private final String word;

    /**
     * The keyword frequency
     */
    private final Long frequency;

    /**
     * The keyword relevance
     */
    private final Double relevance;

    /**
     * Constructs a new Keyword object given the word, frequency, and relevance
     * @param word the word
     * @param frequency the frequency in the text
     * @param relevance the relevance in the text
     */
    Keyword(String word, Long frequency, Double relevance) {
        this.word = word;
        this.frequency = frequency;
        this.relevance = relevance;
    }

    /**
     * Gets the keyword
     * @return the keyword
     */
    String getWord() {
        return word;
    }

    /**
     * Gets the frequency
     * @return the frequency
     */
    Long getFrequency() {
        return frequency;
    }

    /**
     * Gets the relevance
     * @return the relevance
     */
    Double getRelevance() {
        return relevance;
    }

    /**
     * Returns the keyword as a string
     * @return the keyword
     */
    @Override
    public String toString() {
        return word;
    }
}
