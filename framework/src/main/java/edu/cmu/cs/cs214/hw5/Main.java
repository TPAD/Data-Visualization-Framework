package edu.cmu.cs.cs214.hw5;

import edu.cmu.cs.cs214.hw5.core.DataPlugin;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysisFrameworkImpl;
import edu.cmu.cs.cs214.hw5.core.VisualPlugin;
import edu.cmu.cs.cs214.hw5.gui.EmotionAnalysisFrameworkGui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The main class for running the EmotionAnalysisFramework
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndStartFramework);
    }

    private static void createAndStartFramework() {
        EmotionAnalysisFrameworkImpl core = new EmotionAnalysisFrameworkImpl();
        EmotionAnalysisFrameworkGui gui = new EmotionAnalysisFrameworkGui(core);
        core.setStateChangeListener(gui);

        List<DataPlugin> dataPlugins = loadDataPlugins();
        List<VisualPlugin> visualPlugins = loadVisualPlugins();
        dataPlugins.forEach(core::registerDataPlugin);
        dataPlugins.forEach(DataPlugin::onRegister);
        visualPlugins.forEach(core::registerVisualPlugin);
        visualPlugins.forEach(VisualPlugin::onRegister);
    }

    /**
     * Load Data Plugins listed in META-INF/services/...
     * @return List of instantiated plugins
     */
    private static List<DataPlugin> loadDataPlugins() {
        ServiceLoader<DataPlugin> dataPlugins = ServiceLoader.load(DataPlugin.class);
        List<DataPlugin> result = new ArrayList<>();
        for (DataPlugin plugin : dataPlugins) {
            result.add(plugin);
        }
        return result;
    }

    /**
     * Load Visual Plugins listed in META-INF/services/...
     * @return List of instantiated plugins
     */
    private static List<VisualPlugin> loadVisualPlugins() {
        ServiceLoader<VisualPlugin> visualPlugins = ServiceLoader.load(VisualPlugin.class);
        List<VisualPlugin> result = new ArrayList<>();
        for (VisualPlugin plugin : visualPlugins) {
            result.add(plugin);
        }
        return result;
    }

}
