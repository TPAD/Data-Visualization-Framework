package edu.cmu.cs.cs214.hw5.core.display;

import edu.cmu.cs.cs214.hw5.core.*;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AreaChartPlugin implements DisplayPlugin {

    // name of the plugin
    private static final String NAME = "Area Graph Plugin";
    // configuration label: x-axis
    private static final String X_AXIS = "X-Axis (Required)";
    // error msg for when user fails to select x-axis attribute
    private static final String X_AXIS_ERROR_MSG = "Select X-Axis Attribute";
    // configuration label: y-axis
    private static final String Y_AXIS = "Y-Axis (Required)";
    // error msg for when user fails to select y-axis attribute
    private static final String Y_AXIS_ERROR_MSG = "Select Y-Axis Attribute";
    // configuration label: category
    private static final String CATEGORY = "Category (Required)";
    // error msg for when user fails to select category attribute
    private static final String CATEGORY_ERROR_MSG = "Select Category Attribute";
    // background color for the legend
    private static final Color LEGEND_BG = new Color(240, 240, 240, 100);


    /**
     * Getter for the name of the plugin
     * @return the name of the plugin
     */
    @Override
    public String getName() {
        return NAME;
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
        configs.add(new UserInputConfig(Y_AXIS, UserInputType.MULTI_SELECTION, intDoubleLabels));
        configs.add(new UserInputConfig(CATEGORY,UserInputType.SINGLE_SELECTION, stringLabels));
        return configs;
    }

    @Override
    public List<DisplayFilterConfig> getDisplayFilterConfig(Map<String, List<String>> pluginParams) {
        verifyParameters(pluginParams);
        String categoryParam = "";
        List<DisplayFilterConfig> configs = new ArrayList<>();
        boolean categoryEmpty = pluginParams.get(CATEGORY).isEmpty();
        if (!categoryEmpty)
            categoryParam = pluginParams.get(CATEGORY).get(0);
        if (!categoryParam.isEmpty()) {
            DisplayFilterConfig displayConfig = new DisplayFilterConfig(categoryParam, UserInputType.MULTI_SELECTION);
            configs.add(displayConfig);
        }
        configs.add(new DisplayFilterConfig(pluginParams.get(X_AXIS).get(0), UserInputType.NONE, true));
        return configs;
    }


    // borrowed some code from the example line chart plugin in Team 30 directory
    @Override
    public JPanel draw(DataSet dataSet, int width, int height, Map<String, List<String>> pluginParams) {
        verifyParameters(pluginParams);

        String xLabel = pluginParams.get(X_AXIS).get(0);
        String categoryLabel = pluginParams.get(CATEGORY).get(0);
        List<String> yLabels = pluginParams.get(Y_AXIS);
        List<List<Double>> yList = new ArrayList<>();
        for (String yLabel : yLabels)
            yList.add(dataSet.getColumn(yLabel).stream().map(v -> ((Number) v).doubleValue()).collect(Collectors.toList()));

        List<Double> xList;
        xList = dataSet.getColumn(xLabel).stream().map(v -> ((Number) v).doubleValue()).collect(Collectors.toList());
        XYChart chart = new XYChartBuilder().width(width).height(height).build();
        if (categoryLabel != null) {
            List<Object> categoryCol = dataSet.getColumn(categoryLabel);
            Map<String, List<Integer>> indices = IntStream.range(0, categoryCol.size()).boxed()
                .collect(Collectors.groupingBy(i -> (String) categoryCol.get(i), Collectors.toList()));
            for (int i = 0; i < yList.size(); i++) {
                for (Map.Entry<String, List<Integer>> entry : indices.entrySet()) {
                    String label = entry.getKey() + ", " + yLabels.get(i);
                    int ii = i;
                    List<Double> y = entry.getValue().stream().map(j -> yList.get(ii).get(j)).collect(Collectors.toList());
                    List<Double> x = entry.getValue().stream().map(xList::get).collect(Collectors.toList());
                    chart.addSeries(label, x, y);
                }
            }
        } else {
            for (int i = 0; i < yList.size(); i++)
                chart.addSeries(yLabels.get(i), xList, yList.get(i));
        }
        XChartPanel<XYChart> chartPanel = new XChartPanel<>(chart);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);
        chart.setXAxisTitle(xLabel);
        chart.setYAxisTitle("Value(s)");
        chart.getStyler().setLegendBackgroundColor(LEGEND_BG);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        return chartPanel;
    }

    /**
     * Throw exception if user does not specify a required input
     * @param pluginParams parameters specified by the user
     */
    private void verifyParameters(Map<String, List<String>> pluginParams) {
        if (pluginParams.get(X_AXIS).isEmpty()) {
            throw new IllegalArgumentException(X_AXIS_ERROR_MSG);
        }
        if (pluginParams.get(Y_AXIS).isEmpty()) {
            throw new IllegalArgumentException(Y_AXIS_ERROR_MSG);
        }
        if (pluginParams.get(CATEGORY).isEmpty()) {
            throw new IllegalArgumentException(CATEGORY_ERROR_MSG);
        }
    }


}
