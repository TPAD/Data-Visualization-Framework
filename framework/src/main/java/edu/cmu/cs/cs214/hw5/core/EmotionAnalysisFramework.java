package edu.cmu.cs.cs214.hw5.core;

/**
 * EmotionAnalysisFramework - Text Emotion Analysis framework interface
 */
public interface EmotionAnalysisFramework {

    /**
     * Register framework listener
     * @param listener a listener that responds to changes in the framework
     */
    void setStateChangeListener(EmotionAnalysisFrameworkListener listener);

    /**
     * Method invoked for some action to be taken as soon as a {@link DataPlugin} is registered
     * @param plugin the data plugin to register
     */
    void registerDataPlugin(DataPlugin plugin);

    /**
     * Set the current data plugin
     * @param plugin the data plugin most recently selected by the framework gui
     */
    void setCurrentDataPlugin(DataPlugin plugin);

    /**
     * Set the current visual plugin
     * @param plugin the visual plugin most recently selected by the framework gui
     */
    void setCurrentVisualPlugin(VisualPlugin plugin);

    /**
     * Method invoked for some action to be taken as soon as a {@link VisualPlugin} is registered
     * @param plugin the visual plugin to register
     */
    void registerVisualPlugin(VisualPlugin plugin);


}
