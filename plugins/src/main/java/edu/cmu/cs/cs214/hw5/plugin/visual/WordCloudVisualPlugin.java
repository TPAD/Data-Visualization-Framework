package edu.cmu.cs.cs214.hw5.core.visual;

import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.LayeredWordCloud;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import edu.cmu.cs.cs214.hw5.core.Emotion;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysis;
import edu.cmu.cs.cs214.hw5.core.VisualPlugin;

import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WordCloudVisualPlugin - creates a "word cloud" to visualize data from the Text Emotion Analyzer Framework.
 * The visual consists of 5 "bubbles", one for each emotion: joy, anger, disgust, sadness and fear. The "bubbles" with
 * larger words signify that the corresponding emotion is exhibited more strongly in the text analyzed.
 */
public class WordCloudVisualPlugin implements VisualPlugin {

    private static final String NAME = "Word Cloud Visual Plugin";
    private static final String IMG_PATH_ONE = "src/main/resources/bubbles/pen1.png";
    private static final String IMG_PATH_TWO = "src/main/resources/bubbles/pen2.png";
    private static final String IMG_PATH_THREE = "src/main/resources/bubbles/pen3.png";
    private static final String IMG_PATH_FOUR = "src/main/resources/bubbles/pen4.png";
    private static final String IMG_PATH_FIVE = "src/main/resources/bubbles/pen5.png";
    private static final String JOY_STRING = "JOY";
    private static final String ANGER_STRING = "ANGER";
    private static final String DISGUST_STRING = "DISGUST";
    private static final String SADNESS_STRING = "SADNESS";
    private static final String FEAR_STRING = "FEAR";
    private static final Color JOY_COLOR = new Color(0xD8868B);
    private static final Color ANGER_COLOR = new Color(0xD5573B);
    private static final Color DISGUST_COLOR = new Color(0x94C9A9);
    private static final Color SADNESS_COLOR = new Color(0x777DA7);
    private static final Color FEAR_COLOR = new Color(0xC6ECAE);
    private static final int WIDTH = 601;
    private static final int HEIGHT = 601;
    private static final int LAYERS = 5;
    private static final int FREQ = 300;
    private static final int MULTIPLIER = 100;
    private static final int MIN_FONT = 5;
    private static final int LEGEND_HEIGHT = 30;

    private FrequencyAnalyzer frequencyAnalyzer;
    private Map<String, Integer> joyWords;
    private Map<String, Integer> angryWords;
    private Map<String, Integer> disgustWords;
    private Map<String, Integer> sadWords;
    private Map<String, Integer> fearWords;

    /**
     * @return the name of the visual plugin
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Constructs a word cloud
     * @param emotionAnalysis data for the visual
     * @return a JPanel containing the word cloud
     * @throws IOException if the files fail to lead or the building of the word cloud fails
     */
    @Override
    public JPanel getVisual(EmotionAnalysis emotionAnalysis) throws IOException {
        JPanel result = new JPanel();
        JLabel pic = new JLabel();
        onRegister();
        frequencyAnalyzer.setWordFrequenciesToReturn(FREQ);
        frequencyAnalyzer.setMinWordLength(1);
        Dimension dimension = new Dimension(WIDTH, HEIGHT);
        LayeredWordCloud layeredWordCloud = new LayeredWordCloud(LAYERS, dimension, CollisionMode.PIXEL_PERFECT);
        Map<String, Long> keywords = emotionAnalysis.getKeywords();
        Map<String, Map<Emotion, Double>> avgKeyEmotions = emotionAnalysis.getAvgKeyEmotions(keywords.size());
        loadWordsFrom(avgKeyEmotions);
        // the word cloud uses emotion values as frequencies to scale the words opposed to their actual frequencies
        List<WordFrequency> joyFrequencies = joyWords.keySet()
            .stream().map(s-> new WordFrequency(s, joyWords.get(s))).collect(Collectors.toList());
        List<WordFrequency> angryFrequencies = angryWords.keySet()
            .stream().map(s -> new WordFrequency(s, angryWords.get(s))).collect(Collectors.toList());
        List<WordFrequency> disgustFrequencies = disgustWords.keySet()
            .stream().map(s -> new WordFrequency(s, disgustWords.get(s))).collect(Collectors.toList());
        List<WordFrequency> sadFrequencies = sadWords.keySet()
            .stream().map(s -> new WordFrequency(s, sadWords.get(s))).collect(Collectors.toList());
        List<WordFrequency> fearFrequencies = fearWords.keySet()
            .stream().map(s -> new WordFrequency(s, fearWords.get(s))).collect(Collectors.toList());

        setPaddingFor(layeredWordCloud);
        setBackgroundLayersFor(layeredWordCloud);
        setColorPalettesFor(layeredWordCloud);
        // setting the word cloud's relative fonts
        layeredWordCloud.setFontScalar(0,
            new SqrtFontScalar(
                (Collections.min(joyWords.values()) < MIN_FONT ) ? MIN_FONT : Collections.min(joyWords.values()),
                Collections.max(joyWords.values())/2));
        layeredWordCloud.setFontScalar(1,
            new SqrtFontScalar(
                (Collections.min(angryWords.values()) < MIN_FONT) ? MIN_FONT : Collections.min(angryWords.values()),
                Collections.max(angryWords.values())/2));
        layeredWordCloud.setFontScalar(2,
            new SqrtFontScalar(
                (Collections.min(disgustWords.values()) < MIN_FONT) ? MIN_FONT : Collections.min(disgustWords.values()),
                Collections.max(disgustWords.values())/2));
        layeredWordCloud.setFontScalar(1+2,
            new SqrtFontScalar(
                (Collections.min(sadWords.values()) < MIN_FONT) ? MIN_FONT : Collections.min(sadWords.values()),
                Collections.max(sadWords.values())/2));
        layeredWordCloud.setFontScalar(2+2,
            new SqrtFontScalar(
                (Collections.min(fearWords.values()) < MIN_FONT) ? MIN_FONT : Collections.min(fearWords.values()),
                Collections.max(fearWords.values())/2));
        // building the word cloud image
        layeredWordCloud.build(0, joyFrequencies);
        layeredWordCloud.build(1, angryFrequencies);
        layeredWordCloud.build(2, disgustFrequencies);
        layeredWordCloud.build(1+2, sadFrequencies);
        layeredWordCloud.build(2+2, fearFrequencies);

        layeredWordCloud.setBackgroundColor(Color.black);
        pic.setIcon(new ImageIcon(layeredWordCloud.getBufferedImage()));
        result.setLayout(new BorderLayout());
        result.add(pic, BorderLayout.CENTER);
        result.add(legend(), BorderLayout.SOUTH);
        return result;
    }

