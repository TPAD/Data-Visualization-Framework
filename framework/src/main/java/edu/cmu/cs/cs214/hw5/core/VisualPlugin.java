package edu.cmu.cs.cs214.hw5.core;

import javax.swing.JPanel;

/**
 * VisualPlugin - interface for visual plugins
 */
public interface VisualPlugin {

    /**
     * Getter for the visual plugin's name
     * @return the name of the visual plugin
     */
    String getName();

    /**
     * Allows the plugin to do any required setup when it is registered
     */
    void onRegister();

    /**
     * Getter for the visualization that the plugin creates
     * @param emotionAnalysis data for the visual
     * @return a jpanel containing a visual for the data provided
     * @throws Exception if the visual fails
     */
    JPanel getVisual(EmotionAnalysis emotionAnalysis) throws Exception;


}
