package edu.cmu.cs.cs214.hw5.core.display;

import edu.cmu.cs.cs214.hw5.core.*;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

/**
 * BarChartPlugin - a display plugin for the GeoData Framework that displays a bar chart given a category
 * type and a value type.
 */
public class BarChartPlugin implements DisplayPlugin {
    /**
     * Name of the plugin.
     */
    private static final String PLUGIN_NAME = "Bar Chart";

    /**
     * X-axis attribute configuration label name.
     */
    private static final String X_AXIS = "X-Axis (Required)";

    /**
     * Y-axis attribute configuration label name.
     */
    private static final String Y_AXIS = "Y-Axis (Required)";

    /**
     * Category configuration label name.
     */
    private static final String CATEGORY = "Category (Required)";

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public List<UserInputConfig> getPluginConfigs(Map<DataType, List<String>> columnPreview) {
        List<UserInputConfig> configs = new ArrayList<>();

        List<String> intLabels = columnPreview.get(DataType.INTEGER);
        List<String> doubleLabels = columnPreview.get(DataType.DOUBLE);
        List<String> stringLabels = columnPreview.get(DataType.STRING);
        List<String> intDoubleLabels = new ArrayList<>(intLabels);
        intDoubleLabels.addAll(doubleLabels);

        configs.add(new UserInputConfig(X_AXIS, UserInputType.SINGLE_SELECTION, intDoubleLabels));
        configs.add(new UserInputConfig(Y_AXIS, UserInputType.SINGLE_SELECTION, intDoubleLabels));
        configs.add(new UserInputConfig(CATEGORY, UserInputType.SINGLE_SELECTION, stringLabels));
        return configs;
    }

    private void checkParams(Map<String, List<String>> pluginParams) {
        if (pluginParams.get(X_AXIS).isEmpty() || pluginParams.get(X_AXIS).get(0) == null)
            throw new IllegalArgumentException("Select X Attribute");
        if (pluginParams.get(Y_AXIS).isEmpty() || pluginParams.get(Y_AXIS).get(0) == null)
            throw new IllegalArgumentException("Select Y Attribute");
        if (pluginParams.get(CATEGORY).isEmpty() || pluginParams.get(CATEGORY).get(0) == null)
            throw new IllegalArgumentException("Select Category Column");
    }

    @Override
    public List<DisplayFilterConfig> getDisplayFilterConfig(Map<String, List<String>> pluginParams) {
        checkParams(pluginParams);
        List<DisplayFilterConfig> configs = new ArrayList<>();
        if (!pluginParams.get(CATEGORY).isEmpty() && pluginParams.get(CATEGORY).get(0) != null) {
            configs.add(new DisplayFilterConfig(pluginParams.get(CATEGORY).get(0), UserInputType.MULTI_SELECTION));
        }
        return configs;
    }

    @Override
    public JPanel draw(DataSet dataSet, int width, int height, Map<String, List<String>> pluginParams) {
        checkParams(pluginParams);

        // get x and y coordinate titles
        String xLabel = pluginParams.get(X_AXIS).get(0);
        String yLabel = pluginParams.get(Y_AXIS).get(0);

        CategoryChart chart = new CategoryChartBuilder()
                .width(width)
                .height(height)
                .xAxisTitle(xLabel)
                .yAxisTitle(yLabel)
                .build();

        // customize chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setPlotGridLinesVisible(false);

        // get x-values and y-values
        final List<Double> xList = dataSet.getColumn(xLabel).stream().map(v -> ((Number) v).doubleValue()).collect(Collectors.toList());
        final List<Double> yList = dataSet.getColumn(yLabel).stream().map(v -> ((Number) v).doubleValue()).collect(Collectors.toList());

        if (yList.size() > 0) {
            chart.addSeries(yLabel, xList, yList);
        }

        return new XChartPanel<>(chart);
    }

}
