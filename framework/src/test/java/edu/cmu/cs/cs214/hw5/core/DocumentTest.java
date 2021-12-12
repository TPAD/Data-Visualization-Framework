package edu.cmu.cs.cs214.hw5.core;

import com.ibm.watson.natural_language_understanding.v1.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class DocumentTest {
    private Document document;
    private String text = "Hello Hello Goodbye Goodbye";

    @Before
    public void setUp() {
        EmotionScores scores = mock(EmotionScores.class);
        when(scores.getAnger()).thenReturn(0.1);
        when(scores.getDisgust()).thenReturn(0.2);
        when(scores.getFear()).thenReturn(0.3);
        when(scores.getJoy()).thenReturn(0.4);
        when(scores.getSadness()).thenReturn(0.5);


        KeywordsResult keyword = mock(KeywordsResult.class);
        when(keyword.getText()).thenReturn("Hello");
        when(keyword.getCount()).thenReturn((long)2);
        when(keyword.getRelevance()).thenReturn(0.1);
        when(keyword.getEmotion()).thenReturn(scores);

        List<KeywordsResult> keywords = new ArrayList<>(Arrays.asList(keyword));

        AnalysisResults results = mock(AnalysisResults.class);
        EmotionResult emotionResult = mock(EmotionResult.class);
        DocumentEmotionResults documentEmotionResults = mock(DocumentEmotionResults.class);

        when(results.getKeywords()).thenReturn(keywords);
        when(results.getEmotion()).thenReturn(emotionResult);
        when(emotionResult.getDocument()).thenReturn(documentEmotionResults);
        when(documentEmotionResults.getEmotion()).thenReturn(scores);

        document = new Document(text, results);

    }

    @Test
    public void testKeyword() {
        List<Keyword> keywords = document.getKeywords();
        assertEquals(keywords.size(), 1);

        Keyword keyword = keywords.get(0);
        assertEquals(keyword.getRelevance(), Double.valueOf(0.1));
        assertEquals(keyword.getFrequency(), Long.valueOf(2));
        assertEquals(keyword.getWord(), "Hello");
    }

    @Test
    public void testText() {
        assertEquals(document.getText(), text);
    }

    @Test
    public void testEmotions() {
        Map<Emotion, Double> emotions = document.getEmotions();
        assertEquals(emotions.get(Emotion.ANGER), Double.valueOf(0.1));
        assertEquals(emotions.get(Emotion.DISGUST), Double.valueOf(0.2));
        assertEquals(emotions.get(Emotion.FEAR), Double.valueOf(0.3));
        assertEquals(emotions.get(Emotion.JOY), Double.valueOf(0.4));
        assertEquals(emotions.get(Emotion.SADNESS), Double.valueOf(0.5));
    }


    @Test(expected = IllegalStateException.class)
    public void testEmptyResult() {
        new Document(text, null);
    }

}
