package edu.cmu.cs.cs214.hw5.core;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

/**
 * EmotionAnalysisTest -- tests the EmotionAnalysis class
 */
public class EmotionAnalysisTest {
    private EmotionAnalysis analysis1, analysis2, analysis3;

    @Before
    public void setUp() {
        Map<Emotion, Double> emotions1 = new HashMap<>();
        Map<Emotion, Double> emotions2 = new HashMap<>();

        emotions1.put(Emotion.ANGER, 0.1);
        emotions1.put(Emotion.DISGUST, 0.2);
        emotions1.put(Emotion.FEAR, 0.3);
        emotions1.put(Emotion.JOY, 0.4);
        emotions1.put(Emotion.SADNESS, 0.5);

        emotions2.put(Emotion.ANGER, 0.2);
        emotions2.put(Emotion.DISGUST, 0.3);
        emotions2.put(Emotion.FEAR, 0.4);
        emotions2.put(Emotion.JOY, 0.3);
        emotions2.put(Emotion.SADNESS, 0.3);

        List<Keyword> keywords1 = new ArrayList<>();
        List<Keyword> keywords2 = new ArrayList<>();

        keywords1.add(new Keyword("Hello", (long)3, 0.5));
        keywords2.add(new Keyword("Hello", (long)4, 0.5));
        keywords2.add(new Keyword("World", (long)5, 0.5));

        Document doc1 = mock(Document.class);
        when(doc1.getEmotions()).thenReturn(emotions1);
        when(doc1.getKeywords()).thenReturn(keywords1);

        Document doc2 = mock(Document.class);
        when(doc2.getEmotions()).thenReturn(emotions2);
        when(doc2.getKeywords()).thenReturn(keywords2);

        analysis1 = new EmotionAnalysisImpl(new ArrayList<>(Collections.singletonList(doc1)));
        analysis2 = new EmotionAnalysisImpl(new ArrayList<>(Collections.singletonList(doc2)));
        analysis3 = new EmotionAnalysisImpl(new ArrayList<>(Arrays.asList(doc1, doc2)));
    }


    @Test
    public void testGetKeyword1(){
        Map<String, Long> keywords = analysis1.getKeywords();
        assertTrue(keywords.containsKey("Hello"));
        assertEquals(keywords.size(), 1);
    }

    @Test
    public void testGetKeyword3(){
        Map<String, Long> keywords = analysis3.getKeywords();
        assertTrue(keywords.containsKey("Hello"));
        assertTrue(keywords.containsKey("World"));
        assertEquals(keywords.size(), 2);
    }

    @Test
    public void testFrequency1() {
        Map<String, Long> keywords = analysis1.getKeywords();
        assertEquals(keywords.get("Hello"), Long.valueOf(3));
    }

    @Test
    public void testFrequency3() {
        Map<String, Long> keywords = analysis3.getKeywords();
        assertEquals(keywords.get("Hello"), Long.valueOf(7));
        assertEquals(keywords.get("World"), Long.valueOf(5));
    }

    @Test
    public void testAvgEmotion1() {
        equalsEmotions1(analysis1.getAvgEmotions());
    }

    private void equalsEmotions1(Map<Emotion, Double> emotions) {
        assertEquals(emotions.get(Emotion.ANGER), (Double)0.1);
        assertEquals(emotions.get(Emotion.DISGUST), (Double)0.2);
        assertEquals(emotions.get(Emotion.FEAR), (Double)0.3);
        assertEquals(emotions.get(Emotion.JOY), (Double)0.4);
        assertEquals(emotions.get(Emotion.SADNESS), (Double)0.5);
    }

    private void equalsEmotions3(Map<Emotion, Double> emotions) {
        assertEquals(emotions.get(Emotion.ANGER), (Double)0.2);
        assertEquals(emotions.get(Emotion.DISGUST), (Double)0.3);
        assertEquals(emotions.get(Emotion.FEAR), (Double)0.4);
        assertEquals(emotions.get(Emotion.JOY), (Double)0.3);
        assertEquals(emotions.get(Emotion.SADNESS), (Double)0.3);
    }

    @Test
    public void testAvgEmotion3() {
        Map<Emotion, Double> emotions = analysis3.getAvgEmotions();
        assertEquals(emotions.get(Emotion.ANGER), (Double)0.15, 0.001);
        assertEquals(emotions.get(Emotion.DISGUST), (Double)0.25);
        assertEquals(emotions.get(Emotion.FEAR), (Double)0.35);
        assertEquals(emotions.get(Emotion.JOY), (Double)0.35);
        assertEquals(emotions.get(Emotion.SADNESS), (Double)0.4);
    }

    @Test
    public void testAvgKeyEmotions1(){
        Map<String, Map<Emotion, Double>> keyEmotions = analysis1.getAvgKeyEmotions(1);
        Map<Emotion,Double> emotions = keyEmotions.get("Hello");
        equalsEmotions1(emotions);
    }

    @Test
    public void testAvgKeyEmotions2() {
        Map<String, Map<Emotion, Double>> keyEmotions = analysis3.getAvgKeyEmotions(2);

        Map<Emotion,Double> helloEmotions = keyEmotions.get("Hello");
        assertEquals(helloEmotions.get(Emotion.ANGER), 0.15, 0.001);
        assertEquals(helloEmotions.get(Emotion.DISGUST), (Double)0.25);
        assertEquals(helloEmotions.get(Emotion.FEAR), (Double)0.35);
        assertEquals(helloEmotions.get(Emotion.JOY), (Double)0.35);
        assertEquals(helloEmotions.get(Emotion.SADNESS), (Double)0.4);

        Map<Emotion,Double> worldEmotions = keyEmotions.get("World");
        equalsEmotions3(worldEmotions);
    }

    @Test
    public void testFilterKey() {
        EmotionAnalysis analysis = analysis3.filterKey("World");
        Map<String, Map<Emotion, Double>> keyEmotions = analysis.getAvgKeyEmotions(1);

        Map<Emotion,Double> helloEmotions = keyEmotions.get("Hello");
        equalsEmotions1(helloEmotions);
    }

    @Test
    public void testFilterEmotion() {
        EmotionAnalysis analysis = analysis3.filterEmotion(Emotion.SADNESS);
        Map<String, Map<Emotion, Double>> keyEmotions = analysis.getAvgKeyEmotions(2);

        Map<Emotion,Double> helloEmotions = keyEmotions.get("Hello");
        equalsEmotions3(helloEmotions);

        Map<Emotion,Double> worldEmotions = keyEmotions.get("World");
        equalsEmotions3(worldEmotions);
    }

    @Test
    public void testTopKeyword2() {
        Map<String, Map<Emotion, Double>> keyEmotions = analysis2.getAvgKeyEmotions(1);
        assertTrue(keyEmotions.containsKey("World"));
        assertFalse(keyEmotions.containsKey("Hello"));
    }

    @Test
    public void testTopKeyword3() {
        Map<String, Map<Emotion, Double>> keyEmotions = analysis3.getAvgKeyEmotions(1);
        assertTrue(keyEmotions.containsKey("Hello"));
        assertFalse(keyEmotions.containsKey("World"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKeyword() {
        analysis3.getAvgKeyEmotions(3);
    }

}
