package edu.cmu.cs.cs214.hw5.gui;

import edu.cmu.cs.cs214.hw5.core.DataPlugin;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysisFrameworkImpl;
import edu.cmu.cs.cs214.hw5.core.EmotionAnalysisFrameworkListener;
import edu.cmu.cs.cs214.hw5.core.VisualPlugin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * FrameworkGui - The framework GUI implementation. This class is responsible for displaying the framework GUI
 * to the screen, and for forwarding events to {@link EmotionAnalysisFrameworkImpl} when GUI-related events are detected
 * (such as button clicks, menu-item clicks, etc.).
 */
public class EmotionAnalysisFrameworkGui implements EmotionAnalysisFrameworkListener {

    private static final String DEFAULT_TITLE = "Text Emotion Analyzer Framework";
    private static final String INPUT_TEXT_DEFAULT = "Input Source: e.g. /path/file.txt";
    // Menu titles
    private static final String MENU_TITLE = "File";
    private static final String MENU_LOAD_DATA_PLUGIN = "Load Data Plugin";
    private static final String MENU_LOAD_VISUAL_PLUGIN = "Load Visual Plugin";
    private static final String MENU_EXIT = "Exit";
    // Button titles
    private static final String BUTTON_LOAD_DATA = "Load Data";
    private static final String BUTTON_LOAD_VISUAL = "Load Visual";
    // Text field titles
    private static final String TEXT_CURRENT_DATA_PLUGIN = "Data Plugin: ";
    private static final String TEXT_CURRENT_VISUAL_PLUGIN = "Visual Plugin: ";
    // Dimensions
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;
    private static final int AREA_WIDTH = 400;
    private static final int AREA_HEIGHT = 50;
    private static final int INPUT_COLUMNS = 20;

    // The parent JFrame window;
    private final JFrame frame;
    // The button for loading data plugin
    private final JButton loadDataButton;
    // The button for loading visual plugin
    private final JButton loadVisualButton;
    // Text displaying current data plugin
    private final JLabel currentDataPluginText;
    // Text displaying current visual plugin
    private final JLabel currentVisualPluginText;
    private final JTextArea instructionsText;
    private final JTextField sourceInputField;

    private final JMenu dataPluginMenu;
    private final JMenu visualPluginMenu;

    private EmotionAnalysisFrameworkImpl core;
    private DataPlugin currentDataPlugin;
    private VisualPlugin currentVisualPlugin;

