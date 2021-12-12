package edu.cmu.cs.cs214.hw5.core.visual;

import edu.cmu.cs.cs214.hw5.core.Emotion;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysis;
import edu.cmu.cs.cs214.hw5.core.VisualPlugin;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BarGraphPlugin - creates a bar graph representing the frequencies of the top ten
 * keywords computed by the EmotionAnalysisFramework. For each keyword, the
 * percentages of emotions associated with it are displayed on each bar in the graph.
 */
public class BarGraphPlugin implements VisualPlugin {
    private static final String NAME = "Bar Graph Plugin";

    // JPanel dimensions
    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    // Bar graph labels
    private static final String TITLE = "Keyword Frequencies";
    private static final String X_AXIS = "Keyword";
    private static final String Y_AXIS = "Frequency";

    private static final int NUM_KEYWORDS = 10; // number of bars in graph
    private List<Emotion> emotionsList; // an ordered list of emotions
    private Map<Emotion, Color> colors; // map of emotions to bar colors

    // Emotion colors for bar graph
    private static final Color JOY_COLOR = Color.YELLOW;
    private static final Color SADNESS_COLOR = Color.BLUE;
    private static final Color ANGER_COLOR = Color.RED;
    private static final Color DISGUST_COLOR = Color.GREEN;
    private static final Color FEAR_COLOR = Color.MAGENTA;

    /**
     * Creates the BarGraphPlugin
     */
    public BarGraphPlugin() {
        emotionsList = Arrays.asList(Emotion.values());
        colors = new HashMap<>();
        colors.put(Emotion.JOY, JOY_COLOR);
        colors.put(Emotion.SADNESS, SADNESS_COLOR);
        colors.put(Emotion.FEAR, FEAR_COLOR);
        colors.put(Emotion.ANGER, ANGER_COLOR);
        colors.put(Emotion.DISGUST, DISGUST_COLOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Constructs the bar graph of keyword frequencies
     * @param analysis data for the visual
     * @return a JPanel displaying the bar graph
     * @throws Exception if the bar graph could not be created
     */
    @Override
    public JPanel getVisual(EmotionAnalysis analysis) throws Exception {
        Map<String, Long> keywordFreq = analysis.getKeywords();
        Map<String, Map<Emotion, Double>> keywordEmotions = analysis.getAvgKeyEmotions(NUM_KEYWORDS);
        Map<String, Map<Emotion, Double>> normalizedFreq = getNormalized(keywordFreq, keywordEmotions);

        List<String> keywords = new ArrayList<>();
        Map<Emotion, List<Double>> frequencies = new HashMap<>();

        for(String key: keywordEmotions.keySet()) {
            keywords.add(key);

            // For each emotion, get a list of emotion levels corresponding to
            for(Emotion emotion: Emotion.values()) {
                List<Double> emotionFreq = frequencies.getOrDefault(emotion, new ArrayList<>());
                emotionFreq.add(normalizedFreq.get(key).get(emotion));
                frequencies.put(emotion, emotionFreq);
            }
        }

        return createBarGraph(keywords, frequencies);
    }

    private Map<String, Map<Emotion, Double>> getNormalized(Map<String, Long> frequencies, Map<String, Map<Emotion, Double>> emotions) {
        // Get the total emotion levels for each keyword
        Map<String, Double> emotionSum = new HashMap<>();
        for(String key: emotions.keySet()) {
            double sum = 0;
            for(Emotion emotion: Emotion.values()) {
                sum += emotions.get(key).get(emotion);
            }
            emotionSum.put(key, sum); // map total sum to keyword
        }


        Map<String, Map<Emotion, Double>> normalized = new HashMap<>();
        for(String key: emotions.keySet()) {
            Map<Emotion, Double> keyEmotions = new HashMap<>();

            double sum = frequencies.get(key);
            for(Emotion emotion: emotionsList) {
                keyEmotions.put(emotion, sum);
                sum -= emotions.get(key).get(emotion) * frequencies.get(key) / emotionSum.get(key);
            }
            normalized.put(key, keyEmotions);
        }

        return normalized;
    }

    private JPanel createBarGraph(List<String> keywords, Map<Emotion, List<Double>> frequencies)  {
        CategoryChart chart = new CategoryChartBuilder()
                .width(WIDTH)
                .height(HEIGHT)
                .title(TITLE)
                .xAxisTitle(X_AXIS)
                .yAxisTitle(Y_AXIS)
                .theme(Styler.ChartTheme.GGPlot2)
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setOverlapped(true);

        // Overlap
        // Because the emotions are ordered, previous emotions will not cover up
        for(Emotion emotion: emotionsList) {
            CategorySeries series = chart.addSeries(emotion.name(), keywords, frequencies.get(emotion));
            series.setFillColor(colors.get(emotion));
        }

        JPanel chartPanel = new XChartPanel<>(chart);
        chartPanel.validate();
        return chartPanel;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onRegister() {

    }

}
