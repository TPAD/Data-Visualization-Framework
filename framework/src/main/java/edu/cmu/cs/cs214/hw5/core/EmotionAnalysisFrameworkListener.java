package edu.cmu.cs.cs214.hw5.core;

import edu.cmu.cs.cs214.hw5.gui.EmotionAnalysisFrameworkGui;

import javax.swing.*;

/**
 * FrameworkListener - an observer interface that listens for changes in the framework state. The {@link EmotionAnalysisFrameworkImpl}
 * calls these methods to notify the {@link EmotionAnalysisFrameworkGui} when it should update its display.
 */
public interface EmotionAnalysisFrameworkListener {

    /**
     * Called when a new {@link DataPlugin} is registered with the framework
     * @param dp the Data Plugin that has just been registered
     */
    void onDataPluginRegistered(DataPlugin dp);

    /**
     * Called when a new {@link VisualPlugin} is registered with the framework
     * @param vp the Visual Plugin that has just been registered
     */
    void onVisualPluginRegistered(VisualPlugin vp);

    /**
     * Called when the data has been retrieved from source and is ready to be visualized
     */
    void onDataReadyToDisplay();

    /**
     * Called before the framework requests data from plugin so the gui can update accordingly
     */
    void onDataRequest();

    /**
     * Called when the framework is unable to retrieve data from the data plugin
     * @param msg a message to display
     */
    void onCatchLoadException(String msg);

    /**
     * Called when user presses load visual button on the gui
     * @param panel the panel holding the visual to be displayed by the gui
     */
    void onLoadVisualRequest(JPanel panel);

}