    public EmotionAnalysisFrameworkGui(EmotionAnalysisFrameworkImpl fc) {
        frame = new JFrame(DEFAULT_TITLE);
        // parent of panel displaying instructions, input textfield, and load buttons
        JPanel contentPane = new JPanel();
        // parent of label displaying current visual plugin
        JPanel headerPane = new JPanel(new BorderLayout());
        // parent of label displaying current data plugin
        JPanel footerPane = new JPanel(new BorderLayout());
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        instructionsText = new JTextArea(INPUT_TEXT_DEFAULT);
        sourceInputField = new JTextField(INPUT_COLUMNS);

        // footers setup
        currentDataPluginText = new JLabel(" ");
        currentVisualPluginText = new JLabel(" ");
        headerPane.add(currentDataPluginText, BorderLayout.NORTH);
        footerPane.add(currentVisualPluginText, BorderLayout.SOUTH);

        instructionsText.setPreferredSize(new Dimension(AREA_WIDTH, AREA_HEIGHT));
        instructionsText.setText(INPUT_TEXT_DEFAULT);
        instructionsText.setOpaque(false);
        instructionsText.setEditable(false);
        instructionsText.setFocusable(false);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setLineWrap(true);

        loadDataButton = new JButton(BUTTON_LOAD_DATA);
        loadVisualButton = new JButton(BUTTON_LOAD_VISUAL);
        loadDataButton.setEnabled(false);
        loadDataButton.addActionListener(event -> {
            String[] splitByWhiteSpace = sourceInputField.getText().split(" ");
            core.requestDataFromSource(Arrays.asList(splitByWhiteSpace));
        });
        loadVisualButton.setEnabled(false);
        loadVisualButton.addActionListener(event -> {
            core.requestVisualizeData();
        });
        buttonsPanel.add(loadDataButton);
        buttonsPanel.add(loadVisualButton);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(instructionsText);
        contentPane.add(sourceInputField);
        contentPane.add(buttonsPanel);

        // background panel boilerplate
        Dimension dim = new Dimension(WIDTH, HEIGHT);
        JPanel background = new JPanel();
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));
        background.setPreferredSize(dim);
        background.add(headerPane);
        background.add(contentPane);
        background.add(footerPane);

        core = fc;
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu(MENU_TITLE);
        JMenuItem exitMenuItem = new JMenuItem(MENU_EXIT);
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(event -> System.exit(0));
        dataPluginMenu = new JMenu(MENU_LOAD_DATA_PLUGIN);
        visualPluginMenu = new JMenu(MENU_LOAD_VISUAL_PLUGIN);
        fileMenu.add(dataPluginMenu);
        fileMenu.add(visualPluginMenu);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(dim);
        frame.setContentPane(background);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }

    private static void showDialog(Component component, String title, String message) {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onDataRequest() {
        loadDataButton.setEnabled(false);
    }

    /**
     * Invoked by framework when the data has been loaded
     */
    @Override
    public void onDataReadyToDisplay() {
        loadVisualButton.setEnabled(true);
        loadDataButton.setEnabled(true);
    }

    /**
     * Creates the menu item for each of the data plugins registered to the framework. Sets the current data plugin
     * when the corresponding menu item is selected.
     * @param plugin the data plugin that was registered
     */
    @Override
    public void onDataPluginRegistered(DataPlugin plugin) {
        String pluginName = plugin.getName();
        JMenuItem pluginMenuItem = new JMenuItem(pluginName);
        dataPluginMenu.add(pluginMenuItem);
        pluginMenuItem.addActionListener(event -> {
            currentDataPlugin = plugin;
            currentDataPluginText.setText(TEXT_CURRENT_DATA_PLUGIN + pluginName);
            loadDataButton.setEnabled(true);
            instructionsText.setText(plugin.getDescription());
            core.setCurrentDataPlugin(plugin);
            frame.revalidate();
        });
    }

    /**
     * Creates menu item for each visual plugins registered to the framework. Sets the current visual plugin when
     * the corresponding menu item is selected.
     * @param plugin the visual plugin that was registered to the framework
     */
    @Override
    public void onVisualPluginRegistered(VisualPlugin plugin) {
        String pluginName = plugin.getName();
        JMenuItem pluginMenuItem = new JMenuItem(pluginName);
        visualPluginMenu.add(pluginMenuItem);
        pluginMenuItem.addActionListener(event -> {
            currentVisualPlugin = plugin;
            currentVisualPluginText.setText(TEXT_CURRENT_VISUAL_PLUGIN + pluginName);
            core.setCurrentVisualPlugin(plugin);
            frame.revalidate();
        });
    }

    /**
     * Called when data from the data plugin could not be retrieved
     */
    @Override
    public void onCatchLoadException(String msg) {
        String title = "Failed to Load Plugin";
        JFrame frame = (JFrame) SwingUtilities.getRoot(this.frame);
        showDialog(frame, title, msg + " \nRe-select plugins");
        if (loadDataButton.isEnabled()) {
            loadDataButton.setEnabled(false);
        }
        if (loadVisualButton.isEnabled()) {
            loadVisualButton.setEnabled(false);
        }
    }

    /**
     * Called when the user presses load visual button
     * @param panel the panel holding the visual to be displayed by the gui
     */
    @Override
    public void onLoadVisualRequest(JPanel panel) {
        String title = (currentVisualPlugin != null) ? currentVisualPlugin.getName() : "";
        JFrame visualFrame = new JFrame(title);
        visualFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        visualFrame.setContentPane(panel);
        visualFrame.pack();
        visualFrame.setVisible(true);
    }
}
