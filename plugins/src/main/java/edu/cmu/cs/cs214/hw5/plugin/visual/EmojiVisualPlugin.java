package edu.cmu.cs.cs214.hw5.core.visual;

import edu.cmu.cs.cs214.hw5.core.Emotion;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysis;
import edu.cmu.cs.cs214.hw5.core.VisualPlugin;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * EmojiVisualPlugin -- creates a visual with emojis representing the emotions. The size of the emoji faces
 * corresponds to the emotion percentage in the text.
 */
public class EmojiVisualPlugin implements VisualPlugin {

    private static final String NAME = "Emoji Visual Plugin";

    /**
     * File paths to emoji images
     */
    private static final String ANGRY_FILE = "src/main/resources/emojis/angry_emoji.png";
    private static final String DISGUST_FILE = "src/main/resources/emojis/disgust_emoji.png";
    private static final String FEAR_FILE = "src/main/resources/emojis/fear_emoji.png";
    private static final String JOY_FILE = "src/main/resources/emojis/joy_emoji.png";
    private static final String SADNESS_FILE = "src/main/resources/emojis/sadness_emoji.png";

    /**
     * JPanel dimensions
     */
    private static final int WIDTH = 960;
    private static final int HEIGHT = 630;

    /**
     * Getter for the visual plugin's name
     * @return the name of the visual plugin
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Calculates the new scaled size for resizing from emotion data
     * @param value percentage of emotion from emotion data
     * @return an integer of the appropriate scaled size (used for both height and width)
     */
    private int calculateScaledSize(Double value) {
        double scaled = value * HEIGHT;
        return (int) scaled;
    }

    /**
     * Scales an emoji image given its percentage
     * @param imageIcon the emoji image icon
     * @param percentage the percentage of the corresponding emotion
     * @return the scaled imageIcon object
     */
    private ImageIcon resizeEmojiImage(ImageIcon imageIcon, Double percentage) {
        Image image = imageIcon.getImage(); // Convert to an Image
        int scaledSize = calculateScaledSize(percentage);
        Image scaledImage = image.getScaledInstance(scaledSize, scaledSize,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Getter for the visualization that the plugin creates
     *
     * @param emotionAnalysis data for the visual
     * @return a jpanel containing a visual for the data provided
     * @throws Exception if the visual fails
     */
    @Override
    public JPanel getVisual(EmotionAnalysis emotionAnalysis) throws Exception {
        // Create new JPanel
        JPanel panel = new JPanel();

        // Set initial configurations
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setLayout(new GridBagLayout()); // This ensures that everything is aligned vertically and horizontally
        GridBagConstraints gbc = new GridBagConstraints();

        // Get emotion data
        Map<Emotion, Double> emotionData = emotionAnalysis.getAvgEmotions();

        // Display scaled emojis
        for (Emotion emotion : Emotion.values()) {
            Double value = emotionData.get(emotion);
            value = (double)Math.round(value * 1000d) / 1000d; // Round value to three decimal places

            int scaledSize = calculateScaledSize(value);

            // Only display emojis whose emotion percentages are greater than 0
            if ( scaledSize > 0) {
                if (emotion == Emotion.ANGER) {
                    ImageIcon original = new ImageIcon (ANGRY_FILE);
                    JLabel label = new JLabel (resizeEmojiImage(original, value));
                    panel.add(label, gbc);
                }
                if (emotion == Emotion.DISGUST) {
                    ImageIcon original = new ImageIcon (DISGUST_FILE);
                    JLabel label = new JLabel (resizeEmojiImage(original, value));
                    panel.add(label, gbc);
                }
                if (emotion == Emotion.FEAR) {
                    ImageIcon original = new ImageIcon (FEAR_FILE);
                    JLabel label = new JLabel (resizeEmojiImage(original, value));
                    panel.add(label, gbc);
                }
                if (emotion == Emotion.JOY) {
                    ImageIcon original = new ImageIcon (JOY_FILE);
                    JLabel label = new JLabel (resizeEmojiImage(original, value));
                    panel.add(label, gbc);
                }
                if (emotion == Emotion.SADNESS) {
                    ImageIcon original = new ImageIcon (SADNESS_FILE);
                    JLabel label = new JLabel (resizeEmojiImage(original, value));
                    panel.add(label, gbc);
                }
            }

        }

        return panel;
    }

    @Override
    public void onRegister() {

    }

}
