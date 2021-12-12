package edu.cmu.cs.cs214.hw5.core.display;

import edu.cmu.cs.cs214.hw5.core.DataSet;
import edu.cmu.cs.cs214.hw5.core.DataType;
import edu.cmu.cs.cs214.hw5.core.DisplayFilterConfig;
import edu.cmu.cs.cs214.hw5.core.DisplayPlugin;
import edu.cmu.cs.cs214.hw5.core.UserInputConfig;
import edu.cmu.cs.cs214.hw5.core.UserInputType;
import org.knowm.xchart.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PieChartPlugin - a display plugin for the GeoData Framework. This plugin displays a pie chart given a category
 * type and a value type.
 */
public class PieChartPlugin implements DisplayPlugin {
    private static final String NAME = "Pie Chart Plugin";
    private static final String CATEGORY = "Category";
    private static final String VALUE = "Value";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserInputConfig> getPluginConfigs(Map<DataType, List<String>> columnPreview) {
        List<UserInputConfig> configs = new ArrayList<>();
        List<String> stringLabels = columnPreview.get(DataType.STRING);
        List<String> numLabels = columnPreview.get(DataType.INTEGER); // number label can be either an int or double
        numLabels.addAll(columnPreview.get(DataType.DOUBLE));

        configs.add(new UserInputConfig(VALUE, UserInputType.SINGLE_SELECTION, numLabels));
        configs.add(new UserInputConfig(CATEGORY, UserInputType.SINGLE_SELECTION, stringLabels));
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DisplayFilterConfig> getDisplayFilterConfig(Map<String, List<String>> pluginParams) {
        // Let the user filter which categories are displayed
        checkParams(pluginParams);
        List<DisplayFilterConfig> configs = new ArrayList<>();
        configs.add(new DisplayFilterConfig(pluginParams.get(CATEGORY).get(0), UserInputType.MULTI_SELECTION));
        return configs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JPanel draw(DataSet dataset, int width, int height, Map<String, List<String>> pluginParams) {
        checkParams(pluginParams);
        String valueLabel = pluginParams.get(VALUE).get(0);
        String categoryLabel = pluginParams.get(CATEGORY).get(0);

        PieChart chart = new PieChartBuilder().width(width).height(height).build();
        List<Object> values = dataset.getColumn(valueLabel);
        List<Object> categories = dataset.getColumn(categoryLabel);
        Map<String, Number> data = new HashMap<>();

        // Combine data to get rid of duplicate categories
        for(int i = 0; i < values.size(); i++) {
            String category = (String) categories.get(i);
            Number value = (Number) values.get(i);

            Number prev = data.getOrDefault(category, 0.0);
            data.put(category, value.doubleValue() + prev.doubleValue());
        }

        // Add category/value pairs to pie chart
        for(Map.Entry<String, Number> entry : data.entrySet()) {
            chart.addSeries(entry.getKey(), entry.getValue());
        }

        return new XChartPanel<>(chart);
    }

    private void checkParams(Map<String, List<String>> pluginParams) {
        // if the value or category fields are empty then throw exception
        if (pluginParams.get(VALUE).isEmpty() || pluginParams.get(VALUE).get(0) == null)
            throw new IllegalArgumentException("Select Value");
        if (pluginParams.get(CATEGORY).isEmpty() || pluginParams.get(CATEGORY).get(0) == null)
            throw new IllegalArgumentException("Select Category");
    }
}