    /**
     * Adds words to emotion maps. Every word gets mapped to every emotion with a value of the emotion value scaled
     * by ten and truncated
     * @param avgKeyEmotions the map of keywords and their emotions
     */
    private void loadWordsFrom(Map<String, Map<Emotion, Double>> avgKeyEmotions) {
        for (Map.Entry<String, Map<Emotion, Double>> keyEmotion : avgKeyEmotions.entrySet()) {
            String keyword = keyEmotion.getKey();
            Map<Emotion, Double> emotionMap = keyEmotion.getValue();
            for (Map.Entry<Emotion, Double> emotionEntry: emotionMap.entrySet()) {
                Emotion emotion = emotionEntry.getKey();
                Double val = emotionEntry.getValue();
                switch (emotion) {
                    case JOY:
                        joyWords.put(keyword, (int)(val * MULTIPLIER));
                        break;
                    case ANGER:
                        angryWords.put(keyword, (int)(val * MULTIPLIER));
                        break;
                    case DISGUST:
                        disgustWords.put(keyword, (int)(val * MULTIPLIER));
                        break;
                    case SADNESS:
                        sadWords.put(keyword, (int)(val * MULTIPLIER));
                        break;
                    case FEAR:
                        fearWords.put(keyword, (int)(val * MULTIPLIER));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Sets the padding between words in the layered word cloud
     * @param wordCloud the word cloud to adjust the padding
     */
    private void setPaddingFor(LayeredWordCloud wordCloud) {
        wordCloud.setPadding(0, 1);
        wordCloud.setPadding(1, 1);
        wordCloud.setPadding(2, 1);
        wordCloud.setPadding(1+2, 1);
        wordCloud.setPadding(2+2, 1);
    }

    /**
     * Sets the background layers for the layered word cloud
     * @param wordCloud the word cloud to set layers for
     * @throws IOException if the layers could not be opened
     */
    private void setBackgroundLayersFor(LayeredWordCloud wordCloud) throws IOException{
        wordCloud.setBackground(0, new PixelBoundryBackground(IMG_PATH_ONE));
        wordCloud.setBackground(1, new PixelBoundryBackground(IMG_PATH_TWO));
        wordCloud.setBackground(2, new PixelBoundryBackground(IMG_PATH_THREE));
        wordCloud.setBackground(1+2, new PixelBoundryBackground(IMG_PATH_FOUR));
        wordCloud.setBackground(2+2, new PixelBoundryBackground(IMG_PATH_FIVE));
    }

    /**
     * Sets the color palettes for each layer in the word cloud
     * @param wordCloud the word cloud to set color palettes for
     */
    private void setColorPalettesFor(LayeredWordCloud wordCloud) {
        wordCloud.setColorPalette(0, new ColorPalette(JOY_COLOR));
        wordCloud.setColorPalette(1, new ColorPalette(ANGER_COLOR));
        wordCloud.setColorPalette(2, new ColorPalette(DISGUST_COLOR));
        wordCloud.setColorPalette(1+2, new ColorPalette(SADNESS_COLOR));
        wordCloud.setColorPalette(2+2, new ColorPalette(FEAR_COLOR));
    }

    @Override
    public void onRegister() {
        frequencyAnalyzer = new FrequencyAnalyzer();
        joyWords = new HashMap<>();
        angryWords = new HashMap<>();
        disgustWords = new HashMap<>();
        sadWords = new HashMap<>();
        fearWords = new HashMap<>();
    }

    private JPanel legend() {
        JPanel result = new JPanel();
        JPanel joyPanel = new JPanel();
        JPanel angerPanel = new JPanel();
        JPanel disgustPanel = new JPanel();
        JPanel sadnessPanel = new JPanel();
        JPanel fearPanel = new JPanel();
        result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
        joyPanel.setBackground(JOY_COLOR);
        joyPanel.add(new JLabel(JOY_STRING));
        result.add(joyPanel);
        angerPanel.setBackground(ANGER_COLOR);
        angerPanel.add(new JLabel(ANGER_STRING));
        result.add(angerPanel);
        disgustPanel.setBackground(DISGUST_COLOR);
        disgustPanel.add(new JLabel(DISGUST_STRING));
        result.add(disgustPanel);
        sadnessPanel.setBackground(SADNESS_COLOR);
        sadnessPanel.add(new JLabel(SADNESS_STRING));
        result.add(sadnessPanel);
        fearPanel.setBackground(FEAR_COLOR);
        fearPanel.add(new JLabel(FEAR_STRING));
        result.add(fearPanel);
        result.setPreferredSize(new Dimension(WIDTH, LEGEND_HEIGHT));
        return result;
    }

}
